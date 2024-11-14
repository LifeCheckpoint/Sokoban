package com.sokoban.lwjgl3;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.sokoban.CoreTest;
import com.sokoban.Main;

/** Launches Sokoban. */
public class Lwjgl3Launcher {
    /**
     * 程序启动方式
     * 0 = 正常 GUI 启动
     * 1 = 核心测试 CoreTest
     * 2 = 场景测试 SceneTest
     */
    static int runMode = 0;
    public static void main(String[] args) {
        
        // 检查启动参数
        for (String arg : args) {
            if (arg.equals("--test")) {
                runMode = 1;
                break;
            }

            if (arg.equals("--guitest")) {
                runMode = 2;
                break;
            }
        }

        if (runMode == 0 || runMode == 2) {
            if (StartupHelper.startNewJvmIfRequired()) return;
            createApplication(new Main(runMode));
            
        } else if (runMode == 1) {
            CoreTest.main();
        }
    }

    private static Lwjgl3Application createApplication(ApplicationListener entry) {
        return new Lwjgl3Application(entry, getDefaultConfiguration());
    }

    private static Lwjgl3ApplicationConfiguration getDefaultConfiguration() {
        Lwjgl3ApplicationConfiguration configuration = new Lwjgl3ApplicationConfiguration();
        configuration.setTitle("Sokoban");
        configuration.useVsync(true);
        configuration.setForegroundFPS(Lwjgl3ApplicationConfiguration.getDisplayMode().refreshRate + 1);
        configuration.setWindowedMode(1920, 1080);
        configuration.setResizable(false);
        configuration.setWindowIcon("sokoban_icon64.png");
        configuration.setBackBufferConfig(8, 8, 8, 8, 16, 0, 16);
        return configuration;
    }
}