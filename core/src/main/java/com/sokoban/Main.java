package com.sokoban;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.ScreenUtils;

import com.sokoban.scenes.GameWelcomeScene;
import com.sokoban.scenes.LoadingScene;
import com.sokoban.scenes.TestScene;
import com.sokoban.core.Logger;
import com.sokoban.core.settings.SettingManager;
import com.sokoban.manager.APManager;
import com.sokoban.manager.MusicManager;
import com.sokoban.manager.ScreenManager;

/** 
 * {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms.
 * <br><br>
 * <b>游戏中心类，负责全局句柄分发与初始资源加载</b>
 */
public class Main extends ApplicationAdapter {
    private int runMode;
    private APManager apManager;
    private SettingManager setManager;
    private ScreenManager screenManager;
    private MusicManager musicManager;
    
    private Color backGroundColorRGBA = new Color(0x101010ff);

    public Main(int runMode, SettingManager settingManagerCore) {
        this.runMode = runMode;
        this.setManager = settingManagerCore;
    }

    /**
     * 获得资源管理句柄
     * @return 资源管理句柄
     */
    public APManager getAssetsPathManager() {
        return apManager;
    }

    /**
     * 获得设置管理句柄
     * @return 设置管理句柄
     */
    public SettingManager getSettingManager() {
        return setManager;
    }

    /**
     * 获得场景管理句柄
     * @return 场景管理句柄
     */
    public ScreenManager getScreenManager() {
        return screenManager;
    }

    /**
     * 获得音乐管理句柄
     * @return 场景管理句柄
     */
    public MusicManager getMusicManager() {
        return musicManager;
    }

    // 主游戏创建
    @Override
    public void create() {
        Logger.info("Main Thread", "Game start");

        apManager = new APManager(this);
        apManager.preloadAllAssets();

        musicManager = new MusicManager(this);
        screenManager = new ScreenManager();

        if (runMode == 0) screenManager.setScreen(new LoadingScene(this, new GameWelcomeScene(this), apManager));
        if (runMode == 2) screenManager.setScreen(new LoadingScene(this, new TestScene(this), apManager));
    }

    // 重绘逻辑
    private void draw() {
        ScreenUtils.clear(backGroundColorRGBA);

        // 渲染当前屏幕
        screenManager.render();
    }

    // 输入事件处理
    private void input() {}

    // 主渲染帧
    @Override
    public void render() {
        input();
        // logic();
        draw();
    }

    // 资源释放
    @Override
    public void dispose() {
        if (screenManager != null) screenManager.dispose(); // 释放屏幕资源
    }

    public void exit() {
        // Do some Data check...
        apManager.dispose();
        dispose();
        System.exit(0);
    }
}
