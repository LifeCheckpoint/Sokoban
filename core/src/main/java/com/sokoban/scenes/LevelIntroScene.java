package com.sokoban.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.sokoban.Main;
import com.sokoban.manager.APManager;
import com.sokoban.manager.AccelerationMovingManager;
import com.sokoban.manager.MouseMovingTraceManager;
import com.sokoban.polygon.SpineObject;
import com.sokoban.polygon.container.ImageButtonContainer;

public class LevelIntroScene extends SokoyoScene {
    private Levels level;
    private Image returnButton;
    private MouseMovingTraceManager moveTrace;
    private SpineObject playerSpine;
    private AccelerationMovingManager accelerationManager;

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
        playerSpine.setSize(1f, 1f);
        playerSpine.setPosition(8f - playerSpine.getWidth() / 2, 4.5f - playerSpine.getHeight() / 2);

        // 视角跟随
        moveTrace = new MouseMovingTraceManager(viewport, 8f - playerSpine.getWidth() / 2, 4.5f - playerSpine.getHeight() / 2);

        // 加速度管理器
        accelerationManager = new AccelerationMovingManager(playerSpine, 0.006f, 0.08f, 0.93f);

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

    // 处理键盘输入
    private void input() {
        if (Gdx.input.isKeyPressed(Keys.W)) accelerationManager.updateActorMove(AccelerationMovingManager.Direction.Up);
        else if (Gdx.input.isKeyPressed(Keys.S)) accelerationManager.updateActorMove(AccelerationMovingManager.Direction.Down);
        else if (Gdx.input.isKeyPressed(Keys.A)) accelerationManager.updateActorMove(AccelerationMovingManager.Direction.Left);
        else if (Gdx.input.isKeyPressed(Keys.D)) accelerationManager.updateActorMove(AccelerationMovingManager.Direction.Right);
        else accelerationManager.updateActorMove(AccelerationMovingManager.Direction.None);
    }

    /**
     * 关卡 origin 初始化
     */
    private void setupLevelOrigin() {

    }

    // 重绘逻辑
    private void draw() {
        // 更新鼠标跟踪、主角视角
        moveTrace.setPositionWithUpdate(playerSpine);
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
