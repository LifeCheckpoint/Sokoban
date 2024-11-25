package com.sokoban.polygon.combine;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.sokoban.Main;
import com.sokoban.assets.ImageAssets;
import com.sokoban.core.Logger;
import com.sokoban.polygon.container.ImageLabelContainer;

public class WindowImageSelector extends SokobanCombineObject {
    private float centralWidth, centralHeight, sideWidth, sideHeight, buff;
    private List<Image> windows;
    private int currentWindowIndex;
    private boolean isInMoving;

    /** 1920 * 1080 */
    private float DEFAULT_CENTRAL_WIDTH = 10f, DEFAULT_CENTRAL_HEIGHT = 5.625f;
    private float DEFAULT_SIDE_WIDTH = 8f, DEFAULT_SIDE_HEIGHT = 4.5f;
    private float DEFAULT_SCREEN_CENTRE_X = 8f, DEFAULT_SCREEN_CENTRE_Y = 4.5f;
    private float DEFAULT_BUFF = 0.5f;

    public WindowImageSelector(Main gameMain, List<ImageAssets> WindowImageEnums) {
        super(gameMain);
        init(gameMain, WindowImageEnums, DEFAULT_CENTRAL_WIDTH, DEFAULT_CENTRAL_HEIGHT, DEFAULT_SIDE_WIDTH, DEFAULT_SIDE_HEIGHT, DEFAULT_BUFF);
    }

    public WindowImageSelector(Main gameMain, List<ImageAssets> WindowImageEnums, 
                                float centralWidth, float centralHeight, float sideWidth, float sideHeight) {
        super(gameMain);
        init(gameMain, WindowImageEnums, centralWidth, centralHeight, sideWidth, sideHeight, DEFAULT_BUFF);
    }

    public WindowImageSelector(Main gameMain, List<ImageAssets> WindowImageEnums, 
                                float centralWidth, float centralHeight, float sideWidth, float sideHeight, float buff) {
        super(gameMain);
        init(gameMain, WindowImageEnums, centralWidth, centralHeight, sideWidth, sideHeight, buff);
    }

    public void init(Main gameMain, List<ImageAssets> WindowImageEnums, 
                        float centralWidth, float centralHeight, float sideWidth, float sideHeight, float buff) {
        this.buff = buff;
        this.centralWidth = centralWidth;
        this.centralHeight = centralHeight;
        this.sideWidth = sideWidth;
        this.sideHeight = sideHeight;
        this.windows = new ArrayList<>();

        ImageLabelContainer windowContainer = new ImageLabelContainer(gameMain, 0.0005f);
        for (ImageAssets windowImageEnum : WindowImageEnums) windows.add(windowContainer.create(windowImageEnum));
        
        currentWindowIndex = 0;
        
        // 窗口大小
        windows.forEach(window -> window.setSize(sideWidth, sideHeight));
        windows.get(currentWindowIndex).setSize(centralWidth, centralHeight);

        setPosition(DEFAULT_SCREEN_CENTRE_X, DEFAULT_SCREEN_CENTRE_Y);
    }

    /**
     * {@inheritDoc}
     * <br><br>
     * 传入参数为<b>居中坐标</b>
     * <br><br>
     * 该方法<b>不会</b>产生动画效果
     */
    @Override
    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;

        // 窗口位置
        // 计算量不大，无需优化该 O(n^2) 分支
        for (int i = 0; i < windows.size(); i++) {
            windows.get(i).setPosition(getWindowTargetX(i), getWindowTargetY(i));
        }

        this.width = (windows.size() - 1) * (sideWidth + buff) + centralWidth;
        this.height = centralHeight;
    }

    /**
     * 更新当前窗口后，计算窗口理论目标 X
     */
    private float getWindowTargetX(int index) {
        if (index < 0 || index >= windows.size()) {
            Logger.error("WindowImageSelector", String.format("Index %d out of range. Expect (0, %d)", index, windows.size()));
            return 0f;
        }

        // 主窗口
        if (index == currentWindowIndex) {
            return x - centralWidth / 2;
        }

        // 主窗口前
        if (index < currentWindowIndex) {
            float beforeWindowX = x - centralWidth / 2;
            
            // 倒序遍历
            for (int i = currentWindowIndex - 1; i >= 0; i--) {
                // 后一个窗口左边缘 x - buff - 当前窗口宽度 = 当前窗口左边缘 x
                beforeWindowX = beforeWindowX - buff - sideWidth;
                if (i == index) return beforeWindowX;
            }
        }

        // 主窗口后
        if (index > currentWindowIndex) {
            float afterWindowX = x - centralWidth / 2;
    
            for (int i = currentWindowIndex + 1; i < windows.size(); i++) {
                // 上一个窗口宽度 + buff = 当前窗口左边缘 x
                // 需判断上一个窗口是否为主窗口
                afterWindowX = afterWindowX + (i - 1 == currentWindowIndex ? centralWidth : sideWidth) + buff;
                if (i == index) return afterWindowX;
            }
        }

        throw new IllegalStateException("Unreachable code");
    }

    /**
     * 更新当前窗口后，计算窗口理论目标 Y
     */
    private float getWindowTargetY(int index) {
        if (index == currentWindowIndex) return y - centralHeight / 2;
        else return y - sideHeight / 2;
    }

    /**
     * {@inheritDoc}
     * @param stage
     */
    @Override
    public void addActorsToStage(Stage stage) {
        windows.forEach(dig -> stage.addActor(dig));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Actor> getAllActors() {
        List<Actor> actors = new ArrayList<>();
        actors.addAll(windows);
        return actors;
    }

    /**
     * 动画更新主窗口到下一窗口
     */
    public boolean setCurrentWindowToNext() {
        return setCurrentWindowIndex(currentWindowIndex + 1);
    }

    /**
     * 动画更新主窗口到下一窗口
     */
    public boolean setCurrentWindowToPre() {
        return setCurrentWindowIndex(currentWindowIndex - 1);
    }

    /**
     * 动画更新主窗口
     * @param index 主窗口索引
     */
    public boolean setCurrentWindowIndex(int index) {
        if (index < 0) {
            return false;
        }
        if (index >= windows.size()) {
            return false;
        }

        // 若正在执行动画，强制复位并执行下一个
        if (isInMoving()) {
            windows.forEach(Actor::clearActions);
            setPosition(x, y);
        }

        lockMoving();

        this.currentWindowIndex = index;

        for (int i = 0; i < windows.size(); i++) {
            // 执行动画，在完成后解锁
            windows.get(i).addAction(Actions.sequence(
                Actions.parallel(
                    Actions.moveTo(getWindowTargetX(i), getWindowTargetY(i), 0.4f, Interpolation.pow3Out),
                    Actions.sizeTo(i == index ? centralWidth : sideWidth, i == index ? centralHeight : sideHeight, 0.5f, Interpolation.pow2Out)
                ),
                Actions.run(() -> unlockMoving())
            ));
        }

        return true;
    }

    public void lockMoving() {
        this.isInMoving = true;
    }

    public void unlockMoving() {
        this.isInMoving = false;
    }

    public boolean isInMoving() {
        return isInMoving;
    }

    public float getCentralWidth() {
        return centralWidth;
    }

    public float getCentralHeight() {
        return centralHeight;
    }

    public float getSideWidth() {
        return sideWidth;
    }

    public float getSideHeight() {
        return sideHeight;
    }

    public float getBuff() {
        return buff;
    }

    public List<Image> getWindows() {
        return windows;
    }

    public float getDEFAULT_CENTRAL_WIDTH() {
        return DEFAULT_CENTRAL_WIDTH;
    }

    public float getDEFAULT_CENTRAL_HEIGHT() {
        return DEFAULT_CENTRAL_HEIGHT;
    }

    public float getDEFAULT_SIDE_WIDTH() {
        return DEFAULT_SIDE_WIDTH;
    }

    public float getDEFAULT_SIDE_HEIGHT() {
        return DEFAULT_SIDE_HEIGHT;
    }

    public float getDEFAULT_BUFF() {
        return DEFAULT_BUFF;
    }

    public int getCurrentWindowIndex() {
        return currentWindowIndex;
    }

}
