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
import com.sokoban.polygon.combine.WindowImageSelector;
import com.sokoban.polygon.container.ImageButtonContainer;

public class LevelChooseScene extends SokoyoScene {
    private Image returnButton;
    private MouseMovingTraceManager moveTrace;
    private WindowImageSelector levelSelector;
    private BackgroundGrayParticleManager bgParticle;

    public LevelChooseScene(Main gameMain) {
        super(gameMain);
    }

    @Override
    public void init() {
        super.init();

        moveTrace = new MouseMovingTraceManager(viewport);
        
        ImageButtonContainer returnButtonContainer = new ImageButtonContainer(gameMain);
        returnButton = returnButtonContainer.create(APManager.ImageAssets.LeftArrowButton);
        returnButton.setPosition(0.5f, 8f);

        returnButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                gameMain.getScreenManager().returnPreviousScreen();
            }
        });

        // 选择器
        levelSelector = new WindowImageSelector(gameMain, Arrays.asList(APManager.ImageAssets.ShowLevel1, APManager.ImageAssets.ShowLevel2, APManager.ImageAssets.ShowLevel3));

        levelSelector.addActorsToStage(stage);
        stage.addActor(returnButton);

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
