
package com.accenture.spring.batch.Exception;

public class SpringBatchException extends Exception {

	private static final long serialVersionUID = 1L;

	private final String errorCode;
	private final String errorMsg;
	private final Object errorObject;

	public SpringBatchException(ExceptionCodes code) {
		this.errorMsg = code.getMsg();
		this.errorCode = code.getId();
		this.errorObject = null;
	}

	public SpringBatchException(ExceptionCodes code, Object object) {
		this.errorMsg = code.getMsg();
		this.errorCode = code.getId();
		this.errorObject = object;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public Object getErrorObject() {
		return errorObject;
	}

	public String getMessage() {
		return errorMsg + "[" + errorObject + "]";
	}

}