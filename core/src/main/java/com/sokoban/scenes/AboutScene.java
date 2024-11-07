package com.sokoban.scenes;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.sokoban.Main;
import com.sokoban.polygon.BackgroundParticle;
import com.sokoban.polygon.ImageButtonContainer;

public class AboutScene extends ApplicationAdapter implements Screen {
    private Main gameMain;
    private FitViewport viewport;
    private Stage stage;
    private final int backGroundColorRGBA = 0x101010ff;

    // 鼠标位移相关 Vector
    private Vector2 mousePos;
    private Vector2 screenCenter;
    private Vector2 mouse2CenterOffsetScaled;
    private final float maxScreenOffset = 1f;
    private final float screenMoveScaling = 0.03f;

    // Background
    private List<BackgroundParticle> backgroundParticle;
    private final float particleCreateInverval = 1f;

    // Texture UI
    ImageButtonContainer buttonContainer;
    private Texture particleTexture;

    private ImageButton returnButton;

    public AboutScene(Main gameMain) {
        this.gameMain = gameMain;
    }

    public Main getGameMain() {
        return gameMain;
    }

    @Override
    public void show() {
        viewport = new FitViewport(16, 9);
        mousePos = new Vector2();
        mouse2CenterOffsetScaled = new Vector2();
        screenCenter = new Vector2(viewport.getWorldWidth() / 2, viewport.getWorldHeight() / 2);

        // UI Stage
        stage = new Stage(viewport);
        Gdx.input.setInputProcessor(stage);
        buttonContainer = new ImageButtonContainer(0.3f);

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

        // 初始化背景粒子
        particleTexture = new Texture("img/particle1.png");
        backgroundParticle = new ArrayList<>();

        // 粒子 Timer 控制粒子创建
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                addNewParticle();
            }
        }, 0, particleCreateInverval);

        // 添加 UI
        stage.addActor(returnButton);
    }

    // 创建新粒子
    private void addNewParticle() {
        final float x = MathUtils.random(0f, 16f), y = MathUtils.random(0f, 16f);
        BackgroundParticle newParticle = new BackgroundParticle(x, y, particleTexture);
        backgroundParticle.add(newParticle);
        stage.addActor(newParticle);
    }


    // 输入事件处理
    private void input() {
        if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            System.out.println("Space");
        }
    }

    // 重绘逻辑
    private void draw() {
        ScreenUtils.clear(new Color(backGroundColorRGBA));

        // 计算鼠标位置世界坐标以及偏移矢量
        mousePos.set(Gdx.input.getX(), Gdx.input.getY());
        viewport.unproject(mousePos);
        mouse2CenterOffsetScaled = mousePos.cpy().sub(screenCenter).scl(screenMoveScaling);

        // 防止移出
        if (mouse2CenterOffsetScaled.len() > maxScreenOffset) mouse2CenterOffsetScaled.setLength(maxScreenOffset);
        
        // 更新相机位置
        viewport.getCamera().position.set(mouse2CenterOffsetScaled.add(screenCenter), 0);
        viewport.getCamera().update();

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