package com.sokoban.polygon.combine;

import java.util.Arrays;
import java.util.List;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.sokoban.Main;
import com.sokoban.assets.ImageAssets;

/**
 * 游戏主界面 Frame 黑框
 */
public class GameEscapeFrame extends SokobanCombineObject {
    private float toPaddingX, toPaddingY;
    private float centerX, centerY;
    private Image upFrame, downFrame, leftFrame, rightFrame;
    
    private final float DEFAULT_TO_PADDING_X = 3f, DEFAULT_TO_PADDING_Y = 3f;
    private final float DEFAULT_CENTER_X = 8f, DEFAULT_CENTER_Y = 4.5f;
    private final float MAX_OUT_DISTANCE = 16f;

    public GameEscapeFrame(Main gameMain) {
        super(gameMain);
        init(DEFAULT_TO_PADDING_X, DEFAULT_TO_PADDING_Y, DEFAULT_CENTER_X, DEFAULT_CENTER_Y);
    }

    public GameEscapeFrame(Main gameMain, float toPaddingX, float toPaddingY) {
        super(gameMain);
        init(toPaddingX, toPaddingY, DEFAULT_CENTER_X, DEFAULT_CENTER_Y);
    }

    public GameEscapeFrame(Main gameMain, float toPaddingX, float toPaddingY, float centerX, float centerY) {
        super(gameMain);
        init(toPaddingX, toPaddingY, centerX, centerY);
    }

    private void init(float toPaddingX, float toPaddingY, float centerX, float centerY) {
        this.toPaddingX = toPaddingX;
        this.toPaddingY = toPaddingY;
        this.centerX = centerX;
        this.centerY = centerY;

        this.upFrame = new Image(gameMain.getAssetsPathManager().get(ImageAssets.BlackPixel));
        this.downFrame = new Image(gameMain.getAssetsPathManager().get(ImageAssets.BlackPixel));
        this.leftFrame = new Image(gameMain.getAssetsPathManager().get(ImageAssets.BlackPixel));
        this.rightFrame = new Image(gameMain.getAssetsPathManager().get(ImageAssets.BlackPixel));

        setPosition(centerX, centerY);
    }

    /**
     * 设置 Frame 位置
     * <br><br>
     * 传入<b>中心</b>坐标
     * <br><br>
     * {@inheritDoc}
     */
    @Override
    public void setPosition(float x, float y) {
        // 世界坐标
        float partWorldX, partWorldY, partWidth, partHeight;

        // upFrame
        partWidth = 2 * MAX_OUT_DISTANCE;
        partHeight = MAX_OUT_DISTANCE;
        partWorldX = centerX - MAX_OUT_DISTANCE;
        partWorldY = centerY + toPaddingY;
        upFrame.setSize(partWidth, partHeight);
        upFrame.setPosition(partWorldX, partWorldY);

        // downFrame
        partWidth = 2 * MAX_OUT_DISTANCE;
        partHeight = MAX_OUT_DISTANCE;
        partWorldX = centerX - MAX_OUT_DISTANCE;
        partWorldY = centerY - toPaddingY - partHeight;
        downFrame.setSize(partWidth, partHeight);
        downFrame.setPosition(partWorldX, partWorldY);

        // leftFrame
        partWidth = MAX_OUT_DISTANCE;
        partHeight = 2 * MAX_OUT_DISTANCE;
        partWorldX = centerX - toPaddingX - partWidth;
        partWorldY = centerY - MAX_OUT_DISTANCE;
        leftFrame.setSize(partWidth, partHeight);
        leftFrame.setPosition(partWorldX, partWorldY);

        // rightFrame
        partWidth = MAX_OUT_DISTANCE;
        partHeight = 2 * MAX_OUT_DISTANCE;
        partWorldX = centerX + toPaddingX;
        partWorldY = centerY - MAX_OUT_DISTANCE;
        rightFrame.setSize(partWidth, partHeight);
        rightFrame.setPosition(partWorldX, partWorldY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Actor> getAllActors() {
        List<Actor> actors = Arrays.asList(upFrame, downFrame, leftFrame, rightFrame);
        return actors;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addActorsToStage(Stage stage) {
        getAllActors().forEach(actor -> stage.addActor(actor));
    }

}
