package com.evan.demo.security.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

@Slf4j
public class AesUtils {

    private static final int IV_LENGTH_16 = 16;

    public static String decrypt(String encryptedBase64, String key) {
        byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
        SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, "AES");
        Cipher cipher;
        try {
            cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, new IvParameterSpec(getIvFromKey(keyBytes)));
            byte[] decrypted = cipher.doFinal(Base64.decodeBase64(encryptedBase64));
            return new String(decrypted);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException("encrypted error");
        }
    }

    public static String encrypt(String plainText, String key) {

        byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
        SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, "AES");
        Cipher cipher;
        try {
            cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, new IvParameterSpec(getIvFromKey(keyBytes)));
            byte[] decrypted = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
            return Base64.encodeBase64String(decrypted);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException("encrypted error");
        }
    }

    private static byte[] getIvFromKey(byte[] keyBytes) {
        byte[] ivKeys = new byte[IV_LENGTH_16];
        System.arraycopy(keyBytes, 0, ivKeys, 0, IV_LENGTH_16);
        return ivKeys;
    }

}
