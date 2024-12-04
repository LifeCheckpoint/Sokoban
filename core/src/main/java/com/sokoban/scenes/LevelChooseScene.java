package com.sokoban.scenes;

import java.util.Arrays;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.sokoban.Main;
import com.sokoban.assets.ImageAssets;
import com.sokoban.core.game.Logger;
import com.sokoban.core.map.gamedefault.SokobanLevels;
import com.sokoban.polygon.combine.HintMessageBox;
import com.sokoban.polygon.combine.WindowImageSelector;
import com.sokoban.polygon.container.ImageButtonContainer;
import com.sokoban.polygon.manager.BackgroundGrayParticleManager;
import com.sokoban.polygon.manager.MouseMovingTraceManager;
import com.sokoban.utils.ActionUtils;

public class LevelChooseScene extends SokobanScene {
    private BackgroundGrayParticleManager bgParticle;
    private Image leftSelectButton, rightSelectButton;
    private Image returnButton, startButton;
    private Image editorButton;
    private MouseMovingTraceManager moveTrace;
    private WindowImageSelector levelSelector;

    public LevelChooseScene(Main gameMain) {
        super(gameMain);
    }

    @Override
    public void init() {
        super.init();

        moveTrace = new MouseMovingTraceManager(viewport);
        
        // 选择器
        levelSelector = new WindowImageSelector(gameMain, Arrays.asList(ImageAssets.ShowLevel1, ImageAssets.ShowLevel2, ImageAssets.ShowLevel3));

        // 返回 进入按钮
        ImageButtonContainer controlButtonContainer = new ImageButtonContainer(gameMain);
        returnButton = controlButtonContainer.create(ImageAssets.LeftArrowButton);
        returnButton.setPosition(0.5f, 8f);

        startButton = controlButtonContainer.create(ImageAssets.RightArrowButton);
        startButton.setPosition(13.6f, 0.6f);

        // 编辑器按钮
        controlButtonContainer.setScaling(0.005f);
        editorButton = controlButtonContainer.create(ImageAssets.EditorButton);
        editorButton.setPosition(13.6f, 8f);

        // 左右选择按钮
        ImageButtonContainer selectorButtonContainer = new ImageButtonContainer(gameMain);
        leftSelectButton = selectorButtonContainer.create(ImageAssets.LeftSquareArrow);
        leftSelectButton.setSize(0.8f, 0.8f);
        leftSelectButton.setPosition(1f, 0.5f);
        rightSelectButton = selectorButtonContainer.create(ImageAssets.RightSquareArrow);
        rightSelectButton.setSize(0.8f, 0.8f);
        rightSelectButton.setPosition(2f, 0.5f);

        returnButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                gameMain.getScreenManager().returnPreviousScreen();
            }
        });

        startButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                SokobanLevels currentSelectedLevel = getLevelEnum(levelSelector.getCurrentWindowIndex());
                gameMain.getScreenManager().setScreen(new MapChooseScene(gameMain, currentSelectedLevel));
            }
        });

        leftSelectButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                gotoLeftWindow();
            }
        });

        rightSelectButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                gotoRightWindow();
            }
        });

        editorButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                gameMain.getScreenManager().setScreen(new MapEditScene(gameMain));
            }
        });

        addCombinedObjectToStage(levelSelector);
        addActorsToStage(returnButton, startButton, leftSelectButton, rightSelectButton, editorButton);

        // 淡入效果
        levelSelector.getAllActors().forEach(ActionUtils::FadeInEffect);
        ActionUtils.FadeInEffect(returnButton);
        ActionUtils.FadeInEffect(startButton);
        ActionUtils.FadeInEffect(leftSelectButton);
        ActionUtils.FadeInEffect(rightSelectButton);
        ActionUtils.FadeInEffect(editorButton);

        bgParticle = new BackgroundGrayParticleManager(gameMain);
        bgParticle.startCreateParticles();
    }

    /**
     * 到前一个窗口
     */
    private void gotoLeftWindow() {
        if (levelSelector.setCurrentWindowToPre()) {
            HintMessageBox msgBox = new HintMessageBox(gameMain, getLevelEnum(levelSelector.getCurrentWindowIndex()).toString());
            msgBox.setPosition(8f, 0.5f);
            msgBox.addActorsToStage(stage);
        }
    }

    /**
     * 到后一个窗口
     */
    private void gotoRightWindow() {
        if (levelSelector.setCurrentWindowToNext()) {
            HintMessageBox msgBox = new HintMessageBox(gameMain, getLevelEnum(levelSelector.getCurrentWindowIndex()).toString());
            msgBox.setPosition(8f, 0.5f);
            msgBox.addActorsToStage(stage);
        }
    }


    /**
     * 将索引转换为关卡常量枚举
     * @param levelIndex 关卡索引
     * @return 关卡枚举
     */
    private SokobanLevels getLevelEnum(int levelIndex) {
        switch (levelIndex) {
            case 0:
                return SokobanLevels.Origin;
            case 1:
                return SokobanLevels.Moving;
            case 2:
                return SokobanLevels.Random;
            default:
                Logger.error("LevelChooseScene", String.format("%d is not a valid level index, return origin.", levelIndex));
                return SokobanLevels.Origin;
        }
    }

    // 输入事件处理
    @Override
    public void input() {
        // 选择
        if (Gdx.input.isKeyJustPressed(Keys.A) || Gdx.input.isKeyJustPressed(Keys.LEFT)) {
            gotoLeftWindow();
        }

        if (Gdx.input.isKeyJustPressed(Keys.D) || Gdx.input.isKeyJustPressed(Keys.RIGHT)) {
            gotoRightWindow();
        }

        // 退出
        if (Gdx.input.isKeyJustPressed(Keys.ESCAPE)) {
            gameMain.getScreenManager().returnPreviousScreen();
        }

        // 进入
        if (Gdx.input.isKeyJustPressed(Keys.SPACE)) {
            SokobanLevels currentSelectedLevel = getLevelEnum(levelSelector.getCurrentWindowIndex());
            gameMain.getScreenManager().setScreen(new MapChooseScene(gameMain, currentSelectedLevel));
        }
    }

    // 重绘逻辑
    @Override
    public void draw(float delta) {
        moveTrace.setPositionWithUpdate();
        stage.draw();
    }

    @Override
    public void logic(float delta) {}

    // 资源释放
    @Override
    public void dispose() {
        super.dispose();
    }
}
