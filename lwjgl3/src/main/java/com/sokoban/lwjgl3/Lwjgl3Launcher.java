package com.sokoban.lwjgl3;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.sokoban.CoreTest;
import com.sokoban.Main;
import com.sokoban.core.Logger;
import com.sokoban.core.MainConfig;
import com.sokoban.core.settings.SettingManager;

/** 启动 Sokoban Lwjgl3 前端 */
public class Lwjgl3Launcher {
    private static MainConfig mainConfig;

    /**
     * 入口函数
     * @param args 参数
     */
    public static void main(String[] args) {
        // 初始化主游戏类配置
        mainConfig = new MainConfig();
        
        // 检查启动模式
        mainConfig.runMode = MainConfig.RunModes.Normal;
        if (checkArgFlag(args, "--test")) mainConfig.runMode = MainConfig.RunModes.CoreTest;
        if (checkArgFlag(args, "--guitest")) mainConfig.runMode = MainConfig.RunModes.GuiTest;

        // 检查分析器启用
        mainConfig.enableGLProfiler = checkArgFlag(args, "--profile");
        
        // 尝试设置载入
        mainConfig.settingManager = new SettingManager("./settings/global.json");

        // 启动对应前端
        Logger.debug("Lwjgl3Launcher", "Start mode: " + mainConfig.runMode);

        if (mainConfig.runMode == MainConfig.RunModes.Normal || mainConfig.runMode == MainConfig.RunModes.GuiTest) {
            // GUI 测试 / 正常启动
            if (StartupHelper.startNewJvmIfRequired()) return;
            createApplication(new Main(mainConfig));
            
        } else if (mainConfig.runMode == MainConfig.RunModes.CoreTest) {
            // 核心测试
            CoreTest.main();
        }
    }

    /**
     * 应用程序创建
     * @param entry 入口点
     * @return Lwjgl3 应用程序对象
     */
    private static Lwjgl3Application createApplication(ApplicationListener entry) {
        return new Lwjgl3Application(entry, getDefaultConfiguration());
    }

    /**
     * 配置 Lwjgl3 应用信息
     * @return Lwjgl3 应用信息
     */
    private static Lwjgl3ApplicationConfiguration getDefaultConfiguration() {
        Lwjgl3ApplicationConfiguration configuration = new Lwjgl3ApplicationConfiguration();

        // 初始化前端 GUI
        configuration.setTitle("Sokoban");
        configuration.useVsync(mainConfig.settingManager.gameSettings.graphics.vsync);
        configuration.setForegroundFPS(Lwjgl3ApplicationConfiguration.getDisplayMode().refreshRate + 1);
        configuration.setWindowedMode(1920, 1080);
        configuration.setResizable(false);
        configuration.setWindowIcon("img/sokoban_icon.png");
        configuration.setBackBufferConfig(8, 8, 8, 8, 16, 0, mainConfig.settingManager.gameSettings.graphics.msaa);
        return configuration;
    }

    /** 检查参数存在性 */
    private static boolean checkArgFlag(String[] args, String arg) {
        for (String toCheckArg : args) {
            if (toCheckArg.equals(arg)) return true;
        }
        return false;
    }
}