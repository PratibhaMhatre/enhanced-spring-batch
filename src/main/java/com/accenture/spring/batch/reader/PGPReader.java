package com.accenture.spring.batch.reader;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.beans.factory.InitializingBean;

import com.accenture.spring.batch.util.SecurityUtil;

/**
 * FlatFileItemReader implementation which reads encrypted GPG files
 * PGPReader needs to be Step Scoped if isCompressed is true
 * @author Omkar Marathe
 * @since 0.0.1
 */
public class PGPReader extends FlatFileItemReader<Object> implements InitializingBean{

	private boolean isCompressed;
	private String passphrase1;
	private String secretKeyFilePath;
	private String publicKeyFilePath; //Can be null if isCompressed is false
	private String passphrase2;       //Can be null if isCompressed is false
	
	public PGPReader() {
		SecurityUtil.loadSecuritySetting();
	}
	
	/**
	 * @return the isCompressed
	 */
	public boolean isCompressed() {
		return isCompressed;
	}
	/**
	 * @param isCompressed the isCompressed to set
	 */
	public void setCompressed(boolean isCompressed) {
		this.isCompressed = isCompressed;
	}
	/**
	 * @return the passphrase1
	 */
	public String getPassphrase1() {
		return passphrase1;
	}
	/**
	 * @param passphrase1 the passphrase1 to set
	 */
	public void setPassphrase1(String passphrase1) {
		this.passphrase1 = passphrase1;
	}
	/**
	 * @return the secretKeyFilePath
	 */
	public String getSecretKeyFilePath() {
		return secretKeyFilePath;
	}
	/**
	 * @param secretKeyFilePath the secretKeyFilePath to set
	 */
	public void setSecretKeyFilePath(String secretKeyFilePath) {
		this.secretKeyFilePath = secretKeyFilePath;
	}
	/**
	 * @return the publicKeyFilePath
	 */
	public String getPublicKeyFilePath() {
		return publicKeyFilePath;
	}
	/**
	 * @param publicKeyFilePath the publicKeyFilePath to set
	 */
	public void setPublicKeyFilePath(String publicKeyFilePath) {
		this.publicKeyFilePath = publicKeyFilePath;
	}
	/**
	 * @return the passphrase2
	 */
	public String getPassphrase2() {
		return passphrase2;
	}
	/**
	 * @param passphrase2 the passphrase2 to set
	 */
	public void setPassphrase2(String passphrase2) {
		this.passphrase2 = passphrase2;
	}
	
	@Override
	public void afterPropertiesSet() throws Exception {
		super.afterPropertiesSet();
		if(this.isCompressed){
			// TODO Check if isCompressed is true, then generate intermediate file & change/override the input resource
			
		}
	}
	
	
}
