package com.accenture.spring.batch.writer;

import java.util.List;

import org.bouncycastle.openpgp.PGPPublicKey;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStream;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemWriter;

import com.accenture.spring.batch.Exception.ExceptionCodes;
import com.accenture.spring.batch.Exception.SpringBatchException;
import com.accenture.spring.batch.security.encryption.FileEncrypter;
import com.accenture.spring.batch.util.JavaUtil;
import com.accenture.spring.batch.util.SecurityUtil;

/**
 * FlatFileItemWriter implementation which writes encrypted PGP files
 * 
 * @author Shruti Sethia
 * @since 0.0.1
 */
public class PGPWriter implements ItemWriter<Object>, ItemStream {

	private boolean isBufferInitialzed = false;

	private boolean isCompressed;
	private String publicKeyFilePath;
	private String outputFilePath;
	private String validUserID;
	private String header;
	private PGPPublicKey encKey;

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
	 * @param isCompressed
	 *            the isCompressed to set
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
	 * @param publicKeyFilePath
	 *            the publicKeyFilePath to set
	 */
	public void setPublicKeyFilePath(String publicKeyFilePath) {
		this.publicKeyFilePath = publicKeyFilePath;
	}

	/**
	 * @return the outputFilePath
	 */
	public String getOutputFilePath() {
		return outputFilePath;
	}

	/**
	 * @param outputFilePath
	 *            the outputFilePath to set
	 */
	public void setOutputFilePath(String outputFilePath) {
		this.outputFilePath = outputFilePath;
	}

	/**
	 * @return the validUserID
	 */
	public String getValidUserID() {
		return validUserID;
	}

	/**
	 * @param validUserID
	 *            the validUserID to set
	 */
	public void setValidUserID(String validUserID) {
		this.validUserID = validUserID;
	}

	/**
	 * @return the header
	 */
	public String getHeader() {
		return header;
	}

	/**
	 * @param header
	 *            the header to set
	 */
	public void setHeader(String header) {
		this.header = header;
	}

	FileEncrypter fileEncrypter = new FileEncrypter();

	public void write(List<? extends Object> items) throws Exception {
		if (!isBufferInitialzed) {
			isBufferInitialzed = true;
			FileEncrypter.encWithCompress = true;
			if (JavaUtil.isObjectNull(outputFilePath)) {
				throw new SpringBatchException(ExceptionCodes.OUTPUT_FILE_NOTFOUND, "output path not found");
			}

			/*
			 * strSampleOutputFileName = sampleOutputDir + filenameExtractCL;
			 * FD_BATCH_LOGGER.info("Sample output file name: " +
			 * strSampleOutputFileName);
			 */

			if (JavaUtil.isObjectNull(publicKeyFilePath)) {
				throw new SpringBatchException(ExceptionCodes.PUBLIC_KEY_NOTFOUND, "For  Public Key Path");
			}
			encKey = SecurityUtil.readPublicKey(publicKeyFilePath, validUserID);

			if (null == encKey) {
				throw new SpringBatchException(ExceptionCodes.PUBLIC_KEY_NOTFOUND, "For Sample");
			}
			if (null != header) {
				fileEncrypter.encryptBigText(outputFilePath, header + "\n", encKey, false, true);
			}
		}

		encryptAndWrite(items);

	}

	private void encryptAndWrite(List<? extends Object> items) {
		StringBuilder record = new StringBuilder();

		for (Object cdRecord : items) {
			record.append(cdRecord.toString());
			record.append("\n");
		}

		fileEncrypter.encryptBigText(outputFilePath, record.toString(), encKey, false, true);
	}

	@Override
	public void open(ExecutionContext executionContext) throws ItemStreamException {
		// TODO Auto-generated method stub

	}

	@Override
	public void update(ExecutionContext executionContext) throws ItemStreamException {
		// TODO Auto-generated method stub

	}

	@Override
	public void close() throws ItemStreamException {
		FileEncrypter.closeStreams();

	}

}
