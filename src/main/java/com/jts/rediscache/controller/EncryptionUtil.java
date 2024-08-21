package com.jts.rediscache.controller;

import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class EncryptionUtil {

	
	private static final String ALGORITHM = "AES";
    private static final String SECRET_KEY = "6H6SIdt3MdgaaO0MN/Qcrw=="; // should have only 24 letter


    private static SecretKey getSecretKey() { 
    	return new SecretKeySpec(Base64.getDecoder().decode(SECRET_KEY), ALGORITHM);
    }

    public static String encrypt(String data) throws Exception {
        
    	Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, getSecretKey());
        byte[] encryptedData = cipher.doFinal(data.getBytes());
        return Base64.getEncoder().encodeToString(encryptedData);
    }

    public static String decrypt(String encryptedData) throws Exception {
        
    	Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, getSecretKey());
        byte[] decodedData = Base64.getDecoder().decode(encryptedData);
        byte[] decryptedData = cipher.doFinal(decodedData);
        return new String(decryptedData);
    }


}
