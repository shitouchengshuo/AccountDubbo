package com.qiqi.account.exception;

import com.qiqi.account.errorCode.ReturnStatusCode;

public class VerificationCodeErrorException extends Exception implements ExceptionInterface {
	private static final long serialVersionUID = 1L;
	private String reason;

	public VerificationCodeErrorException() {
		super();
	}
	
	public VerificationCodeErrorException(String reason) {
		this.reason = reason;
	}
	
	public VerificationCodeErrorException(String message, String reason) {
		super(message);
		this.reason = reason;
	}
	
	public VerificationCodeErrorException(String message, Throwable cause) {
		super(message, cause);
	}

	@Override
	public String getErrorCode() {
		return ReturnStatusCode.VerificationCodeError.getErrorCode();
	}
	
	@Override
	public String getReason() {
		return reason;
	}
	
	@Override
	public String getMessage() {
		return ReturnStatusCode.VerificationCodeError.getErrorInfo();
	}
}
