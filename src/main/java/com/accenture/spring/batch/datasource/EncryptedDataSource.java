package com.accenture.spring.batch.datasource;

import java.io.IOException;
import java.security.NoSuchProviderException;
import java.util.HashMap;

import org.springframework.jdbc.datasource.DriverManagerDataSource;

import com.accenture.spring.batch.security.decryption.FileDecrypter;
import com.accenture.spring.batch.util.SecurityUtil;

/**
 * @author pratibha
 * 
 */
public class EncryptedDataSource extends DriverManagerDataSource {

	private FileDecrypter fileDecrypt;

	HashMap<String, String> credential = new HashMap<String, String>();

	/**
	 * @return the fileDecrypt
	 */
	public FileDecrypter getFileDecrypt() {
		return fileDecrypt;
	}

	/**
	 * @param fileDecrypt
	 *            the fileDecrypt to set
	 */
	public void setFileDecrypt(FileDecrypter fileDecrypt) {
		this.fileDecrypt = fileDecrypt;
	}

	public EncryptedDataSource() throws NoSuchProviderException, IOException {
		super();
		SecurityUtil.loadSecuritySetting();
	}

	public void initIt() throws Exception {
		credential = fileDecrypt.decryptFile();
	}

	@Override
	public String getPassword() {
System.out.println("password: "+credential.get("password"));
		return credential.get("password");
	}

	@Override
	public String getUsername() {
		System.out.println("username: "+credential.get("password"));
		return credential.get("username");
	}

}
