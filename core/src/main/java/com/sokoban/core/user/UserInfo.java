package com.sokoban.core.user;

import java.util.ArrayList;
import java.util.List;

/**
 * 用户信息类
 * @author Life_Checkpoint
 * @author ChatGPT
 */
public class UserInfo {
    private String userID;
    private String userPasswordHash;
    private Boolean rememberPassword;
    private List<SaveArchiveInfo> saveArchives;

    public UserInfo() {
        saveArchives = new ArrayList<>();
    }

    public UserInfo(String userID, String userPasswordHash, boolean rememberPassword) {
        this.userID = userID;
        this.userPasswordHash = userPasswordHash;
        this.rememberPassword = rememberPassword;
        this.saveArchives = new ArrayList<>();
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
    public List<SaveArchiveInfo> getSaveArchives() {
        return saveArchives;
    }
    public void setSaveArchives(List<SaveArchiveInfo> saveArhives) {
        this.saveArchives = saveArhives;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof UserInfo) {
            UserInfo otherUserInfo = (UserInfo) obj;
            if (!userID.equals(otherUserInfo.userID)) return false;
            if (!userPasswordHash.equals(otherUserInfo.getUserPasswordHash())) return false;
            if (!rememberPassword.equals(otherUserInfo.rememberPassword)) return false;
            if (!saveArchives.equals(otherUserInfo.getSaveArchives())) return false;

            return true;
        } else return false;
    }

}
