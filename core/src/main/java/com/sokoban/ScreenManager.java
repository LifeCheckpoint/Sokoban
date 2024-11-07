package com.sokoban;

import com.badlogic.gdx.Screen;

public class ScreenManager {
    private Screen currentScreen;

    // 场景切换
    public void setScreen(Screen screen) {
        if (currentScreen != null) {
            currentScreen.hide();
            currentScreen.dispose();
        }
        currentScreen = screen;
        currentScreen.show();
    }

    public void dispose() {
        if (currentScreen != null) {
            currentScreen.dispose(); // 释放当前屏幕的资源
        }
    }

    public void render(float delta) {
        if (currentScreen != null) {
            currentScreen.render(delta);
        }
    }

    public void resize(int width, int height) {} // 一般不更改窗口大小
}
