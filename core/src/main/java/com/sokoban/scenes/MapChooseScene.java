package com.sokoban.scenes;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
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
import com.sokoban.polygon.BoxObject;
import com.sokoban.polygon.SpineObject;
import com.sokoban.polygon.TimerClock;
import com.sokoban.polygon.BoxObject.BoxType;
import com.sokoban.polygon.actioninterface.ClockEndCallback;
import com.sokoban.polygon.combine.HintMessageBox;
import com.sokoban.polygon.combine.Stack2DGirdWorld;
import com.sokoban.polygon.container.ImageButtonContainer;
import com.sokoban.utils.ActionUtils;
import com.sokoban.utils.MathUtilsEx;

public class MapChooseScene extends SokobanScene {
    private AccelerationMovingManager accelerationManager;
    private AccelerationMovingManager.Direction preDirection = AccelerationMovingManager.Direction.None;
    private boolean isPlayerInMove;
    private Image returnButton;
    private Levels level;
    private MouseMovingTraceManager moveTrace;
    private SpineObject playerSpine;
    private OverlappingManager playerOverlapManager;
    public Map<Actor, TimerClock> timer;

    private final float SCREEN_WIDTH_CENTER = 8f, SCREEN_HEIGHT_CENTER = 4.5f;

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
        msgBox.setPosition(SCREEN_WIDTH_CENTER, 0.5f);

        // 玩家
        playerSpine = new SpineObject(gameMain, APManager.SpineAssets.Player1);
        playerSpine.stayAnimationAtFirst("down");
        playerSpine.setSize(1f, 1f);
        playerSpine.setPosition(SCREEN_WIDTH_CENTER - playerSpine.getWidth() / 2, SCREEN_HEIGHT_CENTER - playerSpine.getHeight() / 2);

        // 视角跟随
        moveTrace = new MouseMovingTraceManager(viewport, SCREEN_WIDTH_CENTER - playerSpine.getWidth() / 2, SCREEN_HEIGHT_CENTER - playerSpine.getHeight() / 2);

        // 加速度管理器
        accelerationManager = new AccelerationMovingManager(playerSpine, 0.001f, 0.04f, 0.96f);
        accelerationManager.addBound("MapBound", -16f, 16f, -9f, 9f);

        // 碰撞管理器，这里只能添加共有物体
        playerOverlapManager = new OverlappingManager(gameMain, playerSpine);
        playerOverlapManager.addSecondaryObject(returnButton);

        // 组件 - 计时器 字典
        timer = new HashMap<>();

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
                playerOverlapManager.disableAllCollision();

                returnButton.addAction(Actions.fadeOut(0.3f, Interpolation.sine));
                playerSpine.addAction(Actions.fadeOut(0.3f, Interpolation.sine));
                if (originLevel != null) {
                    originLevel.gridMap.getAllActors().forEach(actor -> actor.addAction(Actions.fadeOut(0.3f, Interpolation.sine)));
                    // 清除计时器字典所有部件
                    if (timer != null) for (TimerClock ti : timer.values()) ti.remove();
                }
            }),
            // 等待指定时间
            Actions.delay(0.5f),
            // 返回上一界面
            Actions.run(() -> gameMain.getScreenManager().returnPreviousScreen())
        ));
    }

    /**
     * 进入游玩界面
     */
    public void enterGameScene() {
        stage.addAction(Actions.sequence(
            // 停止碰撞检测并渐隐所有物体
            Actions.run(() -> {
                playerOverlapManager.disableAllCollision();

                returnButton.addAction(Actions.fadeOut(0.3f, Interpolation.sine));
                playerSpine.addAction(Actions.fadeOut(0.3f, Interpolation.sine));
                if (originLevel != null) {
                    originLevel.gridMap.getAllActors().forEach(actor -> actor.addAction(Actions.fadeOut(0.3f, Interpolation.sine)));
                    // 清除计时器字典所有部件
                    if (timer != null) for (TimerClock ti : timer.values()) ti.remove();
                }
            }),
            // 等待指定时间
            Actions.delay(0.5f),
            // 进入游戏界面
            // TODO map 选择
            Actions.run(() -> gameMain.getScreenManager().setScreenWithoutSaving(new GameScene(gameMain, level)))
        ));
    }

    // 处理连续键盘移动，需要累积处理
    private void updateMovingInput() {
        // 加速度管理器管理移动，反向按键混合将静止
        boolean keyUp = Gdx.input.isKeyPressed(Keys.W) || Gdx.input.isKeyPressed(Keys.UP);
        boolean keyLeft = Gdx.input.isKeyPressed(Keys.A) || Gdx.input.isKeyPressed(Keys.LEFT);
        boolean keyDown = Gdx.input.isKeyPressed(Keys.S) || Gdx.input.isKeyPressed(Keys.DOWN);
        boolean keyRight = Gdx.input.isKeyPressed(Keys.D) || Gdx.input.isKeyPressed(Keys.RIGHT);

        boolean inMove = false;

        // System.out.printf("%s %s %s %s\n", keyUp, keyDown, keyLeft, keyRight);
        
        if (keyUp && !keyDown) {
            updatePlayerVelocity(AccelerationMovingManager.Direction.Up);
            inMove = true;
        }
        
        if (keyDown && !keyUp) {
            updatePlayerVelocity(AccelerationMovingManager.Direction.Down);
            inMove = true;
        }
        
        if (keyLeft && !keyRight) {
            updatePlayerVelocity(AccelerationMovingManager.Direction.Left);
            inMove = true;
        }
        
        if (keyRight && !keyLeft) {
            updatePlayerVelocity(AccelerationMovingManager.Direction.Right);
            inMove = true;
        }

        if (!inMove) updatePlayerVelocity(AccelerationMovingManager.Direction.None);

        // 完成速度更新后进行实际位移
        accelerationManager.updateActorMove();
    }

    // 处理单次键盘输入
    @Override
    public void input() {
        // 退出
        if (Gdx.input.isKeyJustPressed(Keys.ESCAPE)) {
            returnToPreviousScreen();
        }
    }

    private void updatePlayerVelocity(AccelerationMovingManager.Direction direction) {
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
        accelerationManager.updateVelocity(direction);
        // 解锁动画
        if (direction == AccelerationMovingManager.Direction.None) isPlayerInMove = false;
        // 更新上一次移动
        preDirection = direction;
    }

    // 全局碰撞检测
    private void overlapsCheck() {
        playerOverlapManager.overlapsCheck();

        // Origin Level
        // 触碰返回按钮触发
        if (returnButton != null && playerOverlapManager.getActorOverlapState(returnButton) == OverlapStatue.FirstOverlap) {
            // 首次碰撞，启动时钟
            timer.put(returnButton, 
                new TimerClock(gameMain, returnButton, 1.5f, new ClockEndCallback() {
                    @Override
                    public void clockEnd() {returnToPreviousScreen();}
                }, false)
            );
            timer.get(returnButton).moveBy(-0.2f, 0);
            addActorsToStage(timer.get(returnButton));
        }

        // 从返回按钮退出，停止启动时钟
        if (timer != null && playerOverlapManager.getActorOverlapState(returnButton) == OverlapStatue.FirstLeave) {
            if (timer.containsKey(returnButton)) {
                timer.get(returnButton).cancel();
                timer.remove(returnButton);
            }
        }

        // 对箱子的检测
        if (originLevel != null) for (Actor boxActor : originLevel.gridMap.getAllActors()) {
            if (boxActor instanceof BoxObject) {
                BoxObject boxObj = (BoxObject) boxActor;

                // 碰撞普通绿色箱子
                if (boxObj.getBoxType() == BoxType.GreenChest) {
                    if (playerOverlapManager.getActorOverlapState(boxObj) == OverlapStatue.FirstOverlap) {
                        // 箱子变为激活状态
                        boxObj.resetBoxType(BoxType.GreenChestActive);

                        // 触发计时器
                        timer.put(boxObj, 
                            new TimerClock(gameMain, boxObj, 1.5f, new ClockEndCallback() {
                                @Override
                                public void clockEnd() {enterGameScene();}
                            }, false)
                        );
                        timer.get(boxObj).moveBy(-0.3f, 0);
                        addActorsToStage(timer.get(boxObj));
                    }
                }

                // 离开绿色激活箱子
                if (boxObj.getBoxType() == BoxType.GreenChestActive) {
                    if (playerOverlapManager.getActorOverlapState(boxObj) == OverlapStatue.FirstLeave) {
                        // 箱子变为普通状态
                        boxObj.resetBoxType(BoxType.GreenChest);

                        // 取消计时器
                        if (timer.containsKey(boxObj)) {
                            timer.get(boxObj).cancel();
                            timer.remove(boxObj);
                        }
                    }
                }

                // 碰撞箱子目标点
                if (boxObj.getBoxType() == BoxType.BoxTarget) {
                    if (playerOverlapManager.getActorOverlapState(boxObj) == OverlapStatue.FirstOverlap) {
                        // 箱子变为激活状态
                        boxObj.setAnimation(1, "active", false);
                        boxObj.setAnimationTotalTime(1, 0.3f);
                    }
                }

                // 离开箱子目标点
                if (boxObj.getBoxType() == BoxType.BoxTarget) {
                    if (playerOverlapManager.getActorOverlapState(boxObj) == OverlapStatue.FirstLeave) {
                        // 箱子目标点变为普通状态
                        boxObj.setAnimation(1, "deactive", false);
                        boxObj.setAnimationTotalTime(1, 0.5f);
                    }
                }
            }
            
        }
    
    }

    /**
     * 关卡 origin 初始化
     */
    private void setupLevelOrigin() {
        originLevel = new OriginLevel();

        // TODO 将转换为文件读取初始化
        
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
        originLevel.gridMap.getTopLayer().addBox(BoxType.BoxTarget, new int[][] {
            {0, 4}, {2, 16}, {4, 8}, {5, 15},
            {6, 17}, {7, 9}, {9, 14}, {10, 5},
            {10, 11}, {11, 19}
        });
        originLevel.gridMap.getTopLayer().addBox(BoxType.GreenChest, new int[][] {
            {0, 5}, {1, 16}, {2, 16}, {6, 8}, {3, 5},
            {6, 4}, {7, 9}, {7, 5}, {9, 13}, {0, 5},
            {0, 11}, {10, 17}, {11, 19}
        });
        originLevel.gridMap.getTopLayer().addBox(BoxType.BlueChest, new int[][] {
            {3, 6}, {2, 12}, {1, 0}, {7, 5}, {6, 3},
            {9, 4}, {8, 5}, {6, 19}, {10, 10}, {3, 2},
            {3, 11}, {11, 13}, {10, 0}
        });

        originLevel.gridMap.setPosition(SCREEN_WIDTH_CENTER, SCREEN_HEIGHT_CENTER);
        // originLevel.gridMap.getLayer(1).getAllActors().forEach(actor -> actor.setZIndex(3));
        // originLevel.gridMap.getLayer(0).getAllActors().forEach(actor -> actor.setZIndex(4));
        originLevel.gridMap.addActorsToStage(stage);

        for (Actor boxActor : originLevel.gridMap.getAllActors()) {
            if (boxActor instanceof BoxObject) {
                BoxObject boxObj = (BoxObject) boxActor;

                // 为所有 GreenBox 与 BoxTarget 添加碰撞检测
                if (boxObj.getBoxType() == BoxType.GreenChest ||boxObj.getBoxType() == BoxType.BoxTarget) {
                    playerOverlapManager.addSecondaryObject(boxObj);
                }

                // 为所有 BlueBox 添加边界
                if (boxObj.getBoxType() == BoxType.BlueChest) {
                    // TODO 这里的标签与位置强相关，临时设置，后续会改
                    String BoxBlockTag = String.format("box[%.2f][%.2f]", boxObj.getX(), boxObj.getY());
                    float blockX = boxObj.getX() - 8f, blockY = boxObj.getY() - 4.5f;
                    accelerationManager.addBound(BoxBlockTag, blockX, blockX + boxObj.getSize(), blockY, blockY + boxObj.getSize());
                }

                // 为所有 BoxTarget 添加动画
                if (boxObj.getBoxType() == BoxType.BoxTarget) {
                    boxObj.setAnimation(0, "rotate", true);
                    boxObj.setAnimationTotalTime(0, MathUtils.random(8f, 12f));
                    boxObj.setFlipX(MathUtils.randomSign() > 0);
                }
            }
        }

        

        // 按照距离从近到远显示淡入
        List<Actor> girdMapActorOrded = new ArrayList<>(originLevel.gridMap.getAllActors());
        girdMapActorOrded.sort(Comparator.comparingDouble(actor -> MathUtilsEx.distance(actor.getX(), actor.getY(), SCREEN_WIDTH_CENTER, SCREEN_HEIGHT_CENTER)));
        girdMapActorOrded.forEach(actor -> ActionUtils.FadeInEffect(actor, MathUtils.random(
            MathUtilsEx.distance(actor.getX(), actor.getY(), SCREEN_WIDTH_CENTER, SCREEN_HEIGHT_CENTER) * 0.05f,
            MathUtilsEx.distance(actor.getX(), actor.getY(), SCREEN_WIDTH_CENTER, SCREEN_HEIGHT_CENTER) * 0.10f
        )));

        addActorsToStage(playerSpine);
    }

    // 重绘逻辑
    @Override
    public void draw(float delta) {
        // 更新鼠标跟踪、主角视角
        moveTrace.setPositionWithUpdate(playerSpine);

        // 更新移动检测
        updateMovingInput();
        
        // 更新碰撞检测器
        overlapsCheck();
        
        stage.draw();
    }

    // 需要连续处理的输入逻辑
    @Override
    public void logic(float delta) {}

    // 资源释放
    @Override
    public void dispose() {
        super.dispose();
    }
    
}
