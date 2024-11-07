package com.sokoban;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.ScreenUtils;

import com.sokoban.scenes.GameWelcomeScene;
import com.sokoban.enums.AudioEnums;
import com.sokoban.manager.MusicManager;
import com.sokoban.manager.ScreenManager;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends ApplicationAdapter {
    private ScreenManager screenManager;
    private int backGroundColorRGBA = 0x101010ff;
    private MusicManager musicManager;

    // 获得当前场景
    public ScreenManager getScreenManager() {
        return screenManager;
    }

    // 主游戏创建
    @Override
    public void create() {
        screenManager = new ScreenManager();
        screenManager.setScreen(new GameWelcomeScene(this));

        musicManager = new MusicManager();
        musicManager.loadMusic(AudioEnums.Background1, "audio/Light.mp3");
        musicManager.setVolume(0.2f);
        musicManager.play(AudioEnums.Background1, true);
    }

    // 重绘逻辑
    private void draw() {
        ScreenUtils.clear(new Color(backGroundColorRGBA));

        // 渲染当前屏幕
        screenManager.render(Gdx.graphics.getDeltaTime());
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
        if (musicManager != null) musicManager.dispose(); // 释放音乐资源
    }

    public void exit() {
        // Do some Data check...
        dispose();
        System.exit(0);
    }
}
