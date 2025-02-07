package com.tr.entity;
import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;

import java.time.LocalDateTime;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
/**
 * 红包信息表
 */
@TableName("red_envelope_info")
@Data
public class RedEnvelopeInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 红包金额(元)
     */
    private BigDecimal totalAmount;

    /**
     * 已抢红包金额(元)
     */
    private BigDecimal useAmount;

    /**
     * 剩余红包金额(元)
     */
    private BigDecimal remainingAmount;

    /**
     * 红包总数量(个)
     */
    private Integer totalNum;

    /**
     * 已抢数量(个)
     */
    private Integer useNum;

    /**
     * 剩余数量(个)
     */
    private Integer remainingNum;

    /**
     * 状态 默认0(没人抢)，1-进行中 2-已抢完
     */
    private Integer status;

    /**
     * 创建人
     */
    private String creator;

    /**
     * 创建时间
     */
    private Date createTime;
}
