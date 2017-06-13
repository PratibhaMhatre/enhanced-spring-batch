package com.accenture.spring.batch.util;


import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPPrivateKey;
import org.bouncycastle.openpgp.PGPPublicKey;
import org.bouncycastle.openpgp.PGPPublicKeyRing;
import org.bouncycastle.openpgp.PGPPublicKeyRingCollection;
import org.bouncycastle.openpgp.PGPSecretKey;
import org.bouncycastle.openpgp.PGPSecretKeyRingCollection;
import org.bouncycastle.openpgp.PGPUtil;
import org.bouncycastle.openpgp.operator.jcajce.JcaKeyFingerprintCalculator;
import org.bouncycastle.openpgp.operator.jcajce.JcePBESecretKeyDecryptorBuilder;

import com.accenture.spring.batch.Exception.ExceptionCodes;
import com.accenture.spring.batch.constants.Constants;

/**
 * This class contains utility operations related to bouncy castle cryptography
 * 
 * @author Omkar Marathe
 * @since Mar 20, 2017
 */
public class SecurityUtil {
	private static volatile boolean isSecuritySettingCalled;
	private static final Logger LOGGER = Logger.getLogger(SecurityUtil.class);

	// Loads BouncyCastleProvider & Java Crypto Extentions only once
	public static void loadSecuritySetting() {
		// make sure this method called only once
		if (isSecuritySettingCalled)
			return;
		isSecuritySettingCalled = true;
		Security.addProvider(new BouncyCastleProvider());

		// to handle unlimited keysize.
		try {
			Field field = Class.forName("javax.crypto.JceSecurity")
					.getDeclaredField("isRestricted");
			field.setAccessible(true);
			Field modifiersField = Field.class.getDeclaredField("modifiers");
			modifiersField.setAccessible(true);
			modifiersField
					.setInt(field, field.getModifiers() & ~Modifier.FINAL);
			field.set(null, java.lang.Boolean.FALSE);
			LOGGER.info("PGP security setting called.");
		} catch (Exception ex) {
			LOGGER.error(ExceptionCodes.FAILED_CRYPTO_LOADING.getMsg(), ex);
		}
	}

	/**
	 * Search a secret key ring collection for a secret key corresponding to
	 * keyID if it exists.
	 * 
	 * @param pgpSec
	 *            a secret key ring collection.
	 * @param keyID
	 *            keyID we want.
	 * @param pass
	 *            passphrase to decrypt secret key with.
	 * @return the private key.
	 * @throws PGPException
	 * @throws NoSuchProviderException
	 */
	public static PGPPrivateKey findSecretKey(
			PGPSecretKeyRingCollection pgpSec, long keyID, char[] pass)
			throws PGPException, NoSuchProviderException {
		PGPSecretKey pgpSecKey = pgpSec.getSecretKey(keyID);

		if (pgpSecKey == null) {
			return null;
		}

		return pgpSecKey
				.extractPrivateKey(new JcePBESecretKeyDecryptorBuilder()
						.setProvider(Constants.BOUNCY_CASTLE_PROVIDER)
						.build(pass));
	}

	/**
	 * A simple routine that opens a key ring file and loads the key matched by
	 * a userID for encryption.
	 * 
	 * @param publicKeyRingPath
	 *            path to KeyRingFile containing the public key data
	 * @param validUserID
	 *            is a User ID of a key to be matched
	 * @return the public key found matched by validUserID
	 * @throws IOException
	 * @throws PGPException
	 */
	public static PGPPublicKey readPublicKey(String publicKeyRingPath,
			String validUserID) throws IOException, PGPException {
		PGPPublicKeyRingCollection pgpPub = new PGPPublicKeyRingCollection(
				PGPUtil.getDecoderStream(new FileInputStream(publicKeyRingPath)),
				new JcaKeyFingerprintCalculator());
		Iterator<PGPPublicKeyRing> itr = pgpPub.getKeyRings();

		PGPPublicKey encKey = null;
		while (itr.hasNext()) {
			encKey = itr.next().getPublicKey();
			@SuppressWarnings("unchecked")
			Iterator<String> ids = encKey.getUserIDs();
			if (ids.hasNext()) {
				String userID = ids.next();
				LOGGER.debug("User ID of Public Key: " + userID);
				LOGGER.debug("No. of seconds key is Valid "
						+ encKey.getValidSeconds());

				if (userID.equals(validUserID) && encKey.getValidSeconds() >= 0) {
					LOGGER.debug("Selected getKeyID for encryption:"
							+ encKey.getKeyID());
					LOGGER.debug("Selected userID for encryption:" + userID);
					break;
				}
			}
		}
		return encKey;
	}

}
