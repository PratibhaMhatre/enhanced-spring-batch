package com.accenture.spring.batch.Exception;
/**
 * @author tkmaawv
 * 
 */
public enum ExceptionCodes {

		EMPTY_FIELD_VALUE("C001", "String field is missing."), 
		INVALID_FIELD_VALUE("C002", "Field value is invalid."), 
		INVALID_NUMBER_FORMAT(
			"C003", "Number format is invalid."), FALED_INTEGRITY_CHK(
			"C004", "Files failed integrity check"), NO_INTEGRITY_CHK(
			"C005", "no Files integrity check"), FAILED_STREAM_CREATION(
			"C006", "Exception occurred while creating Input/Output Stream "), FAILED_BCPROVIDER_LOADING(
			"C007",
			"Exception occurred while loading Bouncy Castle Provider "), FAILED_ENCRYPTION_DECRYPTION(
			"C008", "Exception occurred while encryption/decryption "), FAILED_STREAM_ONCLOSE(
			"C009", "Exception occurred while closing Input/Output Stream "), FILE_NOT_FOUND(
			"C010", "Exception occurred while accessing Input/Key Ring file "), SIGNED_FILE_ERROR(
			"C011",
			"encrypted Files contains a signed message - not literal data."), UNKNOWN_FILETYPE_ERROR(
			"C012", "Files is not a simple encrypted file - type unknown."), SECRET_KEY_NOTFOUND(
			"C013", "secret key for Files not found."), PUBLIC_KEY_NOTFOUND(
			"C014", "Public key for Files not found."), FAILED_CRYPTO_LOADING(
			"C015", "Error while Loading Cryptography settings"), FAILED_PROPRTY_LOADING(
			"C016", "Exception occurred while decrypting file"), FAILED_PROTECTION(
			"C017", "Input File is missing."),FAILED_DECRYPTION(
					"DDC007", "Exception occurred while decrypting file");

	private final String id;
	private final String msg;

	ExceptionCodes(String id, String msg) {
		this.id = id;
		this.msg = msg;
	}

	public String getId() {
		return this.id;
	}

	public String getMsg() {
		return this.msg;
	}

}
