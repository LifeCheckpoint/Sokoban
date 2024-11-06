package com.sokoban;

import com.sokoban.enums.*;

import com.badlogic.gdx.Screen;

public class ScreenManager {
    private Screen currentScreen;

    // 直接场景切换
    public void setScreen(Screen screen) {
        if (currentScreen != null) {
            currentScreen.hide();
            currentScreen.dispose();
        }
        currentScreen = screen;
        currentScreen.show();
    }

    // 专用场景切换
    public void setScreen(ScreenEnum transForm, ScreenEnum transTo, Screen screen) {
        if (transForm == ScreenEnum.GameWelcomeScene) {
            // sth.
        }
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
