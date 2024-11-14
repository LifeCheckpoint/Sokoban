package com.sokoban.polygon.combine;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.sokoban.Main;
import com.sokoban.manager.APManager;
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

    public WindowImageSelector(Main gameMain, List<APManager.ImageAssets> WindowImageEnums) {
        super(gameMain);
        init(gameMain, WindowImageEnums, DEFAULT_CENTRAL_WIDTH, DEFAULT_CENTRAL_HEIGHT, DEFAULT_SIDE_WIDTH, DEFAULT_SIDE_HEIGHT, DEFAULT_BUFF);
    }

    public WindowImageSelector(Main gameMain, List<APManager.ImageAssets> WindowImageEnums, 
                                float centralWidth, float centralHeight, float sideWidth, float sideHeight) {
        super(gameMain);
        init(gameMain, WindowImageEnums, centralWidth, centralHeight, sideWidth, sideHeight, DEFAULT_BUFF);
    }

    public WindowImageSelector(Main gameMain, List<APManager.ImageAssets> WindowImageEnums, 
                                float centralWidth, float centralHeight, float sideWidth, float sideHeight, float buff) {
        super(gameMain);
        init(gameMain, WindowImageEnums, centralWidth, centralHeight, sideWidth, sideHeight, buff);
    }

    public void init(Main gameMain, List<APManager.ImageAssets> WindowImageEnums, 
                        float centralWidth, float centralHeight, float sideWidth, float sideHeight, float buff) {
        this.buff = buff;
        this.centralWidth = centralWidth;
        this.centralHeight = centralHeight;
        this.sideWidth = sideWidth;
        this.sideHeight = sideHeight;
        this.windows = new ArrayList<>();

        ImageLabelContainer windowContainer = new ImageLabelContainer(gameMain, 0.0005f);
        for (APManager.ImageAssets windowImageEnum : WindowImageEnums) windows.add(windowContainer.create(windowImageEnum));
        
        // 设置初始位置
        Image currentWindow = windows.get(0);
        currentWindow.setSize(centralWidth, centralHeight);
        float leftEdgeX = DEFAULT_SCREEN_CENTRE_X - centralWidth / 2, downEdgeY = DEFAULT_SCREEN_CENTRE_Y - centralHeight / 2;
        currentWindow.setPosition(leftEdgeX, downEdgeY);
        // 其它窗口位置
        
        for (int i = 1; i < windows.size(); i++) {
            currentWindow = windows.get(i);
            currentWindow.setSize(DEFAULT_SIDE_WIDTH, DEFAULT_SIDE_HEIGHT);

            // 上一个窗口宽度 + buff = 当前窗口左边缘 x
            leftEdgeX += windows.get(i - 1).getWidth() + buff;
            currentWindow.setPosition(leftEdgeX, DEFAULT_SCREEN_CENTRE_Y - sideHeight / 2);
        }

        setCurrentWindowIndex(0);
    }

    /**
     * {@inheritDoc}
     * <br><br>
     * <b>注意，在 WindowImageSelector 中，此为居中坐标</b>
     */
    @Override
    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;


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

    public void setCurrentWindowIndex(int currentWindowIndex) {
        // TODO
        if (isInMoving()) return;
        this.currentWindowIndex = currentWindowIndex;

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
