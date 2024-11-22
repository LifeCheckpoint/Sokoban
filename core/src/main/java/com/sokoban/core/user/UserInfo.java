package com.sokoban.core.user;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import com.sokoban.core.Logger;

/**
 * 用户信息类
 * @author Life_Checkpoint
 * @author ChatGPT
 */
public class UserInfo {
    private String userID;
    private String userPasswordHash;
    private Boolean rememberPassword;
    private List<SaveArchiveInfo> saveArhives;

    public UserInfo() {
        userID = "";
        userPasswordHash = "";
        rememberPassword = false;
        saveArhives = new ArrayList<>();
    }

    public UserInfo(String userID, String userPasswordHash, boolean rememberPassword) {
        this.userID = userID;
        this.userPasswordHash = userPasswordHash;
        this.rememberPassword = rememberPassword;
        this.saveArhives = new ArrayList<>();
    }

    public String calculatePasswordHash(String password) {
        if (password == null || password.isEmpty()) {
            Logger.error("UserInfo", "Password cannot be null or empty");
        }

        try {
            // 创建 SHA-256 消息摘要实例
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            // 将字符串转换为字节数组并计算哈希值
            byte[] hashBytes = digest.digest(password.getBytes());

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

    public String getUserID() {
        return userID;
    }
    public void setUserID(String userID) {
        this.userID = userID;
    }
    public String getUserPasswordHash() {
        return userPasswordHash;
    }
    public void setUserPasswordHash(String userPasswordHash) {
        this.userPasswordHash = userPasswordHash;
    }
    public Boolean getRememberPassword() {
        return rememberPassword;
    }
    public void setRememberPassword(Boolean rememberPassword) {
        this.rememberPassword = rememberPassword;
    }
    public List<SaveArchiveInfo> getSaveArhives() {
        return saveArhives;
    }
    public void setSaveArhives(List<SaveArchiveInfo> saveArhives) {
        this.saveArhives = saveArhives;
    }

}
