package com.sokoban.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.sokoban.Main;
import com.sokoban.manager.APManager;
import com.sokoban.manager.AccelerationMovingManager;
import com.sokoban.manager.MouseMovingTraceManager;
import com.sokoban.polygon.SpineObject;
import com.sokoban.polygon.container.ImageButtonContainer;
import com.sokoban.utils.ActionUtils;

public class LevelIntroScene extends SokoyoScene {
    private Levels level;
    private Image returnButton;
    private MouseMovingTraceManager moveTrace;
    private SpineObject playerSpine;
    private AccelerationMovingManager accelerationManager;
    private boolean isPlayerInMove;
    private AccelerationMovingManager.Direction preDirection = AccelerationMovingManager.Direction.None;
    private boolean enableOverlapsCheck = true;

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
                returnToPreviousScreen();
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

        ActionUtils.FadeInEffect(returnButton);
        ActionUtils.FadeInEffect(playerSpine);

        switch (level) {
            case Origin:
                setupLevelOrigin();
                break;
            default:
                break;
        }
        
    }

    /**
     * 返回按钮触发返回上一场景
     */
    private void returnToPreviousScreen() {
        stage.addAction(Actions.sequence(
            // 渐隐所有物体并停止碰撞检测
            Actions.run(() -> {
                returnButton.addAction(Actions.fadeOut(0.3f, Interpolation.sine));
                playerSpine.addAction(Actions.fadeOut(0.3f, Interpolation.sine));
                enableOverlapsCheck = false;
            }),
            // 等待指定时间
            Actions.delay(0.5f),
            // 返回上一界面
            Actions.run(() -> gameMain.getScreenManager().returnPreviousScreen())
        ));
    }

    // 处理键盘输入
    private void input() {
        // 加速度管理器管理移动
        if (Gdx.input.isKeyPressed(Keys.W)) {
            playerMoveAnimation(AccelerationMovingManager.Direction.Up);
        }
        else if (Gdx.input.isKeyPressed(Keys.S)) {
            playerMoveAnimation(AccelerationMovingManager.Direction.Down);
        }
        else if (Gdx.input.isKeyPressed(Keys.A)) {
            playerMoveAnimation(AccelerationMovingManager.Direction.Left);
        }
        else if (Gdx.input.isKeyPressed(Keys.D)) {
            playerMoveAnimation(AccelerationMovingManager.Direction.Right);
        }
        else {
            playerMoveAnimation(AccelerationMovingManager.Direction.None);
        }
    }

    private void playerMoveAnimation(AccelerationMovingManager.Direction direction) {
        // 相同移动，仅第一次触发动画
        if (!isPlayerInMove && direction != AccelerationMovingManager.Direction.None) {
            playerSpine.setAnimation(0, direction.getDirection(), false);
            isPlayerInMove = true;
        }
        // 不相同移动，更改触发动画
        else if (preDirection != direction && direction != AccelerationMovingManager.Direction.None) {
            playerSpine.setAnimation(0, direction.getDirection(), false);
            isPlayerInMove = true;
        }

        // 处理移动
        accelerationManager.updateActorMove(direction);
        // 解锁动画
        if (direction == AccelerationMovingManager.Direction.None) isPlayerInMove = false;
        // 更新上一次移动
        preDirection = direction;
    }

    // 全局碰撞检测
    private void overlapsCheck() {
        if (!enableOverlapsCheck) return;
        if (returnButton != null && isOverlap(returnButton, playerSpine)) {
            returnToPreviousScreen();
        }
    }

    private boolean isOverlap(Actor actor1, Actor actor2) {
        Rectangle rectangle1 = new Rectangle(actor1.getX(), actor1.getY(), actor1.getWidth(), actor1.getHeight());
        Rectangle rectangle2 = new Rectangle(actor2.getX(), actor2.getY(), actor2.getWidth(), actor2.getHeight());
        return rectangle1.overlaps(rectangle2);
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
        // 处理碰撞事件
        overlapsCheck();
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
