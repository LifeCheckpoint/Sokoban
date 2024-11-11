package com.sokoban.core.settings;

import java.io.File;

import com.badlogic.gdx.Gdx;
import com.sokoban.core.JsonManager;

public class SettingManager {
    private String settingsFilePath;
    public GameSettings gameSettings;

    public SettingManager(String settingsFilePath) {
        setSettingsFilePath(settingsFilePath);
        readSettings();
    }

    public void readSettings() {
        // TODO
        try {
            gameSettings = new JsonManager().loadEncryptedJson(settingsFilePath, GameSettings.class);
        } catch (Exception e) {
            Gdx.app.error("SettingManager", "can't load / create setting files. " + e);
            System.exit(-1);
        }
    }

    public boolean writeSettings() {
        File settingFile = new File(settingsFilePath);

        // 检测配置文件存在性
        if (settingFile.exists()) settingFile.delete();

        try {
            new JsonManager().saveEncryptedJson(settingsFilePath, gameSettings);
            return true;
        } catch (Exception e) {
            Gdx.app.error("SettingManager", "can't create setting files. " + e);
            return false;
        }
    
    }

    public String getSettingsFilePath() {
        return settingsFilePath;
    }

    public void setSettingsFilePath(String settingsFilePath) {
        // TODO
        this.settingsFilePath = settingsFilePath;
        if (!new File(settingsFilePath).exists()) Gdx.app.log("SettingManager", "Setting file " + settingsFilePath + "is not exists.");
    }
}


