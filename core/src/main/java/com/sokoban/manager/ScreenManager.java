package com.sokoban.manager;

import java.util.Stack;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.sokoban.scenes.SokoyoScene;

/**
 * 场景栈切换管理器
 * @author Life_Checkpoint
 */
public class ScreenManager {
    private SokoyoScene currentScreen;
    private Stack<SokoyoScene> screenStack;

    public ScreenManager() {
        screenStack = new Stack<>();
    }

    /** 
     * 场景进入并彻底清理后台场景
     * @param screen 进入场景
     */
    public void setScreenWithClear(SokoyoScene screen) {
        if (!screenStack.isEmpty()) clearScreenStack();
        setScreenWithoutSaving(screen);
    }

    /** 
     * 场景进入，保持后台栈不变，不将当前场景压入后台
     * @param screen 进入场景
     */
    public void setScreenWithoutSaving(SokoyoScene screen) {
        if (currentScreen != null) {
            currentScreen.hide();
            currentScreen.dispose();
        }
        currentScreen = screen;
        currentScreen.show();
    }

    /** 
     * 场景进入，将当前场景压入后台
     * @param screen 进入场景
     */
    public void setScreen(SokoyoScene screen) {
        if (currentScreen != null) {
            currentScreen.hide();
            screenStack.add(currentScreen);
        }
        currentScreen = screen;
        currentScreen.show();
    }

    /** 
     * 返回上一个场景，当前场景被彻底销毁
     */
    public void returnPreviousScreen() {
        if (screenStack.isEmpty() || currentScreen == null) {
            Gdx.app.error("ScreenManager", "The Previous / Current Screen is not exists");
            return;
        }
        currentScreen.hide();
        currentScreen.dispose();
        currentScreen = screenStack.pop();
        currentScreen.show();
    }

    // 获取当前屏幕
    public SokoyoScene getCurrentScreen() {
        return currentScreen;
    }
    // 清理所有后台场景
    public void clearScreenStack() {
        for(Screen thisScreen : screenStack) thisScreen.dispose();
        screenStack.clear();
    }
    // 彻底清理所有场景
    public void dispose() {
        clearScreenStack();
        if (currentScreen != null) currentScreen.dispose();
    }
    // 执行场景渲染
    public void render(float delta) {
        if (currentScreen != null) {
            currentScreen.render(delta);
        }
    }
}
