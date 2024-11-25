package com.sokoban.core.user;

import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * 用户信息类
 * @author Life_Checkpoint
 * @author ChatGPT
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserInfo {
    private String userID;
    private String userPasswordHash;
    private Boolean rememberPassword;
    private Boolean guest;
    private Boolean tutorial;
    private List<SaveArchiveInfo> saveArchives;

    public UserInfo() {
        saveArchives = new ArrayList<>();
        this.guest = true;
    }

    public UserInfo(String userID, String userPasswordHash, boolean rememberPassword) {
        this.userID = userID.toLowerCase();
        this.userPasswordHash = userPasswordHash;
        this.rememberPassword = rememberPassword;
        this.guest = false;
        this.tutorial = false;
        this.saveArchives = new ArrayList<>();
    }

    public String getUserID() {
        return userID.toLowerCase();
    }
    public void setUserID(String userID) {
        this.userID = userID.toLowerCase();
    }
    public String getUserPasswordHash() {
        return userPasswordHash;
    }
    public void setUserPasswordHash(String userPasswordHash) {
        this.userPasswordHash = userPasswordHash;
    }
    public Boolean isRememberPassword() {
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
    public Boolean isGuest() {
        return guest;
    }
    public void setGuest(Boolean guest) {
        this.guest = guest;
    }
    public Boolean isTutorial() {
        return tutorial;
    }
    public void setTutorial(Boolean tutorial) {
        this.tutorial = tutorial;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof UserInfo) {
            UserInfo otherUserInfo = (UserInfo) obj;
            if (!userID.equals(otherUserInfo.userID)) return false;
            if (!userPasswordHash.equals(otherUserInfo.getUserPasswordHash())) return false;
            if (!rememberPassword.equals(otherUserInfo.rememberPassword)) return false;
            if (!saveArchives.equals(otherUserInfo.getSaveArchives())) return false;
            if (!tutorial.equals(otherUserInfo.isTutorial())) return false;
            if (!guest.equals(otherUserInfo.isGuest())) return false;

            return true;
        } else return false;
    }

}
