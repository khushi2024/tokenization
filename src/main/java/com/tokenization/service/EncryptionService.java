package com.tokenization.service;

import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

@Service
public class EncryptionService {
    private static final String ALGORITHM = "AES/GCM/NoPadding";
    private final byte[] secretKey;
    private final SecureRandom secureRandom;

    public EncryptionService() {
        this.secretKey = "12345678901234567890123456789012"
                .getBytes(StandardCharsets.UTF_8);
        this.secureRandom = new SecureRandom();
    }
    public String encrypt(String plainText) {

        try {
            byte[] iv = new byte[12];
            secureRandom.nextBytes(iv);

            Cipher cipher = Cipher.getInstance(ALGORITHM);

            SecretKeySpec keySpec =
                    new SecretKeySpec(secretKey, "AES");

            GCMParameterSpec gcmSpec =
                    new GCMParameterSpec(128, iv);

            cipher.init(
                    Cipher.ENCRYPT_MODE,
                    keySpec,
                    gcmSpec
            );

            byte[] encryptedBytes =
                    cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

            byte[] result = new byte[iv.length + encryptedBytes.length];

            System.arraycopy(
                    iv, 0,
                    result, 0,
                    iv.length
            );

            System.arraycopy(
                    encryptedBytes, 0,
                    result, iv.length,
                    encryptedBytes.length
            );

            return Base64.getEncoder().encodeToString(result);

        } catch (Exception e) {
            throw new RuntimeException("Encryption failed", e);
        }
    }

    public String decrypt(String encryptedText) {

        try {
            byte[] decodedBytes =
                    Base64.getDecoder().decode(encryptedText);

            byte[] iv = new byte[12];

            System.arraycopy(
                    decodedBytes,
                    0,
                    iv,
                    0,
                    iv.length
            );

            byte[] encryptedBytes =
                    new byte[decodedBytes.length - iv.length];

            System.arraycopy(
                    decodedBytes,
                    iv.length,
                    encryptedBytes,
                    0,
                    encryptedBytes.length
            );

            Cipher cipher = Cipher.getInstance(ALGORITHM);

            SecretKeySpec keySpec =
                    new SecretKeySpec(secretKey, "AES");

            GCMParameterSpec gcmSpec =
                    new GCMParameterSpec(128, iv);

            cipher.init(
                    Cipher.DECRYPT_MODE,
                    keySpec,
                    gcmSpec
            );

            byte[] decryptedBytes =
                    cipher.doFinal(encryptedBytes);

            return new String(
                    decryptedBytes,
                    StandardCharsets.UTF_8
            );

        } catch (Exception e) {
            throw new RuntimeException("Decryption failed", e);
        }
    }
}
