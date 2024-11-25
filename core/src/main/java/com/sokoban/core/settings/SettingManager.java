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

    public final String DEFAULT_SETTING_PATH = "./bin/settings";
    public final String DEFAULT_SETTING_FILE_NAME = "global.json";

    /**
     * 设置管理器构造
     */
    public SettingManager() {
        // 初始化游戏设置，读入设置文件后覆盖
        gameSettings = getDefaultGameSetting();

        // 设置文件路径
        setSettingsFilePath(DEFAULT_SETTING_PATH + "/" + DEFAULT_SETTING_FILE_NAME);

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
        gameSettings = new JsonManager().loadJsonfromFile(settingsFilePath, GameSettings.class);

        // 检查读取成功
        if (gameSettings != null) {
            Logger.info("SettingManager", "Read setting file " + settingsFilePath + " successfully");
            return;
        }
        
        Logger.warning("SettingManager", "Can't load / create setting files");
        Logger.warning("SettingManager", "A default setting file will be created");

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
        Logger.debug("SettingManager", "Written settings: " + new JsonManager().getJsonString(gameSettings));

        // 检测目录存在性
        if (!new File(settingFile.getPath()).exists()) {
            Logger.warning("SettingManager", "Directory " + settingFile.getPath() + " is Not avaliable. Try to create.");
            if (new File(settingFile.getPath()).mkdirs()) {
                Logger.info("SettingManager", "Directory " + settingFile.getPath() + " made successfully");
            } else {
                Logger.error("SettingManager", "Directory " + settingFile.getPath() + " made failed");
                return false;
            }
        }

        // 检测配置文件存在性
        if (settingFile.exists()) settingFile.delete();

        if (new JsonManager().saveJsonToFile(settingsFilePath, gameSettings)) {
            Logger.info("SettingManager", "Create setting files successfully");
            return true;
        } else {
            Logger.error("SettingManager", "Can't create setting files");
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
            Logger.warning("SettingManager", "Setting file " + settingsFilePath + " is not exists");
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
        defaultSetting.sound.effectsVolume = 0.4f;
        defaultSetting.sound.masterVolume = 0.5f;
        defaultSetting.sound.musicVolume = 0.4f;

        return defaultSetting;
    }
}
