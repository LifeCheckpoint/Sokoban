package com.sokoban.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.sokoban.Main;
import com.sokoban.manager.BackgroundGrayParticleManager;
import com.sokoban.polygon.ImageButtonContainer;

public class SettingScene extends SokoyoScene {

    // Background 粒子
    private BackgroundGrayParticleManager bgParticle;

    // UI
    private ImageButtonContainer buttonContainer;
    private Image returnButton;

    public SettingScene(Main gameMain) {
        super(gameMain);
    }

    public void init() {
        super.init();

        buttonContainer = new ImageButtonContainer(gameMain.getAssetsPathManager());
        // labelContainer = new ImageLabelContainer(0.3f);

        // 初始化按钮
        returnButton = buttonContainer.create("left_arrow.png");
        returnButton.setPosition(0.5f, 8f);

        // 返回按钮监听
        returnButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("Return!");
                gameMain.getScreenManager().returnPreviousScreen();
            }
        });

        bgParticle = new BackgroundGrayParticleManager(gameMain);
        bgParticle.startCreateParticles();

        // 添加 UI
        stage.addActor(returnButton);
    }

    // 输入事件处理
    private void input() {
        if (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT) && Gdx.input.isKeyPressed(Input.Keys.S)) {
            System.out.println("Save");
        }
    }

    // 重绘逻辑
    private void draw() {
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

    @Override
    public void dispose() {
        super.dispose();
    }
}