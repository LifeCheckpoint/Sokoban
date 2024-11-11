package com.sokoban.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.sokoban.Main;
import com.sokoban.manager.APManager;
import com.sokoban.manager.BackgroundGrayParticleManager;
import com.sokoban.manager.MouseMovingTraceManager;
import com.sokoban.polygon.container.ImageButtonContainer;
import com.sokoban.polygon.container.ImageLabelContainer;

public class AboutScene extends SokoyoScene {

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
        labelContainer = new ImageLabelContainer(0.008f, gameMain);

        // 初始化按钮
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
                System.out.println("Return!");
                gameMain.getScreenManager().returnPreviousScreen();
            }
        });

        // label 点击彩蛋，触发后监听失效
        infoLabel.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                clickLabelCount += 1;
                if (clickLabelCount >= 10) {
                    System.out.println("Colorful eggs");
                    stage.addActor(infoLabelEgg);
                    infoLabel.remove();
                }
            }
        });

        bgParticle = new BackgroundGrayParticleManager(gameMain);
        bgParticle.startCreateParticles();

        // 添加 UI
        stage.addActor(returnButton);
        stage.addActor(infoLabel);
    }

    // 输入事件处理
    private void input() {}

    // 重绘逻辑
    private void draw() {
        // 相机跟踪
        moveTrace.setPositionWithUpdate();

        // stage 更新
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    // 主渲染帧
    @Override
    public void render(float delta) {
        input();
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