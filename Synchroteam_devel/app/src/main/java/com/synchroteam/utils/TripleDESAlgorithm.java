package com.synchroteam.utils;

import org.apache.commons.codec.binary.Base64;

import java.security.MessageDigest;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;


public class TripleDESAlgorithm {


    private static final String encryptionKey = "KDlTtXchhZTGufMYmOYGS4HffxPSUrfmqCHXaI9wOGYOiJIUzI1NiJ9";


    public static String encryptText(String message) throws Exception {

        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] keyBytes = md.digest(encryptionKey.getBytes("utf-8"));
        SecretKey key = new SecretKeySpec(keyBytes, "DESede");
        Cipher cipher = Cipher.getInstance("DESede");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] plainTextBytes = message.getBytes("utf-8");
        byte[] buf = cipher.doFinal(plainTextBytes);
        byte[] base64Bytes = Base64.encodeBase64(buf);
        String base64EncryptedString = new String(base64Bytes);
        return base64EncryptedString;
    }

    public static String decryptText(String encryptedText) throws Exception {

        byte[] message = Base64.decodeBase64(encryptedText.getBytes("utf-8"));

        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] keyBytes = md.digest(encryptionKey.getBytes("utf-8"));
        SecretKey key = new SecretKeySpec(keyBytes, "DESede");
        Cipher decipher = Cipher.getInstance("DESede");
        decipher.init(Cipher.DECRYPT_MODE, key);
        byte[] plainText = decipher.doFinal(message);
        String decrypt = new String(plainText, "UTF-8");
        return decrypt;
    }

}