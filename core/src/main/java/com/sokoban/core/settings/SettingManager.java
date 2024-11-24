package com.sokoban.core.settings;

import java.io.File;

import com.sokoban.core.JsonManager;
import com.sokoban.core.Logger;

/**
 * 游戏设置管理器
 * @author Life_Checkpoint
 */
public class SettingManager {
    private String settingsFilePath;
    public GameSettings gameSettings;

    public final String DEFAULT_SETTING_FILE_PATH = "./bin/settings/global.json";

    /**
     * 设置管理器构造
     */
    public SettingManager() {
        // 初始化游戏设置，读入设置文件后覆盖
        gameSettings = getDefaultGameSetting();

        // 设置文件路径
        setSettingsFilePath(DEFAULT_SETTING_FILE_PATH);

        // 尝试读入
        readSettings();
    }

    /**
     * 设置管理器构造
     * @param settingsFilePath 设置文件路径
     */
    public SettingManager(String settingsFilePath) {
        // 初始化游戏设置，读入设置文件后覆盖
        gameSettings = getDefaultGameSetting();

        // 设置文件路径
        setSettingsFilePath(settingsFilePath);

        // 尝试读入
        readSettings();
    }

    /**
     * 读取设置文件
     * <br><br>
     * 如果设置文件不存在则尝试创建默认设置文件
     */
    public void readSettings() {
        try {
            gameSettings = new JsonManager().loadEncryptedJson(settingsFilePath, GameSettings.class);
            Logger.info("SettingManager", "Read setting file " + settingsFilePath + "successfully");
            return;
        } catch (Exception e) {
            Logger.warning("SettingManager", "Can't load / create setting files because: " + e.getMessage());
            Logger.warning("SettingManager", "A default setting file will be created");
        }

        // 未读取成功，获取默认设置
        this.gameSettings = getDefaultGameSetting();
        // 创建默认设置失败，提示错误，仍然使用默认设置
        if (!writeSettings()) Logger.error("SettingManager", "Can't load & write any setting files");
    }

    /**
     * 根据当前设置创建设置文件
     * @return 创建是否成功
     */
    public boolean writeSettings() {
        File settingFile = new File(settingsFilePath);

        // 检测配置文件存在性
        if (settingFile.exists()) settingFile.delete();

        try {
            new JsonManager().saveEncryptedJson(settingsFilePath, gameSettings);
            return true;
        } catch (Exception e) {
            Logger.error("SettingManager", "can't create setting files. " + e);
            return false;
        }
    
    }

    public String getSettingsFilePath() {
        return settingsFilePath;
    }

    /**
     * 设置设置文件路径
     * @param settingsFilePath 设置文件路径
     */
    public void setSettingsFilePath(String settingsFilePath) {
        this.settingsFilePath = settingsFilePath;
        if (!new File(settingsFilePath).exists()) {
            Logger.warning("SettingManager", "Setting file " + settingsFilePath + "is not exists");
            Logger.warning("SettingManager", "A setting file will be created");
            writeSettings();
        }
    }

    /**
     * 获得默认游戏设置
     * @return 默认游戏设置
     */
    public GameSettings getDefaultGameSetting() {
        GameSettings defaultSetting = new GameSettings();

        defaultSetting.graphics.mipmap = true;
        defaultSetting.graphics.msaa = 2;
        defaultSetting.graphics.vsync = true;
        defaultSetting.sound.effectsVolume = 40;
        defaultSetting.sound.masterVolume = 50;
        defaultSetting.sound.musicVolume = 40;

        return defaultSetting;
    }
}
