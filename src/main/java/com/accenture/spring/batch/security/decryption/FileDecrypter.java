/*
 * Created for DDC project.
 * Kohl's Corporation 2016
 */
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
import java.util.Iterator;

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
import org.springframework.beans.factory.annotation.Value;

import com.accenture.spring.batch.Exception.ExceptionCodes;
import com.accenture.spring.batch.Exception.SpringBtachException;
import com.accenture.spring.batch.contant.Constants;
import com.accenture.spring.batch.util.SecurityUtil;


/**
 * Class for returning underlying clear input stream to initialize FlatFileItemReader or
 * to uncompress encrypted input file
 *
 * @author Shruti Sethia
 * 
 */
public class FileDecrypter {

	private static final Logger LOGGER = Logger.getLogger(FileDecrypter.class);
  
    @Value("${ddc.publickey.user}")
	public static String validUserID;
    @Value("${batchSize}")
    public static int batchSize; 
    
    public InputStream decryptFile(String inputFileName, String keyFileName,
                                                       char[] passwd, String defaultFileName, boolean decryptWithCompress, int job) throws SpringBtachException {
        InputStream in = null;
        InputStream keyIn = null;
        BufferedReader br;
        
        System.out.println("inputFileName :: "+inputFileName);
        

        try {
            if (StringUtils.isEmpty(inputFileName)) {
                throw new SpringBtachException(ExceptionCodes.EMPTY_FIELD_VALUE, "Input File Name");
            }
            if (StringUtils.isEmpty(keyFileName)) {
                throw new SpringBtachException(ExceptionCodes.EMPTY_FIELD_VALUE, "Key File Name");
            }
            if (StringUtils.isEmpty(defaultFileName)) {
                throw new SpringBtachException(ExceptionCodes.EMPTY_FIELD_VALUE, "Output File Name");
            }

            br = new BufferedReader(new FileReader(inputFileName));
            if (br.readLine() == null) {
                throw new SpringBtachException(ExceptionCodes.EMPTY_FIELD_VALUE, "Input File is Empty");
            }


            in = new BufferedInputStream(new FileInputStream(
                    inputFileName));
            keyIn = new BufferedInputStream(new FileInputStream(
                    new File(keyFileName)));

            return decryptFile(in, keyIn, defaultFileName, passwd, decryptWithCompress, job);


        } catch (FileNotFoundException fileNotFound) {
			throw new SpringBtachException(ExceptionCodes.FILE_NOT_FOUND, fileNotFound);
		} catch (NoSuchProviderException missingBCProv) {
			throw new SpringBtachException(ExceptionCodes.FAILED_BCPROVIDER_LOADING,
					missingBCProv);
		} catch (IOException ioe) {
			throw new SpringBtachException(ExceptionCodes.FAILED_STREAM_ONCLOSE, ioe);
		} catch (Exception e) {
			throw new SpringBtachException(ExceptionCodes.FAILED_ENCRYPTION_DECRYPTION,
					e);
		}

     
    }

    /**
     * decrypt the passed in message stream
     */
    private InputStream decryptFile(InputStream in, InputStream keyIn, String defaultFileName,
                                           char[] passwd, boolean decryptWithCompress, int job) throws IOException,
            NoSuchProviderException, SpringBtachException {
    	
    	System.out.println("decrypt file");

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
                throw new SpringBtachException(ExceptionCodes.UNKNOWN_FILETYPE_ERROR, "Please ensure if the contents of Input are encrypted");
            }
            Iterator<?> it = enc.getEncryptedDataObjects();
            pgpSec = new PGPSecretKeyRingCollection(
                    PGPUtil.getDecoderStream(keyIn),
                    new JcaKeyFingerprintCalculator());

            while (sKey == null && it.hasNext()) {
                pbe = (PGPPublicKeyEncryptedData) it.next();

                sKey = SecurityUtil.findSecretKey(pgpSec, pbe.getKeyID(),
                        passwd);
            }

            if (sKey == null) {
                throw new IllegalArgumentException(ExceptionCodes.SECRET_KEY_NOTFOUND.getMsg());
            }

            clear = pbe
                    .getDataStream(new JcePublicKeyDataDecryptorFactoryBuilder()
                            .setProvider(Constants.BOUNCY_CASTLE_PROVIDER).build(sKey));
            
            System.out.println("clearStream :: "+clear);

            plainFact = new JcaPGPObjectFactory(clear);

            message = plainFact.nextObject();

            if (message instanceof PGPCompressedData) {

                PGPCompressedData cData = (PGPCompressedData) message;

                PGPObjectFactory pgpFact2 = new PGPObjectFactory(cData.getDataStream(), new JcaKeyFingerprintCalculator());

                message = pgpFact2.nextObject();
            }

            if (message instanceof PGPLiteralData) {
                ld = (PGPLiteralData) message;
            }else if (message instanceof PGPOnePassSignatureList) {
				throw new PGPException(
						"encrypted message contains a signed message - not literal data.");
			} else {
				throw new PGPException(
						"message is not a simple encrypted file - type unknown.");
			}	

          
                	
              
                LOGGER.info("Returning InputStream to initilaize reader");
                return ld.getInputStream();
           

        } catch (PGPException e) {
			if (e.getUnderlyingException() != null) {
				LOGGER.error(e.getUnderlyingException());
			}
			throw new SpringBtachException(ExceptionCodes.FAILED_DECRYPTION, e);

		}
    }

}