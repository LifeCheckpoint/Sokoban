package com.sokoban.statics;

import com.sokoban.core.settings.SettingManager;

/**
 * 提供 Main 主类的配置管理
 * @author Life_Checkpoint
 */
public class MainConfig {

    public enum RunModes {Normal, CoreTest, GuiTest}

    /**
     * 程序启动方式
     * <br><br>
     * 0 = 正常 GUI 启动
     * <br><br>
     * 1 = 核心测试 CoreTest
     * <br><br>
     * 2 = 场景测试 GuiTest
     */
    public RunModes runMode;
    
    /** 是否启用 Profiler */
    public boolean enableGLProfiler;

    /** 游戏设置管理 */
    public SettingManager settingManager;
}
