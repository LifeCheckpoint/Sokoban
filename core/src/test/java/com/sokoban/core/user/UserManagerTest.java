package com.sokoban.core.user;

import org.testng.annotations.*;
import static org.testng.Assert.*;

import java.io.File;

/**
 * 测试 UserManager 功能
 * @author Claude
 */
public class UserManagerTest {
    private static final String TEST_USER_DIR = "./test-files/bin/usr";
    private UserManager userManager;

    @BeforeClass
    public void setUp() {
        // 如果目录存在则删除
        File testDir = new File(TEST_USER_DIR);
        if (testDir.exists()) {
            deleteDirectory(testDir);
        }
        
        // 初始化
        userManager = new UserManager(TEST_USER_DIR);
    }

    private void deleteDirectory(File directory) {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteDirectory(file);
                } else {
                    file.delete();
                }
            }
        }
        directory.delete();
    }

    @Test
    public void testValidUsers() throws Exception {
        // 合法用户1
        UserInfo user1 = new UserInfo("testUser1", 
            userManager.calculatePasswordHash("password123"), true);
        assertTrue(userManager.createUserInfo(user1), "Failed to create first user");

        // 合法用户2
        UserInfo user2 = new UserInfo("testUser2", 
            userManager.calculatePasswordHash("differentPass"), false);
        assertTrue(userManager.createUserInfo(user2), "Failed to create second user");

        // 测试是否相等
        UserInfo readUser1 = userManager.readUserInfo("testUser1");
        UserInfo readUser2 = userManager.readUserInfo("testUser2");

        assertNotNull(readUser1, "Failed to read first user");
        assertNotNull(readUser2, "Failed to read second user");
        assertEquals(readUser1, user1, "First user data mismatch");
        assertEquals(readUser2, user2, "Second user data mismatch");
    }

    @Test
    public void testEmptyPasswordUser() throws Exception {
        UserInfo emptyPassUser = new UserInfo("emptyPassUser", 
            userManager.calculatePasswordHash(""), true);
        assertTrue(userManager.createUserInfo(emptyPassUser), "Failed to create empty password user");

        UserInfo readUser = userManager.readUserInfo("emptyPassUser");
        assertNotNull(readUser, "Failed to read empty password user");
        assertEquals(readUser, emptyPassUser, "Empty password user data mismatch");
        assertTrue(userManager.testPassword(readUser, ""), "Empty password verification failed");
    }

    @Test
    public void testInvalidUsers() {
        // 太短 ID
        UserInfo invalidUser1 = new UserInfo("a", 
            userManager.calculatePasswordHash("pass"), true);
        assertFalse(userManager.isValidUserInfo(invalidUser1), "Short username should be invalid");

        // 太长 ID
        String longUsername = "thisIsAVeryLongUsernameThatExceedsTheLimit";
        UserInfo invalidUser2 = new UserInfo(longUsername, 
            userManager.calculatePasswordHash("pass"), true);
        assertFalse(userManager.isValidUserInfo(invalidUser2), "Long username should be invalid");
    }

    @Test
    public void testDuplicateUser() throws Exception {
        // 初始用户
        UserInfo originalUser = new UserInfo("duplicateTest", 
            userManager.calculatePasswordHash("password"), true);
        assertTrue(userManager.createUserInfo(originalUser), "Failed to create original user");

        // 尝试创建重复用户
        UserInfo duplicateUser = new UserInfo("duplicateTest", 
            userManager.calculatePasswordHash("different"), false);
        assertFalse(userManager.createUserInfo(duplicateUser), "Duplicate user creation should fail");
    }

    @Test
    public void testNonExistentUser() throws Exception {
        // 不存在用户测试
        UserInfo nonExistentUser = userManager.readUserInfo("nonExistentUser");
        assertNull(nonExistentUser, "Non-existent user should return null");
    }

    // @AfterClass
    // public void tearDown() {
    //     // Clean up test directory
    //     File testDir = new File(TEST_USER_DIR);
    //     if (testDir.exists()) {
    //         deleteDirectory(testDir);
    //     }
    // }
}