/*
 * Created for DDC project.
 * Kohl's Corporation 2016
 */
package com.accenture.spring.batch.Exception;

/**
 * This is a custom Exception class for DDC Project
 * 
 * @author tkmaawv
 * @since Nov 28 2016
 */
public class SpringBtachException extends Exception {

	private static final long serialVersionUID = 1L;

	private final String errorCode;
	private final String errorMsg;
	private final Object errorObject;

	public SpringBtachException(ExceptionCodes code) {
		this.errorMsg = code.getMsg();
		this.errorCode = code.getId();
		this.errorObject = null;
	}

	public SpringBtachException(ExceptionCodes code, Object object) {
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