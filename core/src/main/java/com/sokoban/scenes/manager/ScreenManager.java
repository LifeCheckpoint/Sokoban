package com.sokoban.scenes.manager;

import java.util.Stack;

import com.badlogic.gdx.Screen;
import com.sokoban.core.game.Logger;
import com.sokoban.scenes.SokobanScene;

/**
 * 场景栈切换管理器
 * @author Life_Checkpoint
 */
public class ScreenManager {
    private SokobanScene currentScreen;
    private Stack<SokobanScene> screenStack;

    public ScreenManager() {
        screenStack = new Stack<>();
    }

    /** 
     * 场景进入并彻底清理后台场景
     * @param screen 进入场景
     */
    public void setScreenWithClear(SokobanScene screen) {
        if (!screenStack.isEmpty()) clearScreenStack();
        setScreenWithoutSaving(screen);
    }

    /** 
     * 场景进入，保持后台栈不变，不将当前场景压入后台
     * @param screen 进入场景
     */
    public void setScreenWithoutSaving(SokobanScene screen) {
        if (currentScreen != null) {
            currentScreen.hide();
            currentScreen.dispose();
        }
        currentScreen = screen;
        currentScreen.show();
        Logger.debug("ScreenManager", "Set screen -> " + currentScreen);
    }

    /** 
     * 场景进入，将当前场景压入后台
     * @param screen 进入场景
     */
    public void setScreen(SokobanScene screen) {
        if (currentScreen != null) {
            currentScreen.hide();
            screenStack.add(currentScreen);
        }
        currentScreen = screen;
        currentScreen.show();
        Logger.debug("ScreenManager", "Set screen -> " + currentScreen);
    }

    /** 
     * 返回上一个场景，当前场景被彻底销毁
     */
    public void returnPreviousScreen() {
        if (screenStack.isEmpty() || currentScreen == null) {
            Logger.error("ScreenManager", "The Previous / Current Screen is not exists");
            return;
        }
        currentScreen.hide();
        currentScreen.dispose();
        currentScreen = screenStack.pop();
        currentScreen.show();
        Logger.debug("ScreenManager", "Set screen -> " + currentScreen);
    }

    public SokobanScene getRootScreen() {
        return screenStack.get(0);
    }

    // 获取当前屏幕
    public SokobanScene getCurrentScreen() {
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
    public void render() {
        if (currentScreen != null) {
            currentScreen.render(SokobanScene.UPDATE_TIME_STEP);
        }
    }

    public Stack<SokobanScene> getScreenStack() {
        return screenStack;
    }
}
