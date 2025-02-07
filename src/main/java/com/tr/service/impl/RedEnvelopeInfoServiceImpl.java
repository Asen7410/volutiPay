package com.tr.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tr.dto.HbMqMessageDto;
import com.tr.entity.RedEnvelopeInfo;
import com.tr.entity.RedEnvelopeRecord;
import com.tr.entity.UseRedEnvelopeRecord;
import com.tr.entity.User;
import com.tr.enums.RedEnvelopeEnum;
import com.tr.enums.ServiceCodeEnum;
import com.tr.exception.ServerException;
import com.tr.mapper.RedEnvelopeInfoMapper;
import com.tr.service.*;
import com.tr.util.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 红包信息表 服务实现类
 * </p>
 *
 * @author Asen
 * @since 2025-01-02
 */
@Service
@Slf4j
public class RedEnvelopeInfoServiceImpl extends ServiceImpl<RedEnvelopeInfoMapper, RedEnvelopeInfo> implements RedEnvelopeInfoService {

    @Autowired
    private RabbitMQSender rabbitMQSender;

    @Autowired
    private RedEnvelopeRecordService redEnvelopeRecordService;

    @Autowired
    private UserService userService;

    @Autowired
    private UseRedEnvelopeRecordService useRedEnvelopeRecordService;

    @Override
    public CommonResponse grabRedEnvelope(Long userId, long hbId) {
        // 先检测用户是否抢过该红包 redis set
        String userKey = String.format(Constants.RED_ENVELOPE_USER_KEY_FORMAT, hbId);
        if (RedisUtil.isMemberOfSet(userKey, userId)) {
            return CommonResponse.buildRespose4Fail(ServiceCodeEnum.ALREADY_USER_RECEIVED.getCode(),"该账号以抢过该红包！");
        }

        // 获取红包信息
        RedEnvelopeInfo redEnvelopeInfo = getRedEnvelopeInfo(hbId);
        if (redEnvelopeInfo == null || redEnvelopeInfo.getStatus().equals(RedEnvelopeEnum.END.getCode())) {
            return CommonResponse.buildRespose4Fail(ServiceCodeEnum.RED_ENVELOPE_NOT_FOUND.getCode(),"红包不存在！");
        }

        // 加锁操作，确保红包操作的线程安全
        String redEnvelopeLock = String.format(Constants.RED_ENVELOPE_LOCK, hbId);
        if (!acquireLock(redEnvelopeLock)) {
            throw new ServerException(ServiceCodeEnum.LOCK_IS_ERR.getCode(), "获取红包锁失败");
        }

        try {
            // 生成随机红包金额并更新红包状态
            BigDecimal amount = grabRedEnvelope(hbId);

            updateRedEnvelopeInfo(redEnvelopeInfo, amount);

            // 更新Redis缓存
            RedisUtil.set(String.format(Constants.RED_ENVELOPE_INFO, hbId), redEnvelopeInfo);

            // 8. 标记用户已抢该红包
            RedisUtil.addToSet(userKey, userId);

            // 封装消息并发送MQ
            sendRedEnvelopeMessage(userId, hbId, amount);

            return CommonResponse.buildRespose4Success(ServiceCodeEnum.SUCCESS.getCode(),"抢红包成功！");
        } finally {
            // 释放锁
            RedisUtil.delete(redEnvelopeLock);
        }
    }

    @Override
    public void updateInfo(Long userId, Long redEnvelopeId, BigDecimal amount) {
        try {
            // 获取用户和红包信息，若不存在则抛出异常
            User user = getUserOrThrow(userId);
            RedEnvelopeInfo info = getRedEnvelopeInfoOrThrow(redEnvelopeId);

            RedEnvelopeRecord record = createRedEnvelopeRecord(info, amount);

            // 更新红包信息
            updateRedEnvelopeInfo(info, amount);

            // 保存更新后的红包信息
            updateById(info);

            // 保存红包记录
            redEnvelopeRecordService.save(record);

            // 保存用户的红包使用记录
            saveUseRedEnvelopeRecord(user, amount, record);

            //更新用户的金额
            user.setAmount(user.getAmount().add(amount));
            user.setUpdateTime(new Date());
            userService.updateById(user);

        } catch (RuntimeException e) {
            // 捕获用户和红包信息未找到的异常并记录日志
            log.error("消息消费失败: 用户或红包信息未找到, userId: {}, redEnvelopeId: {}", userId, redEnvelopeId, e);
            throw new ServerException(ServiceCodeEnum.ERROR_CODE.getCode(), "红包更新失败");
        } catch (Exception e) {
            // 捕获其他未知异常
            log.error("消息消费失败: 未知错误, userId: {}, redEnvelopeId: {}", userId, redEnvelopeId, e);
            throw new ServerException(ServiceCodeEnum.ERROR_CODE.getCode(), "红包更新失败");
        }
    }

    // 获取用户方法
    private User getUserOrThrow(Long userId) {
        User user = userService.getById(userId);
        if (ObjectUtils.isEmpty(user)) {
            throw new ServerException(ServiceCodeEnum.USER_NOT_FOUND.getCode(), "用户未找到");
        }
        return user;
    }

    // 获取红包信息方法
    private RedEnvelopeInfo getRedEnvelopeInfoOrThrow(Long redEnvelopeId) {
        RedEnvelopeInfo info = getById(redEnvelopeId);
        if (ObjectUtils.isEmpty(info)) {
            throw new ServerException(ServiceCodeEnum.RED_ENVELOPE_NOT_FOUND.getCode(), "红包未找到");
        }
        return info;
    }

    // 创建红包记录的方法
    private RedEnvelopeRecord createRedEnvelopeRecord(RedEnvelopeInfo info, BigDecimal amount) {
        RedEnvelopeRecord record = new RedEnvelopeRecord();
        record.setRedEnvelopeId(info.getId());
        record.setBeforeAmount(info.getRemainingAmount());
        record.setBeforeNum(info.getRemainingNum());
        record.setAmount(amount);
        record.setAfterAmount(info.getRemainingAmount().subtract(amount)); // 更新剩余金额
        record.setAfterNum(info.getRemainingNum() - 1); // 更新已用数量
        record.setCreateTime(new Date());
        return record;
    }

    // 保存用户的红包使用记录
    private void saveUseRedEnvelopeRecord(User user, BigDecimal amount, RedEnvelopeRecord record) {
        try {
            UseRedEnvelopeRecord envelopeRecord = new UseRedEnvelopeRecord();
            envelopeRecord.setRedEnvelopeId(record.getRedEnvelopeId());
            envelopeRecord.setCreator(user.getNickName());
            envelopeRecord.setCreateTime(new Date());
            envelopeRecord.setAmount(amount);
            envelopeRecord.setBeferAmount(user.getAmount());
            envelopeRecord.setAfterAmount(user.getAmount() == null? BigDecimal.ZERO : user.getAmount().add(amount)); // 更新后余额
            envelopeRecord.setReceiveRecordId(record.getId());
            useRedEnvelopeRecordService.save(envelopeRecord);
        } catch (Exception e) {
            // 捕获保存用户红包记录的异常并记录日志
            log.error("保存用户红包记录失败, userId: {}, amount: {}, redEnvelopeRecordId: {}", user.getId(), amount, record.getId(), e);
            throw new ServerException(ServiceCodeEnum.ERROR_CODE.getCode(), "保存红包记录失败");
        }
    }


    private RedEnvelopeInfo getRedEnvelopeInfo(long hbId) {
        // 获取redis中的红包信息，如果不存在则从数据库获取
        String redEnvelopeKey = String.format(Constants.RED_ENVELOPE_INFO, hbId);
        RedEnvelopeInfo redEnvelopeInfo = RedisUtil.get(redEnvelopeKey, RedEnvelopeInfo.class);
        if (ObjectUtils.isEmpty(redEnvelopeInfo)) {
            redEnvelopeInfo = this.getById(hbId);
            if (ObjectUtils.isEmpty(redEnvelopeInfo)) {
                return null; // 红包不存在
            }
            List<BigDecimal> amounts = RedEnvelopeUtils.allocateRedEnvelopes(redEnvelopeInfo.getRemainingAmount(), redEnvelopeInfo.getRemainingNum());
            log.info("amounts is {},totalAmount is {}",amounts,amounts.stream()
                    .reduce(BigDecimal.ZERO, BigDecimal::add));

            String amountKey = String.format(Constants.RED_ENVELOPE_AMOUNTS, hbId);
            // 将金额列表存入 Redis
            RedisUtil.lpushAll(amountKey, amounts.stream().map(String::valueOf).toArray(String[]::new));
            // Redis缓存
            RedisUtil.set(redEnvelopeKey, redEnvelopeInfo);
        }
        return redEnvelopeInfo;
    }

    private boolean acquireLock(String lockKey) {
        // 尝试获取锁
        boolean isLockAcquired = RedisUtil.setIfAbsent(lockKey, "locked");
        if (isLockAcquired) {
            // 设置锁的过期时间为10分钟
            RedisUtil.setExpire(lockKey, 10, TimeUnit.MINUTES);
        }
        return isLockAcquired;
    }

    private void updateRedEnvelopeInfo(RedEnvelopeInfo redEnvelopeInfo, BigDecimal amount) {
        BigDecimal remainingAmount = redEnvelopeInfo.getRemainingAmount().subtract(amount);
        redEnvelopeInfo.setRemainingAmount(remainingAmount); // 剩余红包金额 - 此次领取的金额
        redEnvelopeInfo.setRemainingNum(redEnvelopeInfo.getRemainingNum() - 1); // 剩余数量 - 1
        redEnvelopeInfo.setUseAmount(redEnvelopeInfo.getUseAmount().add(amount)); // 已抢红包金额 + 本次领取金额
        redEnvelopeInfo.setUseNum(redEnvelopeInfo.getUseNum() + 1); // 已抢数量 + 1

        // 检查红包是否开始或结束
        if (redEnvelopeInfo.getRemainingAmount().compareTo(redEnvelopeInfo.getTotalAmount()) < 0) {
            redEnvelopeInfo.setStatus(RedEnvelopeEnum.STARTED.getCode());
        }
        if (redEnvelopeInfo.getRemainingNum() == 0 || redEnvelopeInfo.getRemainingAmount().compareTo(BigDecimal.ZERO) == 0) {
            redEnvelopeInfo.setStatus(RedEnvelopeEnum.END.getCode());
        }
    }

    private void sendRedEnvelopeMessage(Long userId, Long hbId, BigDecimal amount) {
        // 封装消息，发送MQ
        HbMqMessageDto messageDto = new HbMqMessageDto();
        messageDto.setUserId(userId);
        messageDto.setRedEnvelopeId(hbId);
        messageDto.setAmount(amount);
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String messageJson = objectMapper.writeValueAsString(messageDto);
            rabbitMQSender.sendMessage(messageJson);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("消息序列化失败", e);
        }
    }

    public BigDecimal grabRedEnvelope(Long redEnvelopeId) {
        String amountKey = String.format(Constants.RED_ENVELOPE_AMOUNTS, redEnvelopeId);

        // 从 Redis 列表中弹出一个随机金额
        String amountStr = RedisUtil.lpop(amountKey);
        if (StringUtils.isEmpty(amountStr)) {
            throw new RuntimeException("红包已抢完");
        }

        return new BigDecimal(amountStr);
    }
}
