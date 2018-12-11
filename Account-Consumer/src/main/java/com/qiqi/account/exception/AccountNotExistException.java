package com.qiqi.account.exception;

import com.qiqi.account.errorCode.ReturnStatusCode;
import org.apache.shiro.authc.AuthenticationException;

public class AccountNotExistException extends AuthenticationException implements ExceptionInterface{
	private static final long serialVersionUID = 1L;
	private String reason;

	public AccountNotExistException() {
		super();
	}
	
	public AccountNotExistException(String message) {
		super(message);
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
