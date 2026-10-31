//package com.etz.cpay.core.utility;
//
//import com.etz.cpay.core.exception.SecurityValidationException;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//import org.springframework.util.StringUtils;
//import javax.annotation.PostConstruct;
//import javax.crypto.Cipher;
//import javax.crypto.KeyGenerator;
//import javax.crypto.SecretKey;
//import javax.crypto.spec.GCMParameterSpec;
//import javax.crypto.spec.SecretKeySpec;
//import java.nio.ByteBuffer;
//import java.nio.charset.StandardCharsets;
//import java.security.SecureRandom;
//import java.util.Base64;
//
//@Slf4j
//@Service
//public class SecurityService {
//
//
//    //Validating intent, encrypting and decrypting
//
//    private static final String ALGORITHM = "AES/GCM/NoPadding";
//    private static final int GCM_TAG_LENGTH = 128; // bits
//    private static final int GCM_IV_LENGTH = 12; // bytes (96 bits recommended for GCM)
//    private static final int AES_KEY_SIZE = 256; // bits
//    private static final String SECURITY_INFO_DELIMITER = "|";
//    private static final int NONCE_LENGTH = 32;
//
//    private final SecretKey secretKey;
//    private final SecureRandom secureRandom;
//
//    public SecurityService(
//            @Value("${encryption.master.key}") String encryptionKey) {
//
//        this.secureRandom = new SecureRandom();
//
//        // Validate and initialize an encryption key
//        if (!StringUtils.hasText(encryptionKey)) {
//            throw new IllegalArgumentException("Encryption key cannot be null or empty");
//        }
//
//        this.secretKey = initializeSecretKey(encryptionKey);
//
//        log.info("SecurityService initialized with AES-{}-GCM encryption", AES_KEY_SIZE);
//    }
//
//    @PostConstruct
//    public void logCacheConfig() {
//        log.info("[SECURITY_CACHE_INIT] Redis-backed caches configured ");
//    }
//
//
//    /**
//     * Generate encrypted security info for outgoing requests.
//     */
//    public String generateSecurityInfo(String requestBody) {
//        try {
//            if (!StringUtils.hasText(requestBody)) {
//                throw new IllegalArgumentException("Request body cannot be null or empty");
//            }
//
//
//
//            String payload = String.join(SECURITY_INFO_DELIMITER,
//                    requestBody, timestamp, nonce);
//
//            return encryptSecurityInfo(payload);
//
//        } catch (Exception e) {
//            log.error("[SECURITY_GENERATE] Failed to generate security info ", e);
//            throw new IllegalStateException("Failed to generate security token: " + e.getMessage());
//        }
//    }
//
//    private String encryptSecurityInfo(String plaintext) {
//        try {
//            byte[] iv = new byte[GCM_IV_LENGTH];
//            secureRandom.nextBytes(iv);
//
//            Cipher cipher = Cipher.getInstance(ALGORITHM);
//            GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
//            cipher.init(Cipher.ENCRYPT_MODE, secretKey, parameterSpec);
//
//            byte[] encryptedData = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));
//
//            ByteBuffer byteBuffer = ByteBuffer.allocate(iv.length + encryptedData.length);
//            byteBuffer.put(iv);
//            byteBuffer.put(encryptedData);
//
//            return Base64.getEncoder().encodeToString(byteBuffer.array());
//
//        } catch (Exception e) {
//            log.error("[SECURITY_ENCRYPT] Encryption failed", e);
//            throw new SecurityValidationException("Failed to encrypt security info", e);
//        }
//    }
//
//    private SecretKey initializeSecretKey(String encryptionKey) {
//        try {
//            byte[] keyBytes;
//
//            if (isBase64Encoded(encryptionKey)) {
//                keyBytes = Base64.getDecoder().decode(encryptionKey);
//            } else {
//                log.warn("[SECURITY_KEY_INIT] Using string-based key derivation. For production, use Base64-encoded keys.");
//                keyBytes = deriveKeyFromString(encryptionKey);
//            }
//
//            if (keyBytes.length != AES_KEY_SIZE / 8) {
//                throw new IllegalArgumentException(
//                        String.format("Invalid key size. Expected %d bytes, got %d bytes",
//                                AES_KEY_SIZE / 8, keyBytes.length)
//                );
//            }
//
//            return new SecretKeySpec(keyBytes, "AES");
//
//        } catch (Exception e) {
//            log.error("[SECURITY_KEY_INIT] Failed to initialize secret key", e);
//            throw new IllegalStateException("Invalid encryption key configuration", e);
//        }
//    }
//
//    private byte[] deriveKeyFromString(String key) throws Exception {
//        java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");
//        return digest.digest(key.getBytes(StandardCharsets.UTF_8));
//    }
//
//    private boolean isBase64Encoded(String str) {
//        try {
//            byte[] decoded = Base64.getDecoder().decode(str);
//            String reencoded = Base64.getEncoder().encodeToString(decoded);
//            return str.equals(reencoded);
//        } catch (IllegalArgumentException e) {
//            return false;
//        }
//    }
//
//    private String generateNonce() {
//        byte[] nonceBytes = new byte[NONCE_LENGTH / 2];
//        secureRandom.nextBytes(nonceBytes);
//
//        StringBuilder nonce = new StringBuilder(NONCE_LENGTH);
//        for (byte b : nonceBytes) {
//            nonce.append(String.format("%02x", b));
//        }
//
//        return nonce.toString();
//    }
//
//    public static String generateNewEncryptionKey() throws Exception {
//        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
//        keyGenerator.init(AES_KEY_SIZE, new SecureRandom());
//        SecretKey key = keyGenerator.generateKey();
//        return Base64.getEncoder().encodeToString(key.getEncoded());
//    }
//}
//
//
