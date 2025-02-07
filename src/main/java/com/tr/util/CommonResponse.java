package com.tr.util;

import java.io.Serializable;

/**
 * @author Asen
 * 返回客户端 response
 * @ClassName: CommonResponse
 *
 */
public class CommonResponse<T> implements Serializable {
	private static final long serialVersionUID = -1L;

	/**
	 * 返回代码
	 */
	private Integer code;
	/**
	 * 失败消息
	 */
	private String message;
	/**
	 * 时间戳
	 */
	private long timestamp;
	/**
	 * 成功标志
	 */
	private boolean success;
	/**
	 * 结果对象
	 */
	private T data;

	public static <T> CommonResponse<T> success(){
		CommonResponse<T> commonResponse = new CommonResponse<>();
		commonResponse.code = 200;
		commonResponse.data = null;
		commonResponse.success = true;
		return commonResponse;
	}

	/**
	 * 返回成功
	 */
	public static <T> CommonResponse<T> success(T resp){
		CommonResponse<T> commonResponse = new CommonResponse<>();
		commonResponse.code = 200;
		commonResponse.data = resp;
		commonResponse.success = true;
		return commonResponse;
	}

	/**
	 * 返回成功
	 */
	public static <T> CommonResponse<T> buildRespose4Success(T resp, String msg){
		CommonResponse<T> commonResponse = new CommonResponse<>();
		commonResponse.code = 200;
		commonResponse.data = resp;
		commonResponse.success = true;
		commonResponse.message = msg;

		return commonResponse;
	}

	/**
	 * 返回失败
	 */
	public static <T> CommonResponse<T> buildRespose4Fail(Integer errorCode, String error){
		CommonResponse<T> commonResponse = new CommonResponse<>();
		commonResponse.code = errorCode;
		commonResponse.success = false;
		commonResponse.message = error;

		return commonResponse;
	}


	public static <T> CommonResponse<T> buildResponse(T resp, Integer errorCode, String error){
		CommonResponse<T> commonResponse = new CommonResponse<>();
		commonResponse.code = errorCode;
		commonResponse.data = resp;
		commonResponse.success = false;
		commonResponse.message = error;

		return commonResponse;
	}

	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}
}