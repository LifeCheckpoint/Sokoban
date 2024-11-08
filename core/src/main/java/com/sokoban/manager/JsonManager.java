package com.sokoban.manager;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.util.Base64;

public class JsonManager {
    private static final String ALGORITHM = "AES";
    private static final String HASH_ALGORITHM = "SHA-256";
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final int AES_KEY_SIZE = 16; // 16 bytes AES Key

    private final SecretKey secretKey;

    // 给定的密钥字符串生成 SecretKey
    public JsonManager(String key) {
        this.secretKey = generateSecretKey(key);
    }

    // 从字符串生成 SecretKey，长度为 16 字节
    private SecretKey generateSecretKey(String key) {
        byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
        if (keyBytes.length < AES_KEY_SIZE) {
            byte[] paddedKey = new byte[AES_KEY_SIZE];
            System.arraycopy(keyBytes, 0, paddedKey, 0, keyBytes.length);
            return new SecretKeySpec(paddedKey, ALGORITHM);
        } else if (keyBytes.length > AES_KEY_SIZE) {
            return new SecretKeySpec(keyBytes, 0, AES_KEY_SIZE, ALGORITHM);
        }
        return new SecretKeySpec(keyBytes, ALGORITHM);
    }

    // 加密并保存 JSON 数据到文件
    public void saveEncryptedJson(String filePath, Object data) throws Exception {
        try {
            // 将对象转换为 JSON 字符串
            String json = objectMapper.writeValueAsString(data);

            // 生成 SHA-256
            String hash = generateSHA256Hash(json);

            // 加密 JSON
            String encryptedJson = encrypt(json);

            // Base64编码加密 JSON 并与校验值合并保存
            String fileContent = Base64.getEncoder().encodeToString(encryptedJson.getBytes(StandardCharsets.UTF_8)) + ":" + hash;
            Files.write(Paths.get(filePath), fileContent.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new Exception("保存加密 JSON 失败: " + e.getMessage(), e);
        }
    }

    // 从文件加载并解密 JSON 数据
    public <T> T loadEncryptedJson(String filePath, Class<T> clazz) throws Exception {
        try {
            // 读取文件内容并分离加密 JSON 和校验值
            String fileContent = new String(Files.readAllBytes(Paths.get(filePath)), StandardCharsets.UTF_8);
            String[] parts = fileContent.split(":");
            if (parts.length != 2) {
                throw new Exception("文件格式无效，无法解析");
            }

            String encryptedJson = new String(Base64.getDecoder().decode(parts[0]), StandardCharsets.UTF_8);
            String savedHash = parts[1];

            // 解密 JSON 数据
            String decryptedJson = decrypt(encryptedJson);

            // 生成解密后 JSON 的 SHA-256 校验值并验证
            String currentHash = generateSHA256Hash(decryptedJson);
            if (!currentHash.equals(savedHash)) {
                throw new Exception("文件被篡改，校验失败");
            }

            // 将 JSON 字符串转换为对象
            return objectMapper.readValue(decryptedJson, clazz);
        } catch (Exception e) {
            throw new Exception("加载加密 JSON 失败: " + e.getMessage(), e);
        }
    }

    // AES 加密
    private String encrypt(String data) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encryptedData = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encryptedData);
    }

    // AES 解密
    private String decrypt(String encryptedData) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] decodedData = Base64.getDecoder().decode(encryptedData);
        byte[] originalData = cipher.doFinal(decodedData);
        return new String(originalData, StandardCharsets.UTF_8);
    }

    // 生成 SHA-256 校验值
    private String generateSHA256Hash(String data) throws Exception {
        MessageDigest digest = MessageDigest.getInstance(HASH_ALGORITHM);
        byte[] hash = digest.digest(data.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(hash);
    }
}
