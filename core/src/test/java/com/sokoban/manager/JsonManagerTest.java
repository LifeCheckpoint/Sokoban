package com.sokoban.manager;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.sokoban.core.json.JsonManager;

import java.io.File;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * 测试加解密 Json 文件数据模块功能
 * <br><br>
 * 较老的测试，请在测试前删除相关 Json 结果防止错误
 * @author Claude
 */
public class JsonManagerTest {

    /**
     * 测试短密钥 Json 加解密
     */
    @Test
    public void testJsonEncryptionDecryption() {
        // Step 1: 创建一个示例数据对象（比如一个 Map）
        Map<String, String> data = new HashMap<>();
        data.put("name", "John Doe");
        data.put("age", "30");
        data.put("city", "New York");

        // Step 2: 使用 JsonManager 进行加密
        String key = "MAKE JSON GREAT AGAIN!@#$%^&*()_";
        JsonManager jsonManager = new JsonManager(key);

        // 指定文件路径（保存到当前测试目录）
        String filePath = "test-encrypted.json";

        try {
            // 将数据加密并保存到文件
            jsonManager.saveJsonToFile(filePath, data);

            // Step 3: 验证未被篡改的文件
            @SuppressWarnings("all")
            Map<String, String> decryptedData = jsonManager.loadJsonfromFile(filePath, Map.class);
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
            Assert.assertNull(jsonManager.loadJsonfromFile(filePath, Map.class), "Expected null due to file tampering"); 

            // Step 5: 删除篡改的文件内容并恢复
            Files.write(file.toPath(), originalContent.getBytes());

            // Step 6: 验证文件不存在的情况
            File nonExistentFile = new File("non-existent-file.json");
            Assert.assertNull(jsonManager.loadJsonfromFile(nonExistentFile.getAbsolutePath(), Map.class), "Expected null due to file not found");

            // Step 7: 处理测试文件
            if (file.exists()) {
                File fileResultDirectory = new File("test-files");
                if (!fileResultDirectory.exists()) fileResultDirectory.mkdir();
                File fileResult = new File("./test-files/test-encrypted.json");
                if (fileResult.exists()) fileResult.delete();
                if (file.exists()) {
                    file.renameTo(fileResult);
                    file.delete();
                }
            }

        } catch (Exception e) {
            Assert.fail("Test failed due to exception: " + e.getMessage(), e);
        }
    }

    /**
     * 测试长密钥 Json 加解密
     */
    @Test
    public void testLongJsonEncryptionDecryption() {
        // Step 1: 创建一个示例数据对象（比如一个 Map）
        Map<String, String> data = new HashMap<>();
        data.put("name", "John Doe");
        data.put("age", "30");
        data.put("city", "New York");

        // Step 2: 使用 JsonManager 进行加密
        String key = new StringBuilder().repeat("TestScreteKey123456", 200).toString();
        JsonManager jsonManager = new JsonManager(key);

        // 指定文件路径（保存到当前测试目录）
        String filePath = "test-encrypted-long.json";

        try {
            // 将数据加密并保存到文件
            jsonManager.saveJsonToFile(filePath, data);

            // Step 3: 验证未被篡改的文件
            @SuppressWarnings("all")
            Map<String, String> decryptedData = jsonManager.loadJsonfromFile(filePath, Map.class);
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
            Assert.assertNull(jsonManager.loadJsonfromFile(filePath, Map.class), "Expected exception due to file tampering");

            // Step 5: 删除篡改的文件内容并恢复
            Files.write(file.toPath(), originalContent.getBytes());

            // Step 7: 处理测试文件
            if (file.exists()) {
                File fileResultDirectory = new File("test-files");
                if (!fileResultDirectory.exists()) fileResultDirectory.mkdir();
                File fileResult = new File("./test-files/test-encrypted-long.json");
                if (fileResult.exists()) fileResult.delete();
                if (file.exists()) {
                    file.renameTo(fileResult);
                    file.delete();
                }
            }

        } catch (Exception e) {
            Assert.fail("Test failed due to exception: " + e.getMessage(), e);
        }
    }

    /**
     * 测试普通 Json
     */
    @Test
    public void testJson() {
        // Step 1: 创建一个示例数据对象（比如一个 Map）
        Map<String, String> data = new HashMap<>();
        data.put("name", "John Doe");
        data.put("age", "30");
        data.put("city", "New York");

        JsonManager jsonManager = new JsonManager();

        // 指定文件路径（保存到当前测试目录）
        String filePath = "test-purejson.json";

        try {
            // 将数据加密并保存到文件
            jsonManager.saveJsonToFile(filePath, data);

            // 验证未被篡改的文件
            @SuppressWarnings("all")
            Map<String, String> decryptedData = jsonManager.loadJsonfromFile(filePath, Map.class);
            Assert.assertEquals(decryptedData, data, "Decrypted data does not match original data");

            File file = new File(filePath);

            // 验证文件不存在情况
            File nonExistentFile = new File("non-existent-file.json");
            Assert.assertNull(jsonManager.loadJsonfromFile(nonExistentFile.getAbsolutePath(), Map.class), "Expected exception due to file not found");

            // Step 7: 处理测试文件
            if (file.exists()) {
                File fileResultDirectory = new File("test-files");
                if (!fileResultDirectory.exists()) fileResultDirectory.mkdir();
                File fileResult = new File("./test-files/test-purejson.json");
                if (fileResult.exists()) fileResult.delete();
                if (file.exists()) {
                    file.renameTo(fileResult);
                    file.delete();
                }
            }

        } catch (Exception e) {
            Assert.fail("Test failed due to exception: " + e.getMessage(), e);
        }
    }
}
