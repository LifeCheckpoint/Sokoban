package com.sokoban.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.sokoban.Main;
import com.sokoban.core.Logger;
import com.sokoban.manager.APManager;
import com.sokoban.manager.BackgroundGrayParticleManager;
import com.sokoban.manager.MouseMovingTraceManager;
import com.sokoban.polygon.combine.HintMessageBox;
import com.sokoban.polygon.container.ImageButtonContainer;
import com.sokoban.polygon.container.ImageLabelContainer;
import com.sokoban.utils.ActionUtils;

/**
 * 关于界面
 * @author Life_Checkpoint
 */
public class AboutScene extends SokobanScene {

    // 画面相机跟踪
    private MouseMovingTraceManager moveTrace;

    // Background 粒子
    private BackgroundGrayParticleManager bgParticle;

    // UI
    private ImageButtonContainer buttonContainer;
    private ImageLabelContainer labelContainer;
    private Image returnButton;
    private Image infoLabel;
    private Image infoLabelEgg;

    private int clickLabelCount = 0;

    public AboutScene(Main gameMain) {
        super(gameMain);
    }

    public void init() {
        super.init();

        moveTrace = new MouseMovingTraceManager(viewport);

        buttonContainer = new ImageButtonContainer(gameMain);
        labelContainer = new ImageLabelContainer(gameMain, 0.008f);

        returnButton = buttonContainer.create(APManager.ImageAssets.LeftArrowButton);
        returnButton.setPosition(0.5f, 8f);

        // 信息 label
        infoLabel = labelContainer.create(APManager.ImageAssets.AboutInfo);
        infoLabelEgg = labelContainer.create(APManager.ImageAssets.AboutInfoEGG);
        infoLabel.setPosition(6f, 4.5f - infoLabel.getHeight() / 2);
        infoLabelEgg.setPosition(6f, 4.5f - infoLabel.getHeight() / 2);

        // 返回按钮监听
        returnButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                gameMain.getScreenManager().returnPreviousScreen();
            }
        });

        // label 点击彩蛋，触发后监听失效
        infoLabel.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                clickLabelCount += 1;
                if (clickLabelCount >= 10) {
                    Logger.info("AboutScene", "A Colorful eggs is trigged");
                    
                    HintMessageBox colorfulEggTriggedHintBox = new HintMessageBox(gameMain, "Something is trigged...");
                    colorfulEggTriggedHintBox.setPosition(8f, 0.5f);
                    colorfulEggTriggedHintBox.addActorsToStage(stage);
 
                    stage.addActor(infoLabelEgg);
                    infoLabel.remove();
                }
            }
        });

        bgParticle = new BackgroundGrayParticleManager(gameMain);
        bgParticle.startCreateParticles();

        ActionUtils.FadeInEffect(returnButton);
        ActionUtils.FadeInEffect(infoLabel);

        // 添加 UI
        addActorsToStage(returnButton, infoLabel);
    }

    @Override
    public void input() {
        // 退出
        if (Gdx.input.isKeyJustPressed(Keys.ESCAPE)) {
            gameMain.getScreenManager().returnPreviousScreen();
        }
    }

    @Override
    public void draw(float delta) {
        // 相机跟踪
        moveTrace.setPositionWithUpdate();
        
        stage.draw();
    }

    @Override
    public void logic(float delta) {}

    @Override
    public void hide() {}

    // 资源释放
    @Override
    public void dispose() {
        super.dispose();
    }
}