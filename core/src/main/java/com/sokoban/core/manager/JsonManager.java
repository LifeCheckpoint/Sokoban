package com.sokoban.core.manager;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.sokoban.core.Logger;
import com.sokoban.core.map.SubMapData;
import com.sokoban.core.map.serialize.SubMapDataDeserializer;
import com.sokoban.core.map.serialize.SubMapDataSerializer;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.util.Base64;

/**
 * Json 序列化管理器
 * @author Claude
 * @author Life_Checkpoint
 */
public class JsonManager {
    private static final String ALGORITHM = "AES";
    private static final String HASH_ALGORITHM = "SHA-256";
    private static final int AES_KEY_SIZE = 16; // 16 bytes AES Key
    private static ObjectMapper objectMapper = new ObjectMapper();
    private static SimpleModule module = new SimpleModule();
    private final SecretKey secretKey;

    private boolean encryptize;

    /**
     * Json 管理器构造
     * <br><br>
     * 设置为不需要密钥
     */
    public JsonManager() {
        this.encryptize = false;
        this.secretKey = null;
        registerSerializers();
    }

    /**
     * Json 管理器构造
     * <br><br>
     * 通过给定的密钥字符串生成 SecretKey
     * @param key 密钥字符串
     */
    public JsonManager(String key) {
        this.encryptize = true;
        this.secretKey = generateSecretKey(key);
        registerSerializers();
    }

    /**
     * 注册自定义序列化器和反序列化器
     */
    public void registerSerializers() {
        module = module.addSerializer(SubMapData.class, new SubMapDataSerializer());
        module = module.addDeserializer(SubMapData.class, new SubMapDataDeserializer());
        objectMapper = objectMapper.registerModule(module);
    }

    /** 
     * 从字符串生成 SecretKey，长度为 16 字节
     * @param key 密钥
     */
    private SecretKey generateSecretKey(String key) {
        byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
        byte[] finalKey = new byte[AES_KEY_SIZE];
        // 填充至 16 字节，超长则截取前 16 字节
        System.arraycopy(keyBytes, 0, finalKey, 0, Math.min(keyBytes.length, AES_KEY_SIZE));
        return new SecretKeySpec(finalKey, ALGORITHM);
    }

    /**
     * 序列化对象为 Json 字符串
     * @param data 要进行序列化的对象
     * @return Json 字符串，失败返回 null
     */
    public String getJsonString(Object data) {
        try {
            // 将对象转换为 JSON 字符串
            String json = objectMapper.writeValueAsString(data);

            // 判定加密
            if (encryptize) {
                // 生成 SHA-256
                String hash = generateSHA256Hash(json);
                // 加密 JSON
                String encryptedJson = encrypt(json);
                // Base64编码加密 JSON 并与校验值合并保存
                json = "ENC" + Base64.getEncoder().encodeToString(encryptedJson.getBytes(StandardCharsets.UTF_8)) + ":" + hash;
            }            

            return json;

        } catch (Exception e) {
            Logger.error("JsonManager", String.format("Can't parse object %s to Json String because %s", data, e.getMessage()));
            return null;
        }
    }

    /**
     * 加载并解密 JSON 字符串
     * @param <T> 目标解析对象类型
     * @param json 将要解析的 Json
     * @param clazz 目标类类型
     * @return 解析结果，失败返回 null
     */
    public <T> T parseJsonToObject(String json, Class<T> clazz) {
        try {
            // 判断是否加密
            if (json.indexOf("ENC", 0) == 0) {

                String[] parts = json.replaceFirst("ENC", "").split(":");

                if (parts.length != 2) {
                    Logger.error(
                        "JsonManager", 
                        String.format("Json %s... is not a valid encrypted Json", json.substring(0, Math.min(20, json.length())))
                    );
                    return null;
                }

                String encryptedJson = new String(Base64.getDecoder().decode(parts[0]), StandardCharsets.UTF_8);
                String savedHash = parts[1];

                // 解密 JSON 数据
                String decryptedJson = decrypt(encryptedJson);

                // 生成解密后 JSON 的 SHA-256 校验值并验证
                String currentHash = generateSHA256Hash(decryptedJson);
                if (!currentHash.equals(savedHash)) {
                    Logger.error("JsonManager", String.format("Current Json %s... fail to pass sha256 verification. Expect %s, get %s", 
                        json.substring(0, Math.min(20, json.length())),
                        savedHash,
                        currentHash
                    ));
                    return null;
                }

                // 将 JSON 字符串转换为对象
                return objectMapper.readValue(decryptedJson, clazz);

            } else {
                return objectMapper.readValue(json, clazz);
            }
            
        } catch (Exception e) {
            if (json == null) json = "";
            Logger.error("JsonManager", String.format("Because '%s', fail to read Json %s", e.getMessage(), json, 500));
            return null;
        }
    }
    

    /**
     * 加密并保存 Json 数据到文件
     * @param filePath 将要写入的 Json 文件路径
     * @param data 将要解析的对象
     * @return 是否成功保存
     */
    public boolean saveJsonToFile(String filePath, Object data) {
        String fileContent = getJsonString(data);
        if (fileContent == null) return false;

        try {
            Files.write(Paths.get(filePath), fileContent.getBytes(StandardCharsets.UTF_8));
            return true;
        } catch (Exception e) {
            Logger.error("JsonManager", String.format("Can't write json string to file %s because %s", filePath, e.getMessage()));
            return false;
        }
    }

    /**
     * 加载并解密 JSON 字符串
     * @param <T> 目标解析对象类型
     * @param filePath 将要读取的 Json 文件路径
     * @param clazz 目标类类型
     * @return 解析结果，失败返回 null
     */
    public <T> T loadJsonfromFile(String filePath, Class<T> clazz) {
        try {
            String fileContent = new String(Files.readAllBytes(Paths.get(filePath)), StandardCharsets.UTF_8);
            return parseJsonToObject(fileContent, clazz);
        } catch (Exception e) {
            Logger.error("JsonManager", String.format("Can't read json string from file %s because %s", filePath, e.getMessage()));
            return null;
        }
    }

    /**
     * 对数据进行 AES 加密
     * @param data 文本数据
     * @return 加密文本
     * @throws Exception
     */
    private String encrypt(String data) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encryptedData = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encryptedData);
    }

    /**
     * 对数据进行 AES 解密
     * @param encryptedData 文本数据
     * @return 解密文本
     * @throws Exception
     */
    private String decrypt(String encryptedData) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] decodedData = Base64.getDecoder().decode(encryptedData);
        byte[] originalData = cipher.doFinal(decodedData);
        return new String(originalData, StandardCharsets.UTF_8);
    }

    /**
     * 生成 SHA-256 校验值
     * @param data 文本数据
     * @return SHA-256 摘要
     * @throws Exception
     */
    public String generateSHA256Hash(String data) throws Exception {
        MessageDigest digest = MessageDigest.getInstance(HASH_ALGORITHM);
        byte[] hash = digest.digest(data.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(hash);
    }
}
