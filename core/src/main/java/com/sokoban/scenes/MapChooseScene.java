package com.sokoban.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.sokoban.Main;
import com.sokoban.manager.APManager;
import com.sokoban.manager.AccelerationMovingManager;
import com.sokoban.manager.MouseMovingTraceManager;
import com.sokoban.manager.OverlappingManager;
import com.sokoban.manager.OverlappingManager.OverlapStatue;
import com.sokoban.polygon.SpineObject;
import com.sokoban.polygon.TimerClock;
import com.sokoban.polygon.BoxObject.BoxType;
import com.sokoban.polygon.actioninterface.ClockEndCallback;
import com.sokoban.polygon.combine.HintMessageBox;
import com.sokoban.polygon.combine.Stack2DGirdWorld;
import com.sokoban.polygon.container.ImageButtonContainer;
import com.sokoban.utils.ActionUtils;

public class MapChooseScene extends SokobanScene {
    private AccelerationMovingManager accelerationManager;
    private AccelerationMovingManager.Direction preDirection = AccelerationMovingManager.Direction.None;
    private boolean isPlayerInMove;
    private Image returnButton;
    private Levels level;
    private MouseMovingTraceManager moveTrace;
    private SpineObject playerSpine;
    private OverlappingManager overlapManager;
    public TimerClock timer;

    private OriginLevel originLevel;

    /**
     * Origin Level 组件
     */
    private class OriginLevel {
        public Stack2DGirdWorld gridMap;
    }

    // 关卡名
    public enum Levels {
        Origin("origin"),
        Moving("moving"),
        Random("random");

        private final String levelName;
        Levels(String levelName) {this.levelName = levelName;}
        public String getLevelName() {return levelName;}
    }

    public MapChooseScene(Main gameMain, Levels levelEnum) {
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

        // 提示
        HintMessageBox msgBox = new HintMessageBox(gameMain, level.getLevelName());
        msgBox.setPosition(8f, 0.5f);

        // 玩家
        playerSpine = new SpineObject(gameMain, APManager.SpineAssets.Player1);
        playerSpine.stayAnimationAtFirst("down");
        playerSpine.setSize(1f, 1f);
        playerSpine.setPosition(8f - playerSpine.getWidth() / 2, 4.5f - playerSpine.getHeight() / 2);

        // 视角跟随
        moveTrace = new MouseMovingTraceManager(viewport, 8f - playerSpine.getWidth() / 2, 4.5f - playerSpine.getHeight() / 2);

        // 加速度管理器
        accelerationManager = new AccelerationMovingManager(playerSpine, 0.006f, 0.08f, 0.93f);

        // 碰撞管理器，这里只能添加共有物体
        overlapManager = new OverlappingManager(gameMain, playerSpine);
        overlapManager.addSecondaryObject(returnButton);

        // 根据场景不同调用对应初始化
        switch (level) {
            case Origin:
                setupLevelOrigin();
                break;
            default:
                break;
        }

        addActorsToStage(returnButton, playerSpine);
        addCombinedObjectToStage(msgBox);

        ActionUtils.FadeInEffect(returnButton);
        ActionUtils.FadeInEffect(playerSpine);
        
    }

    /**
     * 返回按钮触发返回上一场景
     */
    public void returnToPreviousScreen() {
        stage.addAction(Actions.sequence(
            // 停止碰撞检测并渐隐所有物体
            Actions.run(() -> {
                overlapManager.disableAllCollision();

                returnButton.addAction(Actions.fadeOut(0.3f, Interpolation.sine));
                playerSpine.addAction(Actions.fadeOut(0.3f, Interpolation.sine));
                if (originLevel != null) {
                    originLevel.gridMap.getAllActors().forEach(actor -> actor.addAction(Actions.fadeOut(0.3f, Interpolation.sine)));
                    if (timer != null) timer.remove();
                }
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
        if (Gdx.input.isKeyPressed(Keys.W) || Gdx.input.isKeyPressed(Keys.UP)) {
            playerMoveAnimation(AccelerationMovingManager.Direction.Up);
        }
        else if (Gdx.input.isKeyPressed(Keys.S) || Gdx.input.isKeyPressed(Keys.DOWN)) {
            playerMoveAnimation(AccelerationMovingManager.Direction.Down);
        }
        else if (Gdx.input.isKeyPressed(Keys.A) || Gdx.input.isKeyPressed(Keys.LEFT)) {
            playerMoveAnimation(AccelerationMovingManager.Direction.Left);
        }
        else if (Gdx.input.isKeyPressed(Keys.D) || Gdx.input.isKeyPressed(Keys.RIGHT)) {
            playerMoveAnimation(AccelerationMovingManager.Direction.Right);
        }
        else {
            playerMoveAnimation(AccelerationMovingManager.Direction.None);
        }

        // 退出
        if (Gdx.input.isKeyJustPressed(Keys.ESCAPE)) {
            returnToPreviousScreen();
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
        overlapManager.overlapsCheck();

        // Origin Level
        // 触碰返回按钮触发
        if (returnButton != null && overlapManager.getActorOverlapState(returnButton) == OverlapStatue.FirstOverlap) {
            // 首次碰撞，启动时钟
            timer = new TimerClock(gameMain, returnButton, 1.5f, new ClockEndCallback() {
                @Override
                public void clockEnd() {MapChooseScene.this.returnToPreviousScreen();}
            }, false);
            timer.moveBy(-0.2f, 0);
            addActorsToStage(timer);
        }

        // 从返回按钮退出，停止启动时钟
        if (timer != null && overlapManager.getActorOverlapState(returnButton) == OverlapStatue.FirstLeave) {
            timer.cancel();
        }
    
    }

    /**
     * 关卡 origin 初始化
     */
    private void setupLevelOrigin() {
        originLevel = new OriginLevel();

        // TODO 要把这些文件化
        
        originLevel.gridMap = new Stack2DGirdWorld(gameMain, 20, 12, 1f);
        originLevel.gridMap.addLayer();
        originLevel.gridMap.getTopLayer().addBox(BoxType.DarkBlueBack, new int[][] {
            {0, 4}, {0, 5}, {0, 6}, {1, 6},
            {2, 16}, {2, 15}, {4, 8}, {4, 9}, {5, 9},
            {5, 15}, {6, 17}, {7, 17}, {8, 17}, {10, 17},
            {7, 10}, {7, 13}, {9, 12}, {9, 11}, {10, 5},
            {10, 11}, {10, 14}, {11, 13}, {11, 12}
        });
        originLevel.gridMap.getTopLayer().addBox(BoxType.DarkGrayBack, new int[][] {
            {0, 3}, {0, 1}, {0, 0}, {2, 4},
            {2, 12}, {2, 13}, {4, 4}, {4, 11}, {5, 11},
            {5, 16}, {6, 9}, {7, 10}, {8, 19}, {10, 0},
            {7, 0}, {7, 1}, {9, 2}, {9, 3}, {10, 15},
            {10, 10}, {10, 18}, {11, 6}, {11, 7}
        });
        originLevel.gridMap.addLayer();
        originLevel.gridMap.getTopLayer().addBox(BoxType.CornerRightDown, new int[][] {
            {0, 4}, {2, 16}, {4, 8}, {5, 15},
            {6, 17}, {7, 9}, {9, 14}, {10, 5},
            {10, 11}, {11, 19}
        });

        originLevel.gridMap.setPosition(8f, 4.5f);
        // originLevel.gridMap.getLayer(1).getAllActors().forEach(actor -> actor.setZIndex(3));
        // originLevel.gridMap.getLayer(0).getAllActors().forEach(actor -> actor.setZIndex(4));
        originLevel.gridMap.addActorsToStage(stage);
        addActorsToStage(playerSpine);
    }

    // 重绘逻辑
    private void draw() {
        // 更新鼠标跟踪、主角视角
        moveTrace.setPositionWithUpdate(playerSpine);
        
        // 更新碰撞检测器
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
