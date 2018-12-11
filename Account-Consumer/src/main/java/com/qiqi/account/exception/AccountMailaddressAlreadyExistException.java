package com.qiqi.account.exception;


import com.qiqi.account.errorCode.ReturnStatusCode;

public class AccountMailaddressAlreadyExistException extends Exception implements ExceptionInterface {
	private static final long serialVersionUID = 1L;
	private String reason;

	public AccountMailaddressAlreadyExistException() {
		super();
	}
	
	public AccountMailaddressAlreadyExistException(String message) {
		super(message);
	}
	
	public AccountMailaddressAlreadyExistException(String message, String reason) {
		super(message);
		this.reason = reason;
	}
	
	public AccountMailaddressAlreadyExistException(String message, Throwable cause) {
		super(message, cause);
	}

	@Override
	public String getErrorCode() {
		return ReturnStatusCode.AccountMailaddressAlreadyExist.getErrorCode();
	}
	
	@Override
	public String getReason() {
		return this.reason;
	}
	
	@Override
	public String getMessage() {
		return ReturnStatusCode.AccountMailaddressAlreadyExist.getErrorInfo();
	}
}
