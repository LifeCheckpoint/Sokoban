package com.sokoban.core.user;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.sokoban.core.JsonManager;
import com.sokoban.core.Logger;

/**
 * 用户管理类，提供用户管理功能
 * @author Life_Checkpoint
 */
public class UserManager {
    private String userInfosRootPath;

    private String DEFAULT_USER_INFOS_PATH = "./bin/usr";
    private String NULL_PASSWORD_PLACEHOLDER = "<NullPassword>";

    public UserManager() {
        this.userInfosRootPath = DEFAULT_USER_INFOS_PATH;
    }

    public UserManager(String userInfosPath) {
        this.userInfosRootPath = userInfosPath;
    }

    /**
     * 读取用户信息
     * @param userName 用户名
     * @return 用户信息，不存在返回 null
     * @throws Exception
     */
    public UserInfo readUserInfo(String userName) throws Exception {
        try {
            // 测试根目录存在性，不存在则创建
            if (!testUserInfosPathWithCreate()) return null;

        } catch (Exception e) {
            Logger.error("UserManager", e.getMessage());
            throw e;
        }

        try {
            // 使用用户名作为加密密钥，读取相应目录下用户文件
            String userInfoPath = Paths.get(userInfosRootPath, userName + ".usr").toString();
            
            // 用户文件不存在
            if (!new File(userInfoPath).exists()) return null;

            UserInfo currentUserInfo = new JsonManager(userName).loadEncryptedJson(userInfoPath, UserInfo.class);
            return currentUserInfo;

        } catch (Exception e) {
            Logger.error("UserManager", e.getMessage());
            return null;
        }
    }

    /**
     * 创建用户信息，如果重复则返回 false
     * @param userInfo 用户信息
     * @return 创建是否成功
     * @throws Exception
     */
    public boolean createUserInfo(UserInfo userInfo) throws Exception {
        try {
            // 测试根目录存在性，不存在则创建
            testUserInfosPathWithCreate();

            if (!isValidUserInfo(userInfo)) {
                Logger.error("UserManager", "Not a valid userInfo object");
                return false;
            }

            // 使用用户名作为加密密钥，创建相应目录下用户文件
            String userInfoPath = Paths.get(userInfosRootPath, userInfo.getUserID() + ".usr").toString();

            // 若重复创建，返回假
            if (new File(userInfoPath).exists()) {
                Logger.warning("UserManager", String.format("User %s has be created, ignore current creating", userInfo.getUserID()));
                return false;
            }

            new JsonManager(userInfo.getUserID()).saveEncryptedJson(userInfoPath, userInfo);

            return true;

        } catch (Exception e) {
            Logger.error("UserManager", e.getMessage());
            throw e;
        }
    }

    /**
     * 测试用户配置目录是否存在，若不存在则尝试创建
     * @return 目录存在性，不存在则返回 false，无论是否存在都会进行创建
     * @throws Exception 创建目录异常
     */
    public synchronized boolean testUserInfosPathWithCreate() throws Exception {
        try {
            File userInfoPathObj = new File(userInfosRootPath);

            // 不存在则创建目录
            if (!userInfoPathObj.exists()) {
                Logger.warning("UserManager", String.format("User infos directory %s is not exists. It will be created.", userInfosRootPath));
                userInfoPathObj.mkdirs();
                return false;
            }

            return true;
            
        } catch (Exception e) {
            throw new Exception(String.format("Cannot create user infos directory %s:", userInfosRootPath) + e.getMessage(), e);
        }
    }

    /**
     * 判定是否为合法用户信息文件
     * @param userInfo 用户信息文件
     * @return 合法性
     */
    public boolean isValidUserInfo(UserInfo userInfo) {
        if (userInfo.isRememberPassword() == null || userInfo.getUserID() == null || 
            userInfo.getSaveArchives() == null|| userInfo.getUserPasswordHash() == null) return false;
        if (userInfo.getUserID().length() <= 1 || userInfo.getUserID().length() >= 30) return false;
        if (userInfo.getUserPasswordHash().equals("")) return false;
        
        return true;
    }

    /**
     * 计算密码对应哈希摘要
     * @param password 原文
     * @return SHA-256 摘要
     */
    public String calculatePasswordHash(String password) {
        if (password == null || password.isEmpty()) {
            Logger.warning("UserInfo", "Password is null or empty, translate to placeholder");
            password = NULL_PASSWORD_PLACEHOLDER;
        }

        try {
            // 创建 SHA-256 消息摘要实例
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            // 将字符串转换为字节数组并计算哈希值
            byte[] hashBytes = digest.digest(password.getBytes(StandardCharsets.UTF_8));

            // 将字节数组转换为十六进制字符串
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error calculating SHA-256 hash", e);
        }
    }

    /**
     * 测试密码是否正确
     * @param userInfo 用户信息
     * @param password 密码原文
     * @return 密码是否正确
     */
    public boolean testPassword(UserInfo userInfo, String password) {
        // 空密码
        if (calculatePasswordHash(password).equals(calculatePasswordHash(NULL_PASSWORD_PLACEHOLDER))) return true;
        else return userInfo.getUserPasswordHash().equals(calculatePasswordHash(password));
    }

    public String getUserInfosRootPath() {
        return userInfosRootPath;
    }

    public void setUserInfosRootPath(String userInfosPath) {
        this.userInfosRootPath = userInfosPath;
    }
    
}
