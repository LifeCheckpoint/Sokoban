package com.sokoban.core.user;

import com.sokoban.utils.FilePathUtils;
import com.sokoban.core.game.Logger;
import com.sokoban.core.json.JsonManager;

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
     */
    public UserInfo readUserInfo(String userName) {
        userName = userName.toLowerCase();
        try {
            // 测试根目录存在性，不存在则创建
            if (!testUserInfosPathWithCreate()) return null;

        } catch (Exception e) {
            Logger.error("UserManager", e.getMessage());
            return null;
        }

        try {
            // 使用用户名作为加密密钥，读取相应目录下用户文件
            String userInfoPath = FilePathUtils.combine(userInfosRootPath, userName + ".usr");
            // 用户文件不存在
            if (!FilePathUtils.exists(userInfoPath)) return null;

            UserInfo currentUserInfo = new JsonManager(userName).loadJsonfromFile(userInfoPath, UserInfo.class);
            Logger.debug("UserManager", "User data : " + new JsonManager().getJsonString(currentUserInfo));
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
     */
    public boolean createUserInfo(UserInfo userInfo) {
        return createUserInfo(userInfo, false);
    }

    /**
     * 创建用户信息
     * @param userInfo 用户信息
     * @param force 强制覆盖
     * @return 创建是否成功
     */
    public boolean createUserInfo(UserInfo userInfo, boolean force) {
        // 测试根目录存在性，不存在则创建
        testUserInfosPathWithCreate();

        if (!isValidUserInfo(userInfo)) {
            Logger.error("UserManager", "Not a valid userInfo object");
            return false;
        }

        // 使用用户名作为加密密钥，创建相应目录下用户文件
        String userInfoPath = FilePathUtils.combine(userInfosRootPath, userInfo.getUserID() + ".usr");

        // 若重复创建，返回假
        if (FilePathUtils.exists(userInfoPath)) {
            Logger.warning("UserManager", String.format("User %s has be created, ignore current creating", userInfo.getUserID()));
            return false;
        }

        new JsonManager(userInfo.getUserID()).saveJsonToFile(userInfoPath, userInfo);
        return true;
    }

    /**
     * 更新用户信息
     * @param userInfo
     * @return 是否更新成功，用户不存在则返回 false
     */
    public boolean updateUserInfo(UserInfo userInfo) {
        // 测试根目录存在性，不存在则创建
        testUserInfosPathWithCreate();

        if (!isValidUserInfo(userInfo)) {
            Logger.error("UserManager", "Not a valid userInfo object");
            return false;
        }

        // 使用用户名作为加密密钥，创建相应目录下用户文件
        String userInfoPath = FilePathUtils.combine(userInfosRootPath, userInfo.getUserID() + ".usr");

        // 若不存在用户，返回假
        if (!FilePathUtils.exists(userInfoPath)) {
            Logger.warning("UserManager", String.format("User %s is not created", userInfo.getUserID()));
            return false;
        }

        new JsonManager(userInfo.getUserID()).saveJsonToFile(userInfoPath, userInfo);
        return true;
    }

    /**
     * 删除用户目录下的用户
     * @param userID 用户名
     * @return 删除成功性，若不存在等返回 false
     */
    public boolean deleteUserInfo(String userName) {
        // 测试根目录存在性，不存在则创建
        testUserInfosPathWithCreate();

        String userInfoPath = FilePathUtils.combine(userInfosRootPath, userName + ".usr");
        return FilePathUtils.delete(userInfoPath);
    }

    /**
     * 测试用户配置目录是否存在，若不存在则尝试创建
     * @return 目录存在性，不存在则返回 false，无论是否存在都会进行创建
     */
    public synchronized boolean testUserInfosPathWithCreate() {
        try {
            // 不存在则创建目录
            if (!FilePathUtils.exists(userInfosRootPath)) {
                Logger.warning("UserManager", String.format("User infos directory %s is not exists. It will be created.", userInfosRootPath));
                FilePathUtils.createDirectories(userInfosRootPath);
                return false;
            }
            return true;

        } catch (Exception e) {
            Logger.error("UserManager", String.format("Cannot create user infos directory %s because %s", userInfosRootPath, e.getMessage()));
            return false;
        }
    }

    /**
     * 判定是否为合法用户信息文件
     * @param userInfo 用户信息文件
     * @return 合法性
     */
    public boolean isValidUserInfo(UserInfo userInfo) {
        if (
            userInfo.isRememberPassword() == null ||
            userInfo.getUserID() == null ||
            userInfo.getSaveArchives() == null ||
            userInfo.getUserPasswordHash() == null
        ) return false;
        if (userInfo.getUserID().length() <= 1 || userInfo.getUserID().length() >= 30) return false;
        if (userInfo.getUserPasswordHash().equals("")) return false;

        return true;
    }

    /**
     * 计算密码对应哈希摘要
     * @param password 原文
     * @return SHA-256 摘要，失败返回 null
     */
    public String calculatePasswordHash(String password) {
        if (password == null || password.isEmpty()) {
            Logger.warning("UserInfo", "Password is null or empty, translate to placeholder");
            password = NULL_PASSWORD_PLACEHOLDER;
        }

        try {
            String hash = new JsonManager().generateSHA256Hash(password);
            return hash;
        } catch (Exception e) {
            Logger.error("UserManager", "Error calculating password's hash");
            return null;
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
        else {
            Logger.debug("UserManager", "Password Text's pwd Hash: " + calculatePasswordHash(password));
            Logger.debug("UserManager", "User Info's pwd Hash: " + userInfo.getUserPasswordHash());
            return userInfo.getUserPasswordHash().equals(calculatePasswordHash(password));
        }
    }

    public String getUserInfosRootPath() {
        return userInfosRootPath;
    }

    public void setUserInfosRootPath(String userInfosPath) {
        this.userInfosRootPath = userInfosPath;
    }
}
