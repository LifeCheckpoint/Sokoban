package com.sokoban.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.sokoban.Main;
import com.sokoban.manager.BackgroundGrayParticleManager;
import com.sokoban.polygon.CheckboxObject;
import com.sokoban.polygon.container.ImageButtonContainer;

public class SettingScene extends SokoyoScene {

    // Background 粒子
    private BackgroundGrayParticleManager bgParticle;

    // UI
    private ImageButtonContainer buttonContainer;
    private Image returnButton;

    private CheckboxObject mipmapCheckbox;

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

        // Mipmap 设置
        mipmapCheckbox = new CheckboxObject(gameMain, "mipmap.png", true, true, 0.16f);
        mipmapCheckbox.setPosition(1f, 7f);
        mipmapCheckbox.setCheckboxType(true);

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
        stage.addActor(mipmapCheckbox.getCheckbox());
        stage.addActor((mipmapCheckbox.getCheckboxText()));
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