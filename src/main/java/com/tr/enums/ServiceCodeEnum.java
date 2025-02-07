package com.tr.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public enum ServiceCodeEnum {
	/** 状态码 */
	SUCCESS(200,"成功!"),
    ERROR_CODE(500,"系统内部错误!"),
    USER_NOT_FOUND(10005, "用户未找到"),
    RED_ENVELOPE_NOT_FOUND(10006, "红包未找到"),
    ALREADY_USER_RECEIVED(10007, "该账号已经抢过红包"),
    LOCK_IS_ERR(10008, "加锁失败"),
    RED_IS_END(10009, "红包已结束"),
    ;

    /** 返回码 */
    private Integer code;
    /** 返回描述 */
    private String message;

}
