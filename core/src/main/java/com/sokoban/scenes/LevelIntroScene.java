package com.sokoban.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.sokoban.Main;
import com.sokoban.manager.APManager;
import com.sokoban.manager.MouseMovingTraceManager;
import com.sokoban.polygon.SpineObject;
import com.sokoban.polygon.container.ImageButtonContainer;

public class LevelIntroScene extends SokoyoScene {
    private Levels level;
    private Image returnButton;
    private MouseMovingTraceManager moveTrace;
    SpineObject playerSpine;

    // 关卡名
    public enum Levels {
        Origin("origin"),
        Moving("moving"),
        Random("random");

        private final String levelName;
        Levels(String levelName) {this.levelName = levelName;}
        public String getLevelName() {return levelName;}
    }

    public LevelIntroScene(Main gameMain, Levels levelEnum) {
        super(gameMain);
        this.level = levelEnum;
    }

    @Override
    public void init() {
        super.init();
        
        // 返回按钮
        ImageButtonContainer controlButtonContainer = new ImageButtonContainer(gameMain);
        returnButton = controlButtonContainer.create(APManager.ImageAssets.LeftArrowButton);
        returnButton.setPosition(0.5f, 8f);

        returnButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                gameMain.getScreenManager().returnPreviousScreen();
            }
        });

        // 玩家
        playerSpine = new SpineObject(gameMain, APManager.SpineAssets.Player1);
        playerSpine.stayAnimationAtFirst("down");
        playerSpine.setPosition(8f, 4.5f);
        playerSpine.setSize(1f, 1f);

        // 视角跟随
        moveTrace = new MouseMovingTraceManager(viewport);
        stage.addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                float speed = 0.1f;
                switch (keycode) {
                    case Keys.W:
                        playerSpine.moveBy(0, speed);
                        break;
                    case Keys.S:
                        playerSpine.moveBy(0, -speed);
                        break;
                    case Keys.A:
                        playerSpine.moveBy(-speed, 0);
                        break;
                    case Keys.D:
                        playerSpine.moveBy(speed, 0);
                        break;
                }
                return false;
            }
        });

        stage.addActor(returnButton);
        stage.addActor(playerSpine);

        switch (level) {
            case Origin:
                setupLevelOrigin();
                break;
            default:
                break;
        }
        
    }

    /**
     * 关卡 origin 初始化
     */
    private void setupLevelOrigin() {

    }

    // 重绘逻辑
    private void draw() {
        // 在更新主视角后更新相对视角
        // viewport.getCamera().position.set(playerSpine.getX(), playerSpine.getY(), 0);
        // viewport.getCamera().update();
        moveTrace.setPositionWithUpdate(playerSpine, 8f, 4.5f);
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
