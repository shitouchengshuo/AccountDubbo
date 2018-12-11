package com.qiqi.account.exception;

import com.qiqi.account.errorCode.ReturnStatusCode;
import org.apache.shiro.authc.AuthenticationException;

public class AccountIsUnActivedException extends Exception implements ExceptionInterface {
	private static final long serialVersionUID = 1L;
	private String reason;

	public AccountIsUnActivedException() {
		super();
	}

	public AccountIsUnActivedException(String message) {
		super(message);
	}

	public AccountIsUnActivedException(String message, String reason) {
		super(message);
		this.reason = reason;
	}

	public AccountIsUnActivedException(String message, Throwable cause) {
		super(message, cause);
	}

	@Override
	public String getErrorCode() {
		return ReturnStatusCode.AccountNotActive.getErrorCode();
	}

	@Override
	public String getReason() {
		return this.reason;
	}

	@Override
	public String getMessage() {
		return ReturnStatusCode.AccountNotActive.getErrorInfo();
	}

}
