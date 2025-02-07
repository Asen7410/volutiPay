package com.tr.service;

import com.tr.entity.RedEnvelopeInfo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.tr.util.CommonResponse;
import com.tr.util.ServerCode;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * <p>
 * 红包信息表 服务类
 * </p>
 *
 * @author Asen
 * @since 2025-01-02
 */
public interface RedEnvelopeInfoService extends IService<RedEnvelopeInfo> {

    @Transactional
    CommonResponse grabRedEnvelope(Long userId, long hbId);

    void updateInfo(Long userId, Long redEnvelopeId, BigDecimal amount);
}
