package com.sokoban.scenes;

import java.util.Arrays;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.sokoban.Main;
import com.sokoban.manager.APManager;
import com.sokoban.manager.BackgroundGrayParticleManager;
import com.sokoban.manager.MouseMovingTraceManager;
import com.sokoban.polygon.combine.HintMessageBox;
import com.sokoban.polygon.combine.WindowImageSelector;
import com.sokoban.polygon.container.ImageButtonContainer;
import com.sokoban.scenes.LevelIntroScene.Levels;
import com.sokoban.utils.ActionUtils;

public class LevelChooseScene extends SokobanScene {
    private BackgroundGrayParticleManager bgParticle;
    private Image leftSelectButton, rightSelectButton;
    private Image returnButton, startButton;
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
        levelSelector = new WindowImageSelector(gameMain, Arrays.asList(APManager.ImageAssets.ShowLevel1, APManager.ImageAssets.ShowLevel2, APManager.ImageAssets.ShowLevel3));

        // 返回 进入按钮
        ImageButtonContainer controlButtonContainer = new ImageButtonContainer(gameMain);
        returnButton = controlButtonContainer.create(APManager.ImageAssets.LeftArrowButton);
        returnButton.setPosition(0.5f, 8f);

        startButton = controlButtonContainer.create(APManager.ImageAssets.RightArrowButton);
        startButton.setPosition(13.6f, 0.6f);

        // 左右选择按钮
        ImageButtonContainer selectorButtonContainer = new ImageButtonContainer(gameMain);
        leftSelectButton = selectorButtonContainer.create(APManager.ImageAssets.LeftSquareArrow);
        leftSelectButton.setSize(0.8f, 0.8f);
        leftSelectButton.setPosition(1f, 0.5f);
        rightSelectButton = selectorButtonContainer.create(APManager.ImageAssets.RightSquareArrow);
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
                Levels currentSelectedLevel = getLevelEnum(levelSelector.getCurrentWindowIndex());
                gameMain.getScreenManager().setScreen(new LevelIntroScene(gameMain, currentSelectedLevel));
            }
        });

        leftSelectButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (levelSelector.setCurrentWindowToPre()) {
                    HintMessageBox msgBox = new HintMessageBox(gameMain, getLevelEnum(levelSelector.getCurrentWindowIndex()).getLevelName());
                    msgBox.setPosition(8f, 0.5f);
                    msgBox.addActorsToStage(stage);
                }
            }
        });

        rightSelectButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (levelSelector.setCurrentWindowToNext()) {
                    HintMessageBox msgBox = new HintMessageBox(gameMain, getLevelEnum(levelSelector.getCurrentWindowIndex()).getLevelName());
                    msgBox.setPosition(8f, 0.5f);
                    msgBox.addActorsToStage(stage);
                }
            }
        });

        addCombinedObjectToStage(levelSelector);
        addActorsToStage(returnButton, startButton, leftSelectButton, rightSelectButton);

        // 淡入效果
        levelSelector.getAllActors().forEach(ActionUtils::FadeInEffect);
        ActionUtils.FadeInEffect(returnButton);
        ActionUtils.FadeInEffect(startButton);
        ActionUtils.FadeInEffect(leftSelectButton);
        ActionUtils.FadeInEffect(rightSelectButton);

        bgParticle = new BackgroundGrayParticleManager(gameMain);
        bgParticle.startCreateParticles();
    }

    /**
     * 将索引转换为关卡常量枚举
     * @param levelIndex 关卡索引
     * @return 关卡枚举
     */
    private Levels getLevelEnum(int levelIndex) {
        switch (levelIndex) {
            case 0:
                return Levels.Origin;
            case 1:
                return Levels.Moving;
            case 2:
                return Levels.Random;
            default:
                Gdx.app.error("LevelChooseScene", String.format("%d is not a valid level index, return origin.", levelIndex));
                return Levels.Origin;
        }
    }

    // 重绘逻辑
    private void draw() {
        moveTrace.setPositionWithUpdate();
        // stage 更新
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    // 主渲染帧
    @Override
    public void render(float delta) {
        draw();
    }

    @Override
    public void hide() {}

    // 资源释放
    @Override
    public void dispose() {
        super.dispose();
    }
}
