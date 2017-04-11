/*
 * Created for DDC project.
 * Kohl's Corporation 2016
 */
package com.accenture.spring.batch.security.encryption;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.SecureRandom;
import java.util.Date;

import org.apache.log4j.Logger;
import org.bouncycastle.openpgp.PGPCompressedData;
import org.bouncycastle.openpgp.PGPCompressedDataGenerator;
import org.bouncycastle.openpgp.PGPEncryptedData;
import org.bouncycastle.openpgp.PGPEncryptedDataGenerator;
import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPLiteralData;
import org.bouncycastle.openpgp.PGPLiteralDataGenerator;
import org.bouncycastle.openpgp.PGPPublicKey;
import org.bouncycastle.openpgp.operator.jcajce.JcePGPDataEncryptorBuilder;
import org.bouncycastle.openpgp.operator.jcajce.JcePublicKeyKeyEncryptionMethodGenerator;

import com.accenture.spring.batch.Exception.ExceptionCodes;
import com.accenture.spring.batch.Exception.SpringBtachException;
import com.accenture.spring.batch.contant.Constants;
import com.accenture.spring.batch.util.JavaUtil;

/**
 * Class for chunk-wise encryption of an input file
 * @author Shruti Sethia
 * 
 *
 */
public class FileEncrypter {    

	private static final Logger BATCH_LOGGER = Logger.getLogger(Constants.BATCH_LOGGER);

    private static OutputStream pOut = null;
    private static PGPCompressedDataGenerator comData = null;
    private static OutputStream cOut = null;
    private static OutputStream outFromComData = null;
    private static boolean calledForFirstTime = false;
    public static boolean encWithCompress=false;
    public static FileOutputStream fOutsample;
    public static BufferedOutputStream fBufferedOut;


    public static boolean isCalledForFirstTime() {
        return calledForFirstTime;
    }

    public static void setCalledForFirstTime(boolean calledForFirstTime) {
    	FileEncrypter.calledForFirstTime = calledForFirstTime;
    }

    /**
     * Encrypts records equal to batchSize & writes encrypted bytes to sample output File
     * @author tkmaawv
     * @param fileName Name of output File for sample with absolute file path
     * @param stringToBeEnc contains records to be encrypted & written to sample output file
     * @param encKey is a PGPPublicKey to be used for Encryption
     * @param armor used to enable & disable ArmoredOutputStream
     * @param withIntegrityCheck if enabled can be used while decryption of encrypted file to verify integrity of a file
     * @return void
     */
    public void encryptBigText(String fileName, String stringToBeEnc,
                                      PGPPublicKey encKey, boolean armor, boolean withIntegrityCheck) {


        try {
            if (stringToBeEnc == null) {
                throw new SpringBtachException(ExceptionCodes.EMPTY_FIELD_VALUE, "stringToBeEnc[output] is Empty");
            }


            if (!calledForFirstTime) {
            	BATCH_LOGGER.info("Initialising the sample Encrypter for file "+fileName);
                fOutsample=new FileOutputStream(fileName);
                fBufferedOut=new BufferedOutputStream(fOutsample);
                PGPEncryptedDataGenerator cPk = new PGPEncryptedDataGenerator(
                        new JcePGPDataEncryptorBuilder(PGPEncryptedData.AES_128)
                                .setWithIntegrityPacket(withIntegrityCheck)
                                .setSecureRandom(new SecureRandom())
                                .setProvider(
                                		Constants.BOUNCY_CASTLE_PROVIDER));

                cPk.addMethod(new JcePublicKeyKeyEncryptionMethodGenerator(encKey)
                        .setProvider(Constants.BOUNCY_CASTLE_PROVIDER));
                cOut = cPk.open(fBufferedOut, new byte[1 << 16]);
                comData = new PGPCompressedDataGenerator(
                        PGPCompressedData.ZIP);
                outFromComData = encWithCompress?comData.open(cOut):cOut;
            }


            writeFileToLiteralData(stringToBeEnc, outFromComData,
                    PGPLiteralData.BINARY,
                    new byte[1 << 16]);


        } catch (PGPException e) {
        	BATCH_LOGGER.fatal(e);
        } catch(SpringBtachException e){
        	BATCH_LOGGER.fatal(e);
        }catch (IOException io) {
        	BATCH_LOGGER.error(ExceptionCodes.FAILED_STREAM_CREATION.getMsg() , io);
        }


    }


    /**
     * Write out the contents of the provided file as a literal data packet in partial packet
     * format.
     *
     * @param out      the stream to write the literal data to.
     * @param fileType the {@link PGPLiteralData} type to use for the file data.
     * @param buffer   buffer to be used to chunk the file into partial packets.
     * @throws java.io.IOException if an error occurs reading the file or writing to the output stream.
     * @see PGPLiteralDataGenerator#open(java.io.OutputStream, char, String, java.util.Date, byte[]).
     */
    private void writeFileToLiteralData(
            String s, OutputStream out,
            char fileType,
            byte[] buffer) {
        try {

            if (!calledForFirstTime) {
                Date date = new Date();
                PGPLiteralDataGenerator lData = new PGPLiteralDataGenerator();
                pOut = lData.open(out, fileType, "fileName", date,
                        buffer);
                calledForFirstTime = true;
            }


            pipeFileContents(s, pOut, buffer.length);
        } catch (IOException io) {
        	BATCH_LOGGER.error(ExceptionCodes.FAILED_STREAM_CREATION.getMsg() , io);

        }
    }

    /**
     * Writes encrypted bytes to sample output File
     * @param myString contains records to be encrypted & written to sample output file
     * @param pOut is an OutputStream obtained from PGPLiteralDataGenerator
     * @param bufSize buffer to be used to chunk the file into partial packets.
     * @return void
     */
    private void pipeFileContents(String myString, OutputStream pOut, int bufSize) {

        InputStream in;
        byte[] buf;
        int len;
        try {
            in = new ByteArrayInputStream(myString.getBytes());
            buf= new byte[bufSize];

            while ((len = in.read(buf)) > 0) {
                pOut.write(buf, 0, len);
            }
        } catch (IOException io) {
        	BATCH_LOGGER.error(ExceptionCodes.FAILED_STREAM_CREATION.getMsg() , io);
        }
    }

    /**
     * Closes all the open streams
     * @author tkmaah7
     * @return void
     */
    public static void closeStreams() {

        try {

            if (!JavaUtil.isObjectNull(pOut)) {
                pOut.close();
            }

            if (!JavaUtil.isObjectNull(comData)) {
                comData.close();
            }

            if (!JavaUtil.isObjectNull(cOut)) {
                cOut.close();
            }


            if(!JavaUtil.isObjectNull(outFromComData)){
                outFromComData.close();
            }

        } catch (IOException e) {
        	BATCH_LOGGER.warn("Error while closing sample Encryption Streams" ,e);
        }

    }

}
