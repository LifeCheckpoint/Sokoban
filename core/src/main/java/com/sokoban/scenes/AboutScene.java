package com.sokoban.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.sokoban.Main;
import com.sokoban.manager.BackgroundGrayParticleManager;
import com.sokoban.manager.MouseMovingTraceManager;
import com.sokoban.polygon.ImageButtonContainer;
import com.sokoban.polygon.ImageLabelContainer;

public class AboutScene extends SokoyoScene {

    // 画面相机跟踪
    private MouseMovingTraceManager moveTrace;

    // Background 粒子
    private BackgroundGrayParticleManager bgParticle;

    // UI
    private ImageButtonContainer buttonContainer;
    private ImageLabelContainer labelContainer;
    private ImageButton returnButton;
    private Image infoLabel;

    private int clickLabelCount = 0;

    public AboutScene(Main gameMain) {
        super(gameMain);
    }

    public void init() {
        super.init();

        moveTrace = new MouseMovingTraceManager(viewport);

        buttonContainer = new ImageButtonContainer(0.3f);
        labelContainer = new ImageLabelContainer(0.3f);

        // 初始化按钮
        returnButton = buttonContainer.createButton("left_arrow.png");
        returnButton.setPosition(0.5f, 8f);

        // 信息 label
        infoLabel = labelContainer.createLabel("about_info.png", 3f);
        infoLabel.setPosition(6f, 4.5f - infoLabel.getHeight() / 2);

        // 返回按钮监听
        returnButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("Return!");
                gameMain.getScreenManager().returnPreviousScreen();
            }
        });

        // label 点击彩蛋
        infoLabel.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                clickLabelCount += 1;
                if (clickLabelCount >= 10) {
                    System.out.println("Colorful eggs");
                    labelContainer.resetLabel(infoLabel, "about_info2.png", 3f);
                }
            }
        });

        bgParticle = new BackgroundGrayParticleManager(stage);
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