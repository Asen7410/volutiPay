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
 * 用户领取红包记录表
 */
@TableName("use_red_envelope_record")
@Data
public class UseRedEnvelopeRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 红包id
     */
    private Long redEnvelopeId;

    /**
     * 用户领取红包金额
     */
    private BigDecimal amount;

    /**
     * 用户领取红包之前的金额
     */
    private BigDecimal beferAmount;

    /**
     * 用户领取红包后的金额
     */
    private BigDecimal afterAmount;

    /**
     * 创建人
     */
    private String creator;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 领取红包的流水ID(red_envelope_record的ID)
     */
    private Long receiveRecordId;
}
