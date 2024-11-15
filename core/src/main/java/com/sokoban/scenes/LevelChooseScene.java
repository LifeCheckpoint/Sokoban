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

public class LevelChooseScene extends SokoyoScene {
    private BackgroundGrayParticleManager bgParticle;
    private Image leftSelectButton, rightSelectButton;
    private Image returnButton;
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

        // 返回按钮
        ImageButtonContainer returnButtonContainer = new ImageButtonContainer(gameMain);
        returnButton = returnButtonContainer.create(APManager.ImageAssets.LeftArrowButton);
        returnButton.setPosition(0.5f, 8f);

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

        leftSelectButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (levelSelector.setCurrentWindowToPre()) {
                    HintMessageBox msgBox = new HintMessageBox(gameMain, "Level-");
                    msgBox.setPosition(8f, 0.1f);
                    msgBox.addActorsToStage(stage);
                }
            }
        });

        rightSelectButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (levelSelector.setCurrentWindowToNext()) {
                    HintMessageBox msgBox = new HintMessageBox(gameMain, "Level+");
                    msgBox.setPosition(8f, 0.1f);
                    msgBox.addActorsToStage(stage);
                }
            }
        });

        levelSelector.addActorsToStage(stage);
        stage.addActor(returnButton);
        stage.addActor(leftSelectButton);
        stage.addActor(rightSelectButton);

        bgParticle = new BackgroundGrayParticleManager(gameMain);
        bgParticle.startCreateParticles();
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
