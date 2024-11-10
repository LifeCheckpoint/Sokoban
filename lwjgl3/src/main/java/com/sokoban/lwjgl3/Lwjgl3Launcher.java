package com.sokoban.lwjgl3;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.sokoban.CoreTest;
import com.sokoban.Main;

/** Launches Sokoban. */
public class Lwjgl3Launcher {
    public static void main(String[] args) {
        // 选择是否进入测试模式
        boolean runTests = false;

        // 检查启动参数
        for (String arg : args) {
            if (arg.equals("--test")) {
                runTests = true;
                break;
            }
        }

        if (!runTests) {
            if (StartupHelper.startNewJvmIfRequired()) return;
            createApplication();
        } else {
            CoreTest.main();
        }
    }

    private static Lwjgl3Application createApplication() {
        return new Lwjgl3Application(new Main(), getDefaultConfiguration());
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