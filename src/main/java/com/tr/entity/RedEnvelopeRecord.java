package com.tr.entity;
import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;

import java.time.LocalDateTime;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
/**
 * 红包领取记录表
 */
@TableName("red_envelope_record")
@Data
public class RedEnvelopeRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 红包的ID
     */
    private Long redEnvelopeId;

    /**
     * 扣减前的金额
     */
    private BigDecimal beforeAmount;

    /**
     * 扣减的金额
     */
    private BigDecimal amount;

    /**
     * 扣减后的金额
     */
    private BigDecimal afterAmount;

    /**
     * 领取前的次数（红包可领取次数）
     */
    private Integer beforeNum;

    /**
     * 领取后的次数（红包可领取次数）
     */
    private Integer afterNum;

    /**
     * 创建时间
     */
    private Date createTime;

}
