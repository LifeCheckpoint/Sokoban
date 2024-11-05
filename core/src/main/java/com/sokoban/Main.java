package com.sokoban;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends ApplicationAdapter {
    private SpriteBatch batch;
    private FitViewport viewport;
    private Stage stage;
    private int backGroundColorRGBA = 0x101010ff;

    // 鼠标位移相关 Vector
    private Vector2 mousePos;
    private Vector2 screenCenter;
    private Vector2 mouse2CenterOffsetScaled;
    private float maxScreenOffset = 1f;
    private float screenMoveScaling = 0.03f;

    // Texture素材
    private Texture startGameButtonTextTexture;

    // Region UI
    private TextureRegion startGameButtonRegion;
    private ImageButton startGameButton;

    @Override
    public void create() {
        batch = new SpriteBatch();
        viewport = new FitViewport(16, 9);
        mousePos = new Vector2();
        mouse2CenterOffsetScaled = new Vector2();
        screenCenter = new Vector2(viewport.getWorldWidth() / 2, viewport.getWorldHeight() / 2);

        // UI Stage
        stage = new Stage(viewport);
        Gdx.input.setInputProcessor(stage);

        // 游戏开始按钮
        startGameButtonTextTexture = new Texture("start_game.png");
        startGameButtonRegion = new TextureRegion(startGameButtonTextTexture);
        startGameButton = new ImageButton(new TextureRegionDrawable(startGameButtonRegion));
        startGameButton.setSize(2f, 3f);
        startGameButton.setPosition(2f, 2f);
        startGameButton.setTransform(true);
        startGameButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("Start Game!");
            }
        });
        startGameButton.addListener(new InputListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                startGameButton.addAction(Actions.scaleTo(1.2f, 1.2f, 0.2f, Interpolation.sine)); // 增大按钮
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                startGameButton.addAction(Actions.scaleTo(1f, 1f, 0.2f, Interpolation.sine)); // 恢复原始大小
            }
        });

        // 添加 UI
        stage.addActor(startGameButton);
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
        
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();

        // Sprite Batch 绘制
        batch.setProjectionMatrix(viewport.getCamera().combined);
        batch.begin();
        // startGameButtonText.draw(batch);
        batch.end();
    }

    // 输入事件处理
    private void input() {
        if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            System.out.println("Space");
        }
    }

    // 主渲染帧
    @Override
    public void render() {
        input();
        // logic();
        draw();
    }

    // 资源释放
    @Override
    public void dispose() {
        batch.dispose();
        stage.dispose();
        startGameButtonTextTexture.dispose();
    }

    // 游戏开始
    public void startGame() {
        System.out.println("Game start.");
    }
}
