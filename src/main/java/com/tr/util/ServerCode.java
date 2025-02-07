package com.tr.util;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 *  @author Asen
  * 框架级通用的错误代码
 */
@Getter
@AllArgsConstructor
public enum ServerCode {
	/** 状态码 */
	SUCCESS(200,"成功!"),
    TOKEN_EXPIRED(401, "token过期!"),
	UNAUTHORIZED(403, "未被授权的，没有权限!"),
    PARAMS_ERROR_CODE(400,"请求参数异常!"),
    ERROR_CODE(500,"系统内部错误!"),
    ERROR(10000,"失败!"),
    REFRESH_TOKEN_EXPIRED(402, "refreshToken过期!"),
    VALUE_NO_FOUND(10001,"未查询到信息!"),
    NETWORK_REQUEST_ERROR(10002,"网络繁忙，请稍后重试"),
    NETWORK_OR_DATA_ERROR(10003,"网络连接或数据操作失败"),
    OVER_LIMIT(10004, "使用太频繁啦"),
    ;

    /** 返回码 */
    private Integer code;
    /** 返回描述 */
    private String message;

}
