package com.qiqi.account.exception;


import com.qiqi.account.errorCode.ReturnStatusCode;

public class VerificationCodeUsedException extends Exception implements ExceptionInterface {
	private static final long serialVersionUID = 1L;
	private String reason;

	public VerificationCodeUsedException() {
		super();
	}
	
	public VerificationCodeUsedException(String message) {
		super(message);
	}
	
	public VerificationCodeUsedException(String message, String reason) {
		super(message);
		this.reason = reason;
	}
	
	public VerificationCodeUsedException(String message, Throwable cause) {
		super(message, cause);
	}

	@Override
	public String getErrorCode() {
		return ReturnStatusCode.VerificationCodeUsed.getErrorCode();
	}
	
	@Override
	public String getReason() {
		return reason;
	}
	
	@Override
	public String getMessage() {
		return ReturnStatusCode.VerificationCodeUsed.getErrorInfo();
	}
}
