package com.sokoban;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.ScreenUtils;

import com.sokoban.scenes.GameWelcomeScene;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends ApplicationAdapter {
    private ScreenManager screenManager;
    private int backGroundColorRGBA = 0x101010ff;

    // 获得当前场景
    public ScreenManager getScreenManager() {
        return screenManager;
    }

    @Override
    public void create() {
        screenManager = new ScreenManager();
        screenManager.setScreen(new GameWelcomeScene(this));
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
        if (screenManager != null) {
            screenManager.dispose(); // 释放当前屏幕的资源
        }
    }

    // 游戏开始
    public void startGame() {
        System.out.println("Game start.");
    }
}
