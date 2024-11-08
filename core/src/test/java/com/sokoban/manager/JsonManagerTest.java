package com.sokoban.manager;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class JsonManagerTest {

    @Test
    public void testJsonEncryptionDecryption() {
        // Step 1: 创建一个示例数据对象（比如一个 Map）
        Map<String, String> data = new HashMap<>();
        data.put("name", "John Doe");
        data.put("age", "30");
        data.put("city", "New York");

        // Step 2: 使用 JsonManager 进行加密
        String key = "mysecretkey"; // 你可以使用任何自定义密钥
        JsonManager jsonManager = new JsonManager(key);

        // 指定文件路径（保存到当前测试目录）
        String filePath = "test-encrypted.json";

        try {
            // 将数据加密并保存到文件
            jsonManager.saveEncryptedJson(filePath, data);

            // Step 3: 验证未被篡改的文件
            @SuppressWarnings("all")
            Map<String, String> decryptedData = jsonManager.loadEncryptedJson(filePath, Map.class);
            Assert.assertEquals(decryptedData, data, "Decrypted data does not match original data");

            // Step 4: 篡改文件内容并验证
            File file = new File(filePath);
            String originalContent = new String(Files.readAllBytes(file.toPath()));

            // 模拟篡改：随机修改 Base64 字符串中的某些字符
            Random random = new Random();
            char[] contentArray = originalContent.toCharArray();
            int changeIndex = random.nextInt(contentArray.length);  // 随机选择一个位置
            contentArray[changeIndex] = (char) (random.nextInt(26) + 'A'); // 修改为随机大写字母
            String tamperedContent = new String(contentArray);

            // 将篡改后的内容写回文件
            Files.write(file.toPath(), tamperedContent.getBytes());

            // 尝试加载被篡改的文件并验证抛出异常
            try {
                jsonManager.loadEncryptedJson(filePath, Map.class);
                Assert.fail("Expected exception due to file tampering");
            } catch (Exception e) {}

            // Step 5: 删除篡改的文件内容并恢复
            Files.write(file.toPath(), originalContent.getBytes());

            // Step 6: 验证文件不存在的情况
            File nonExistentFile = new File("non-existent-file.json");
            try {
                jsonManager.loadEncryptedJson(nonExistentFile.getAbsolutePath(), Map.class);
                Assert.fail("Expected exception due to file not found");
            } catch (Exception e) {}

            // Step 7: 删除测试文件
            if (file.exists()) {
                file.delete();
            }

        } catch (Exception e) {
            Assert.fail("Test failed due to exception: " + e.getMessage(), e);
        }
    }

    @Test
    public void testLongJsonEncryptionDecryption() {
        // Step 1: 创建一个示例数据对象（比如一个 Map）
        Map<String, String> data = new HashMap<>();
        data.put("name", "John Doe");
        data.put("age", "30");
        data.put("city", "New York");

        // Step 2: 使用 JsonManager 进行加密
        String key = new StringBuilder().repeat("mysecretkey", 200).toString(); // 你可以使用任何自定义密钥
        JsonManager jsonManager = new JsonManager(key);

        // 指定文件路径（保存到当前测试目录）
        String filePath = "test-encrypted.json";

        try {
            // 将数据加密并保存到文件
            jsonManager.saveEncryptedJson(filePath, data);

            // Step 3: 验证未被篡改的文件
            @SuppressWarnings("all")
            Map<String, String> decryptedData = jsonManager.loadEncryptedJson(filePath, Map.class);
            Assert.assertEquals(decryptedData, data, "Decrypted data does not match original data");

            // Step 4: 篡改文件内容并验证
            File file = new File(filePath);
            String originalContent = new String(Files.readAllBytes(file.toPath()));

            // 模拟篡改：随机修改 Base64 字符串中的某些字符
            Random random = new Random();
            char[] contentArray = originalContent.toCharArray();
            int changeIndex = random.nextInt(contentArray.length);  // 随机选择一个位置
            contentArray[changeIndex] = (char) (random.nextInt(26) + 'A'); // 修改为随机大写字母
            String tamperedContent = new String(contentArray);

            // 将篡改后的内容写回文件
            Files.write(file.toPath(), tamperedContent.getBytes());

            // 尝试加载被篡改的文件并验证抛出异常
            try {
                jsonManager.loadEncryptedJson(filePath, Map.class);
                Assert.fail("Expected exception due to file tampering");
            } catch (Exception e) {}

            // Step 5: 删除篡改的文件内容并恢复
            Files.write(file.toPath(), originalContent.getBytes());

            // Step 6: 验证文件不存在的情况
            File nonExistentFile = new File("non-existent-file.json");
            try {
                jsonManager.loadEncryptedJson(nonExistentFile.getAbsolutePath(), Map.class);
                Assert.fail("Expected exception due to file not found");
            } catch (Exception e) {}

            // Step 7: 删除测试文件
            if (file.exists()) {
                file.delete();
            }

        } catch (Exception e) {
            Assert.fail("Test failed due to exception: " + e.getMessage(), e);
        }
    }
}
