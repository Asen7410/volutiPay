package com.tr.exception;


import com.tr.util.ServerCode;

/**
  * @return
 */
public class ServerException extends RuntimeException {
    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    private Integer code;

	public ServerException() {
        super();
    }
    public ServerException(String message) { super(message); }

    public ServerException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    public ServerException(ServerCode memberErrorCode) {
    	super(memberErrorCode.getMessage());
    	this.code = memberErrorCode.getCode();
    }
}
