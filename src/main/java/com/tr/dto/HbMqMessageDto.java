package com.tr.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class HbMqMessageDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户的ID
     */
    private Long userId;

    /**
     * 金额
     */
    private BigDecimal amount;

    /**
     * 红包的ID
     */
    private Long redEnvelopeId;
}
