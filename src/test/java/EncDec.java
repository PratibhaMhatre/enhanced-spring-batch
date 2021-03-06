
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.util.Iterator;

import org.apache.commons.io.IOUtils;
import org.bouncycastle.bcpg.ArmoredOutputStream;
import org.bouncycastle.bcpg.CompressionAlgorithmTags;
import org.bouncycastle.openpgp.PGPCompressedData;
import org.bouncycastle.openpgp.PGPEncryptedData;
import org.bouncycastle.openpgp.PGPEncryptedDataGenerator;
import org.bouncycastle.openpgp.PGPEncryptedDataList;
import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPLiteralData;
import org.bouncycastle.openpgp.PGPOnePassSignatureList;
import org.bouncycastle.openpgp.PGPPrivateKey;
import org.bouncycastle.openpgp.PGPPublicKey;
import org.bouncycastle.openpgp.PGPPublicKeyEncryptedData;
import org.bouncycastle.openpgp.PGPSecretKeyRingCollection;
import org.bouncycastle.openpgp.PGPUtil;
import org.bouncycastle.openpgp.jcajce.JcaPGPObjectFactory;
import org.bouncycastle.openpgp.operator.jcajce.JcaKeyFingerprintCalculator;
import org.bouncycastle.openpgp.operator.jcajce.JcePGPDataEncryptorBuilder;
import org.bouncycastle.openpgp.operator.jcajce.JcePublicKeyDataDecryptorFactoryBuilder;
import org.bouncycastle.openpgp.operator.jcajce.JcePublicKeyKeyEncryptionMethodGenerator;
import org.bouncycastle.util.io.Streams;

import com.accenture.spring.batch.util.SecurityUtil;

/**
 * A simple utility class that encrypts/decrypts public key based encryption
 * files.
 * <p>
 * To encrypt a file: KeyBasedFileProcessor -e [-a|-ai] fileName
 * publicKeyFile.<br>
 * If -a is specified the output file will be "ascii-armored". If -i is
 * specified the output file will be have integrity checking added.
 * <p>
 * To decrypt: KeyBasedFileProcessor -d fileName secretKeyFile passPhrase.
 * <p>
 * Note 1: this example will silently overwrite files, nor does it pay any
 * attention to the specification of "_CONSOLE" in the filename. It also expects
 * that a single pass phrase will have been used.
 * <p>
 * Note 2: if an empty file name has been specified in the literal data object
 * contained in the encrypted packet a file with the name filename.out will be
 * generated in the current working directory.
 */
public class EncDec {
	private static void decryptFile(String inputFileName, String keyFileName, char[] passwd, String defaultFileName)
			throws IOException, NoSuchProviderException {
		InputStream in = new BufferedInputStream(new FileInputStream(inputFileName));
		InputStream keyIn = new BufferedInputStream(new FileInputStream(keyFileName));
		decryptFile(in, keyIn, passwd, defaultFileName);
		keyIn.close();
		in.close();
	}

	/**
	 * decrypt the passed in message stream
	 */
	private static void decryptFile(InputStream in, InputStream keyIn, char[] passwd, String defaultFileName)
			throws IOException, NoSuchProviderException {
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
			Iterator it = enc.getEncryptedDataObjects();
			PGPPrivateKey sKey = null;
			PGPPublicKeyEncryptedData pbe = null;
			PGPSecretKeyRingCollection pgpSec = new PGPSecretKeyRingCollection(PGPUtil.getDecoderStream(keyIn),
					new JcaKeyFingerprintCalculator());

			while (sKey == null && it.hasNext()) {
				pbe = (PGPPublicKeyEncryptedData) it.next();

				sKey = PGPEUtil.findSecretKey(pgpSec, pbe.getKeyID(), passwd);
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

				String outFileName = ld.getFileName();
				if (outFileName.length() == 0) {
					outFileName = defaultFileName;
				}

				InputStream unc = ld.getInputStream();
				OutputStream fOut = new BufferedOutputStream(new FileOutputStream(outFileName));
				String s = IOUtils.toString(unc, "UTF-8");
				System.out.println(s);

				Streams.pipeAll(unc, fOut);

				fOut.close();
			} else if (message instanceof PGPOnePassSignatureList) {
				throw new PGPException("encrypted message contains a signed message - not literal data.");
			} else {
				throw new PGPException("message is not a simple encrypted file - type unknown.");
			}

			if (pbe.isIntegrityProtected()) {
				if (!pbe.verify()) {
					System.err.println("message failed integrity check");
				} else {
					System.err.println("message integrity check passed");
				}
			} else {
				System.err.println("no message integrity check");
			}
		} catch (PGPException e) {
			System.err.println(e);
			if (e.getUnderlyingException() != null) {
				e.getUnderlyingException().printStackTrace();
			}
		}
	}

	private static void encryptFile(String outputFileName, String inputFileName, String encKeyFileName, boolean armor,
			boolean withIntegrityCheck) throws IOException, NoSuchProviderException, PGPException {
		OutputStream out = new BufferedOutputStream(new FileOutputStream(outputFileName));
		PGPPublicKey encKey = PGPEUtil.readPublicKey(encKeyFileName);
		encryptFile(out, inputFileName, encKey, armor, withIntegrityCheck);
		out.close();
	}

	private static void encryptFile(OutputStream out, String fileName, PGPPublicKey encKey, boolean armor,
			boolean withIntegrityCheck) throws IOException, NoSuchProviderException {
		if (armor) {
			out = new ArmoredOutputStream(out);
		}

		try {
			byte[] bytes = PGPEUtil.compressFile(fileName, CompressionAlgorithmTags.ZIP);

			PGPEncryptedDataGenerator encGen = new PGPEncryptedDataGenerator(
					new JcePGPDataEncryptorBuilder(PGPEncryptedData.CAST5).setWithIntegrityPacket(withIntegrityCheck)
							.setSecureRandom(new SecureRandom()).setProvider("BC"));

			encGen.addMethod(new JcePublicKeyKeyEncryptionMethodGenerator(encKey).setProvider("BC"));

			OutputStream cOut = encGen.open(out, bytes.length);

			cOut.write(bytes);
			cOut.close();

			if (armor) {
				out.close();
			}
		} catch (PGPException e) {
			System.err.println(e);
			if (e.getUnderlyingException() != null) {
				e.getUnderlyingException().printStackTrace();
			}
		}
	}

	public static void main(String[] args) throws Exception {

		SecurityUtil.loadSecuritySetting();
		String inputFile = "C:\\Users\\pratibha.ghorpade\\git\\enhanced-spring-batch\\src\\test\\resources\\test.properties";
		// "G:\\share\\IS\\A141\\Omkar\\DKC\\au\\input\\kohls_cash_emails_sas.dat";
		String outFile = "C:\\Users\\pratibha.ghorpade\\git\\enhanced-spring-batch\\src\\test\\resources\\enc_test.properties";
		// String keyFileName =
		// "G:\\share\\IS\\A141\\Omkar\\DKC\\KeyRing\\secring.gpg";
		// String publicKeyName =
		// "G:\\share\\IS\\A141\\Omkar\\DKC\\KeyRing\\pubring.gpg";
		String publicKeyName = "C:\\Users\\pratibha.ghorpade\\git\\enhanced-spring-batch\\src\\test\\resources\\public.asc";
		String keyFileName = "C:\\Users\\pratibha.ghorpade\\git\\enhanced-spring-batch\\src\\test\\resources\\Secret.asc";
		char[] ch = "12345".toCharArray();
		// decryptFile(outFile, keyFileName, ch, "dec2.out");
		encryptFile(outFile, inputFile, publicKeyName, false, true);
		// }
		// else
		// {
		// System.err.println("usage: KeyBasedFileProcessor -d|-e [-a|ai] file
		// [secretKeyFile passPhrase|pubKeyFile]");
		// }
	}
}
