
package com.accenture.spring.batch.security.decryption;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchProviderException;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.bouncycastle.openpgp.PGPCompressedData;
import org.bouncycastle.openpgp.PGPEncryptedDataList;
import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPLiteralData;
import org.bouncycastle.openpgp.PGPObjectFactory;
import org.bouncycastle.openpgp.PGPOnePassSignatureList;
import org.bouncycastle.openpgp.PGPPrivateKey;
import org.bouncycastle.openpgp.PGPPublicKeyEncryptedData;
import org.bouncycastle.openpgp.PGPSecretKeyRingCollection;
import org.bouncycastle.openpgp.PGPUtil;
import org.bouncycastle.openpgp.jcajce.JcaPGPObjectFactory;
import org.bouncycastle.openpgp.operator.jcajce.JcaKeyFingerprintCalculator;
import org.bouncycastle.openpgp.operator.jcajce.JcePublicKeyDataDecryptorFactoryBuilder;

import com.accenture.spring.batch.Exception.ExceptionCodes;
import com.accenture.spring.batch.Exception.SpringBatchException;
import com.accenture.spring.batch.constants.Constants;
import com.accenture.spring.batch.util.SecurityUtil;

/**
 * Class for returning underlying clear input stream to initialize
 * FlatFileItemReader or to uncompress encrypted input file
 *
 * @author Shruti Sethia
 * 
 */
public class FileDecrypter {

	private static final Logger LOGGER = Logger.getLogger(FileDecrypter.class);

	private String gpgFilePath;
	private String keyFileName;
	private String passwd;

	public String getGpgFilePath() {
		return gpgFilePath;
	}

	public void setGpgFilePath(String gpgFilePath) {
		this.gpgFilePath = gpgFilePath;
	}

	public String getKeyFileName() {
		return keyFileName;
	}

	public void setKeyFileName(String keyFileName) {
		this.keyFileName = keyFileName;
	}

	public String getPasswd() {
		return passwd;
	}

	public void setPasswd(String passwd) {
		this.passwd = passwd;
	}

	public HashMap<String, String> decryptFile() throws IOException, NoSuchProviderException {
		InputStream in = new BufferedInputStream(new FileInputStream(gpgFilePath));
		InputStream keyIn = new BufferedInputStream(new FileInputStream(keyFileName));
		HashMap<String, String> db = decrypt(in, keyIn, passwd.toCharArray());
		keyIn.close();
		in.close();
		return db;
	}

	/**
	 * decrypt the passed in message stream
	 */
	private HashMap<String, String> decrypt(InputStream in, InputStream keyIn, char[] passwd)
			throws IOException, NoSuchProviderException {
		HashMap<String, String> db = new HashMap<String, String>();
		in = PGPUtil.getDecoderStream(in);

		try {
			JcaPGPObjectFactory pgpF = new JcaPGPObjectFactory(in);
			PGPEncryptedDataList enc;

			Object o = pgpF.nextObject();
			//
			// the first object might be a PGP marker packet.
			//
			if (o instanceof PGPEncryptedDataList) {
				enc = (PGPEncryptedDataList) o;
			} else {
				enc = (PGPEncryptedDataList) pgpF.nextObject();
			}

			//
			// find the secret key
			//
			@SuppressWarnings("unchecked")
			Iterator<PGPPublicKeyEncryptedData> it = enc.getEncryptedDataObjects();
			PGPPrivateKey sKey = null;
			PGPPublicKeyEncryptedData pbe = null;
			PGPSecretKeyRingCollection pgpSec = new PGPSecretKeyRingCollection(PGPUtil.getDecoderStream(keyIn),
					new JcaKeyFingerprintCalculator());

			while (sKey == null && it.hasNext()) {
				pbe = (PGPPublicKeyEncryptedData) it.next();

				sKey = SecurityUtil.findSecretKey(pgpSec, pbe.getKeyID(), passwd);
			}

			if (sKey == null) {
				throw new IllegalArgumentException("secret key for message not found.");
			}

			InputStream clear = pbe
					.getDataStream(new JcePublicKeyDataDecryptorFactoryBuilder().setProvider("BC").build(sKey));

			JcaPGPObjectFactory plainFact = new JcaPGPObjectFactory(clear);

			Object message = plainFact.nextObject();

			if (message instanceof PGPCompressedData) {
				PGPCompressedData cData = (PGPCompressedData) message;
				JcaPGPObjectFactory pgpFact = new JcaPGPObjectFactory(cData.getDataStream());

				message = pgpFact.nextObject();
			}

			if (message instanceof PGPLiteralData) {
				PGPLiteralData ld = (PGPLiteralData) message;

				InputStream unc = ld.getInputStream();
				String s = IOUtils.toString(unc, "UTF-8");

				String[] val = s.split("/");
				db.put("username", val[0]);
				db.put("password", val[1]);
				IOUtils.closeQuietly(unc);

			} else if (message instanceof PGPOnePassSignatureList) {
				throw new PGPException("encrypted message contains a signed message - not literal data.");
			} else {
				throw new PGPException("message is not a simple encrypted file - type unknown.");
			}

			if (pbe.isIntegrityProtected()) {
				if (!pbe.verify()) {
					LOGGER.info("message failed integrity check");
				} else {
					LOGGER.info("message integrity check passed");
				}
			} else {
				LOGGER.info("no message integrity check");
			}
		} catch (PGPException e) {
			LOGGER.error(e);
			if (e.getUnderlyingException() != null) {
				e.getUnderlyingException().printStackTrace();
			}
		}
		return db;
	}

	public InputStream decryptFile(String inputFileName, String keyFileName, char[] passwd, String defaultFileName,
			boolean decryptWithCompress, int job) throws SpringBatchException {
		InputStream in = null;
		InputStream keyIn = null;
		BufferedReader br;

		try {
			if (StringUtils.isEmpty(inputFileName)) {
				throw new SpringBatchException(ExceptionCodes.EMPTY_FIELD_VALUE, "Input File Name");
			}
			if (StringUtils.isEmpty(keyFileName)) {
				throw new SpringBatchException(ExceptionCodes.EMPTY_FIELD_VALUE, "Key File Name");
			}
			if (StringUtils.isEmpty(defaultFileName)) {
				throw new SpringBatchException(ExceptionCodes.EMPTY_FIELD_VALUE, "Output File Name");
			}

			br = new BufferedReader(new FileReader(inputFileName));
			if (br.readLine() == null) {
				br.close();
				throw new SpringBatchException(ExceptionCodes.EMPTY_FIELD_VALUE, "Input File is Empty");
			}
			br.close();
			in = new BufferedInputStream(new FileInputStream(inputFileName));
			keyIn = new BufferedInputStream(new FileInputStream(new File(keyFileName)));

			return decryptFile(in, keyIn, defaultFileName, passwd, decryptWithCompress, job);

		} catch (FileNotFoundException fileNotFound) {
			throw new SpringBatchException(ExceptionCodes.FILE_NOT_FOUND, fileNotFound);
		} catch (NoSuchProviderException missingBCProv) {
			throw new SpringBatchException(ExceptionCodes.FAILED_BCPROVIDER_LOADING, missingBCProv);
		} catch (IOException ioe) {
			throw new SpringBatchException(ExceptionCodes.FAILED_STREAM_ONCLOSE, ioe);
		} catch (Exception e) {
			throw new SpringBatchException(ExceptionCodes.FAILED_ENCRYPTION_DECRYPTION, e);
		}

	}

	/**
	 * decrypt the passed in message stream
	 */
	private InputStream decryptFile(InputStream in, InputStream keyIn, String defaultFileName, char[] passwd,
			boolean decryptWithCompress, int job) throws IOException, NoSuchProviderException, SpringBatchException {

		in = PGPUtil.getDecoderStream(in);
		JcaPGPObjectFactory pgpF;
		PGPEncryptedDataList enc;
		PGPPrivateKey sKey = null;
		PGPPublicKeyEncryptedData pbe = null;
		Object o;
		InputStream clear;
		JcaPGPObjectFactory plainFact;
		PGPSecretKeyRingCollection pgpSec;
		Object message;
		PGPLiteralData ld = null;
		try {
			pgpF = new JcaPGPObjectFactory(in);

			o = pgpF.nextObject();
			//
			// the first object might be a PGP marker packet.
			//
			if (o instanceof PGPEncryptedDataList) {
				enc = (PGPEncryptedDataList) o;
			} else {
				enc = (PGPEncryptedDataList) pgpF.nextObject();
			}

			//
			// find the secret key
			//
			if (null == enc) {
				throw new SpringBatchException(ExceptionCodes.UNKNOWN_FILETYPE_ERROR,
						"Please ensure if the contents of Input are encrypted");
			}
			Iterator<?> it = enc.getEncryptedDataObjects();
			pgpSec = new PGPSecretKeyRingCollection(PGPUtil.getDecoderStream(keyIn), new JcaKeyFingerprintCalculator());

			while (sKey == null && it.hasNext()) {
				pbe = (PGPPublicKeyEncryptedData) it.next();

				sKey = SecurityUtil.findSecretKey(pgpSec, pbe.getKeyID(), passwd);
			}

			if (sKey == null) {
				throw new IllegalArgumentException(ExceptionCodes.SECRET_KEY_NOTFOUND.getMsg());
			}

			clear = pbe.getDataStream(new JcePublicKeyDataDecryptorFactoryBuilder()
					.setProvider(Constants.BOUNCY_CASTLE_PROVIDER).build(sKey));

			plainFact = new JcaPGPObjectFactory(clear);

			message = plainFact.nextObject();

			if (message instanceof PGPCompressedData) {

				PGPCompressedData cData = (PGPCompressedData) message;

				PGPObjectFactory pgpFact2 = new PGPObjectFactory(cData.getDataStream(),
						new JcaKeyFingerprintCalculator());

				message = pgpFact2.nextObject();
			}

			if (message instanceof PGPLiteralData) {
				ld = (PGPLiteralData) message;
			} else if (message instanceof PGPOnePassSignatureList) {
				throw new PGPException("encrypted message contains a signed message - not literal data.");
			} else {
				throw new PGPException("message is not a simple encrypted file - type unknown.");
			}

			LOGGER.info("Returning InputStream to initilaize reader");
			return ld.getInputStream();

		} catch (PGPException e) {
			if (e.getUnderlyingException() != null) {
				LOGGER.error(e.getUnderlyingException());
			}
			throw new SpringBatchException(ExceptionCodes.FAILED_DECRYPTION, e);

		}
	}

}