package com.qiqi.account.exception;


import com.qiqi.account.errorCode.ReturnStatusCode;

public class AccountNameAlreadyExistException extends Exception implements ExceptionInterface {
	private static final long serialVersionUID = 1L;
	private String reason;

	public AccountNameAlreadyExistException() {
		super();
	}
	
	public AccountNameAlreadyExistException(String message) {
		super(message);
	}
	
	public AccountNameAlreadyExistException(String message, String reason) {
		super(message);
		this.reason = reason;
	}
	
	public AccountNameAlreadyExistException(String message, Throwable cause) {
		super(message, cause);
	}

	@Override
	public String getErrorCode() {
		return ReturnStatusCode.AccountNameAlreadyExist.getErrorCode();
	}
	
	@Override
	public String getReason() {
		return this.reason;
	}
	
	@Override
	public String getMessage() {
		return ReturnStatusCode.AccountNameAlreadyExist.getErrorInfo();
	}
}
