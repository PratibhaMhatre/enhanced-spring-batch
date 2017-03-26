package com.accenture.spring.batch.writer;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.beans.factory.InitializingBean;

import com.accenture.spring.batch.util.SecurityUtil;

/**
 * FlatFileItemWriter implementation which writes encrypted PGP files
 * @author Omkar Marathe
 * @since 0.0.1
 */
public class PGPWriter extends FlatFileItemWriter<Object> implements InitializingBean{
	
	private boolean isCompressed;
	private String publicKeyFilePath;
	
	public PGPWriter() {
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
	
	@Override
	public void afterPropertiesSet() throws Exception {
		super.afterPropertiesSet();		
	}	

}
