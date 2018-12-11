package com.qiqi.account.exception;

import com.qiqi.account.errorCode.ReturnStatusCode;

public class AccountPhonenumberAlreadyExistException extends Exception implements ExceptionInterface {
	private static final long serialVersionUID = 1L;
	private String reason;

	public AccountPhonenumberAlreadyExistException() {
		super();
	}
	
	public AccountPhonenumberAlreadyExistException(String message) {
		super(message);
	}
	
	public AccountPhonenumberAlreadyExistException(String message, String reason) {
		super(message);
		this.reason = reason;
	}
	
	public AccountPhonenumberAlreadyExistException(String message, Throwable cause) {
		super(message, cause);
	}

	@Override
	public String getErrorCode() {
		return ReturnStatusCode.AccountPhonenumberAlreadyExist.getErrorCode();
	}
	
	@Override
	public String getReason() {
		return this.reason;
	}
	
	@Override
	public String getMessage() {
		return ReturnStatusCode.AccountPhonenumberAlreadyExist.getErrorInfo();
	}
}
