package com.sokoban.scenes;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
// import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.sokoban.Main;
import com.sokoban.manager.BackgroundGrayParticleManager;
import com.sokoban.polygon.ImageButtonContainer;
// import com.sokoban.polygon.ImageLabelContainer;

public class SettingScene extends ApplicationAdapter implements Screen {
    private Main gameMain;
    private FitViewport viewport;
    private Stage stage;

    // Background 粒子
    private BackgroundGrayParticleManager bgParticle;

    // UI
    private ImageButtonContainer buttonContainer;
    // private ImageLabelContainer labelContainer;
    private ImageButton returnButton;

    public SettingScene(Main gameMain) {
        this.gameMain = gameMain;
    }

    public Main getGameMain() {
        return gameMain;
    }

    @Override
    public void show() {
        viewport = new FitViewport(16, 9);

        // UI Stage
        stage = new Stage(viewport);
        Gdx.input.setInputProcessor(stage);
        buttonContainer = new ImageButtonContainer(0.3f);
        // labelContainer = new ImageLabelContainer(0.3f);

        // 初始化按钮
        returnButton = buttonContainer.createButton("img/left_arrow.png");
        returnButton.setPosition(0.5f, 8f);

        // 返回按钮监听
        returnButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("Return!");
                gameMain.getScreenManager().setScreen(new GameWelcomeScene(gameMain));
            }
        });

        bgParticle = new BackgroundGrayParticleManager(stage);
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

    // 资源释放
    @Override
    public void dispose() {
        // 释放 stage
        if (stage != null) stage.dispose();
    }
}