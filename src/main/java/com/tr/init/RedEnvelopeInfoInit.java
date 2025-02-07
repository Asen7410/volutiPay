package com.tr.init;

import cn.hutool.core.collection.CollectionUtil;
import com.tr.entity.RedEnvelopeInfo;
import com.tr.service.RedEnvelopeInfoService;
import com.tr.util.Constants;
import com.tr.util.RedEnvelopeUtils;
import com.tr.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;

@Component
@Slf4j
public class RedEnvelopeInfoInit implements CommandLineRunner {

    @Resource
    private RedEnvelopeInfoService redEnvelopeInfoService;

    @Override
    public void run(String... args) throws Exception {
        List<RedEnvelopeInfo> redEnvelopeInfoList = redEnvelopeInfoService.list();
        if(CollectionUtil.isNotEmpty(redEnvelopeInfoList)){
            for(RedEnvelopeInfo redEnvelopeInfo : redEnvelopeInfoList){
                //红包id
                Long id = redEnvelopeInfo.getId();
                String key = String.format(Constants.RED_ENVELOPE_INFO,id);
                String amountKey = String.format(Constants.RED_ENVELOPE_AMOUNTS, id);
                RedEnvelopeInfo redEnvelopeInfoValue = RedisUtil.get(key,RedEnvelopeInfo.class);
                if(redEnvelopeInfoValue == null){
                    RedisUtil.set(key,redEnvelopeInfo);
                    // 随机生成金额列表
                    List<BigDecimal> amounts = RedEnvelopeUtils.allocateRedEnvelopes(redEnvelopeInfo.getRemainingAmount(), redEnvelopeInfo.getRemainingNum());
                    log.info("amounts is {},totalAmount is {}",amounts,amounts.stream()
                            .reduce(BigDecimal.ZERO, BigDecimal::add));
                    // 将金额列表存入 Redis
                    RedisUtil.lpushAll(amountKey, amounts.stream().map(String::valueOf).toArray(String[]::new));
                }
            }
        }
    }
}
