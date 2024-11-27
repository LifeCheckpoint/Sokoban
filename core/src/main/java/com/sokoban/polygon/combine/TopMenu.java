package com.sokoban.polygon.combine;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.sokoban.Main;
import com.sokoban.assets.ImageAssets;
import com.sokoban.polygon.container.ImageButtonContainer;

/**
 * 用于编辑器的顶层菜单
 */
public class TopMenu extends SokobanCombineObject {
    private Image newButton;
    private Image openButton;
    private Image saveButton;
    private Image saveAsButton;
    private Image exitButton;
    private Image pullButton;

    private float buff;
    private float backWidth;

    private final float DEFAULT_BUFF = 0.1f;
    private final float DEFAULT_BACK_WIDTH = 3f;
    private final float DEFAULT_SCALING = 0.0035f;

    public TopMenu(Main gameMain) {
        super(gameMain);
        init(DEFAULT_BUFF, DEFAULT_BACK_WIDTH, DEFAULT_SCALING);
    }

    public TopMenu(Main gameMain, float buff) {
        super(gameMain);
        init(buff, DEFAULT_BACK_WIDTH, DEFAULT_SCALING);
    }

    public TopMenu(Main gameMain, float buff, float backWidth, float scaling) {
        super(gameMain);
        init(buff, backWidth, scaling);
    }

    private void init(float buff, float backWidth, float scaling) {
        ImageButtonContainer menuButtonContainer = new ImageButtonContainer(gameMain, scaling);

        newButton = menuButtonContainer.create(ImageAssets.NewButton);
        openButton = menuButtonContainer.create(ImageAssets.OpenButton);
        saveButton = menuButtonContainer.create(ImageAssets.SaveButton);
        saveAsButton = menuButtonContainer.create(ImageAssets.SaveAsButton);
        exitButton = menuButtonContainer.create(ImageAssets.ExitButton);

        menuButtonContainer.setScaling(scaling * 1.5f);
        pullButton = menuButtonContainer.create(ImageAssets.DownArrowButton);

        this.buff = buff;
        this.backWidth = backWidth;

        setPosition(8f, 2f);
    }

    /**
     * 设置菜单位置
     * <br><br>
     * {@inheritDoc}
     * @param x 居中坐标
     * @param y 底部坐标
     */
    @Override
    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;

        // 从 pull 按钮开始设置
        float currentY = y;
        pullButton.setPosition(x - pullButton.getWidth() / 2, currentY);
        currentY += pullButton.getHeight() + buff;
        exitButton.setPosition(x - exitButton.getWidth() / 2, currentY);
        currentY += exitButton.getHeight() + buff;
        saveAsButton.setPosition(x - saveAsButton.getWidth() / 2, currentY);
        currentY += saveAsButton.getHeight() + buff;
        saveButton.setPosition(x - saveButton.getWidth() / 2, currentY);
        currentY += saveButton.getHeight() + buff;
        openButton.setPosition(x - openButton.getWidth() / 2, currentY);
        currentY += openButton.getHeight() + buff;
        newButton.setPosition(x - newButton.getWidth() / 2, currentY);

        this.width = backWidth;
        this.height = currentY + buff - y;

    }

    @Override
    public List<Actor> getAllActors() {
        List<Actor> actors = new ArrayList<>();
        actors.add(newButton);
        actors.add(openButton);
        actors.add(saveButton);
        actors.add(saveAsButton);
        actors.add(exitButton);
        actors.add(pullButton);
        return actors;
    }

    @Override
    public void addActorsToStage(Stage stage) {
        getAllActors().forEach(actor -> stage.addActor(actor));
    }

    public Image getNewButton() {
        return newButton;
    }

    public Image getOpenButton() {
        return openButton;
    }

    public Image getSaveButton() {
        return saveButton;
    }

    public Image getSaveAsButton() {
        return saveAsButton;
    }

    public Image getExitButton() {
        return exitButton;
    }

    public Image getPullButton() {
        return pullButton;
    }

}
