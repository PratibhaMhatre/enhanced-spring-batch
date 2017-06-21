package com.accenture.spring.batch.reader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.NoSuchProviderException;
import java.util.ArrayList;

import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPPublicKey;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.InputStreamResource;

import com.accenture.spring.batch.Exception.ExceptionCodes;
import com.accenture.spring.batch.Exception.SpringBatchException;
import com.accenture.spring.batch.constants.Constants;
import com.accenture.spring.batch.security.decryption.FileDecrypter;
import com.accenture.spring.batch.security.encryption.FileEncrypter;
import com.accenture.spring.batch.util.JavaUtil;
import com.accenture.spring.batch.util.SecurityUtil;

/**
 * FlatFileItemReader implementation which reads encrypted GPG files PGPReader
 * needs to be Step Scoped if isCompressed is true
 * 
 * @author Shruti Sethia
 * @since 0.0.1
 */
public class PGPFlatFileItemReader extends FlatFileItemReader<Object> implements InitializingBean {

	/*
	 * if this flag is true that means file
	 * is commpressed
	 */
	private boolean isCompressed = true; 
	private String passphrase;
	private String secretKeyFilePath;
	private String publicKeyFilePath;
	private String publicKeyUserId;
	private String inputFilePath;

	public PGPFlatFileItemReader() {
		SecurityUtil.loadSecuritySetting();
	}

	/**
	 * @return the isCompressed
	 */
	public boolean isCompressed() {
		return isCompressed;
	}

	/**
	 * @param isCompressed
	 *            the isCompressed to set
	 */
	public void setCompressed(boolean isCompressed) {
		this.isCompressed = isCompressed;
	}

	/**
	 * @return the passphrase
	 */
	public String getPassphrase() {
		return passphrase;
	}

	/**
	 * @param passphrase
	 *            the passphrase to set
	 */
	public void setPassphrase(String passphrase) {
		this.passphrase = passphrase;
	}

	/**
	 * @return the secretKeyFilePath
	 */
	public String getSecretKeyFilePath() {
		return secretKeyFilePath;
	}

	/**
	 * @param secretKeyFilePath
	 *            the secretKeyFilePath to set
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
	 * @param publicKeyFilePath
	 *            the publicKeyFilePath to set
	 */
	public void setPublicKeyFilePath(String publicKeyFilePath) {
		this.publicKeyFilePath = publicKeyFilePath;
	}

	/**
	 * @return the publicKeyUserId
	 */
	public String getPublicKeyUserId() {
		return publicKeyUserId;
	}

	/**
	 * @param publicKeyUserId
	 *            the publicKeyUserId to set
	 */
	public void setPublicKeyUserId(String publicKeyUserId) {
		this.publicKeyUserId = publicKeyUserId;
	}

	/**
	 * @return the inputFilePath
	 */
	public String getInputFilePath() {
		return inputFilePath;
	}

	/**
	 * @param inputFilePath
	 *            the inputFilePath to set
	 */
	public void setInputFilePath(String inputFilePath) {
		this.inputFilePath = inputFilePath;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		super.afterPropertiesSet();
		FileDecrypter fileDecrypter = new FileDecrypter();
		if (this.isCompressed) {

			this.uncompressFile(fileDecrypter.decryptFile(inputFilePath, secretKeyFilePath, passphrase.toCharArray(),
					"sample.txt", true, 2));

			inputFilePath = inputFilePath + Constants.INBOUND_UNC_FILE;

		}

		InputStream clearStream = fileDecrypter.decryptFile(inputFilePath, secretKeyFilePath, passphrase.toCharArray(),
				"sample.txt", false, 2);

		InputStreamResource in = new InputStreamResource(clearStream);

		this.setResource(in);

	}

	private void uncompressFile(InputStream unc)
			throws IOException, NoSuchProviderException, PGPException, SpringBatchException {

		BufferedReader bufferRead = new BufferedReader(new InputStreamReader(unc));

		ArrayList<String> rawdata = new ArrayList<String>();
		FileEncrypter fileEncrypter = new FileEncrypter();

		PGPPublicKey encKey;

		String outFileName = inputFilePath + Constants.INBOUND_UNC_FILE;

		if (JavaUtil.isObjectNull(publicKeyFilePath)) {

			throw new SpringBatchException(ExceptionCodes.PUBLIC_KEY_NOTFOUND, "For Public Key Path");

		}
		encKey = SecurityUtil.readPublicKey(publicKeyFilePath, publicKeyUserId);

		String line = bufferRead.readLine();

		while (line != null) {
			if (rawdata.size() < Constants.batchSize) {
				rawdata.add(line + "\n");
			} else if (rawdata.size() == Constants.batchSize) {
				rawdata.add(line + "\n");
				StringBuilder RecordsBuilder = new StringBuilder();
				for (String record : rawdata) {
					RecordsBuilder.append(record);
				}
				fileEncrypter.encryptBigText(outFileName, RecordsBuilder.toString(), encKey, false, true);
				rawdata.clear();
			}
			line = bufferRead.readLine();
			if (line == null) {
				for (String record : rawdata) {

					if (rawdata.size() > 0) {
						fileEncrypter.encryptBigText(outFileName, record, encKey, false, true);
					}
				}
				rawdata.clear();
			}
		}

		if (rawdata.size() > 0) {
			for (String record : rawdata) {
				fileEncrypter.encryptBigText(outFileName, record, encKey, false, true);
			}
		}

		FileEncrypter.setCalledForFirstTime(false);
		FileEncrypter.closeStreams();
		if (!JavaUtil.isObjectNull(FileEncrypter.fBufferedOut)) {
			FileEncrypter.fBufferedOut.close();
		}
	}

}
