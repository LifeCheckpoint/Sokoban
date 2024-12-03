package com.sokoban.scenes;

import java.time.LocalDateTime;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.sokoban.assets.ImageAssets;
import com.sokoban.core.game.Direction;
import com.sokoban.core.game.GameHistoryRecoder;
import com.sokoban.core.game.GameParams;
import com.sokoban.core.game.GameStateFrame;
import com.sokoban.core.Logger;
import com.sokoban.core.manager.JsonManager;
import com.sokoban.core.map.MapData;
import com.sokoban.core.map.gamedefault.SokobanLevels;
import com.sokoban.core.map.gamedefault.SokobanMaps;
import com.sokoban.Main;
import com.sokoban.polygon.action.ViewportRescaleAction;
import com.sokoban.polygon.BoxObject.BoxType;
import com.sokoban.polygon.combine.CheckboxObject;
import com.sokoban.polygon.combine.GameEscapeFrame;
import com.sokoban.polygon.combine.Stack2DGirdWorld;
import com.sokoban.polygon.container.ButtonCheckboxContainers;
import com.sokoban.polygon.manager.BackgroundGrayParticleManager;
import com.sokoban.polygon.manager.MouseMovingTraceManager;
import com.sokoban.polygon.manager.SingleActionInstanceManager;
import com.sokoban.utils.ActionUtils;

/**
 * 主游戏类，负责游戏的显示交互
 * @author Life_Checkpoint
 */
public class GameScene extends SokobanFitScene {
    // 状态控制
    private SokobanLevels levelEnum;
    private SokobanMaps mapEnum;
    private boolean isInEscapeMenu;
    private GameParams gameParams;

    // 功能增强
    private BackgroundGrayParticleManager bgParticle;
    private MouseMovingTraceManager moveTrace;
    private SingleActionInstanceManager SAIManager;
    private Actor actorHelper;

    // Escape Menu
    private ButtonCheckboxContainers buttonContainer;
    private GameEscapeFrame escapeFrame;
    private CheckboxObject continueGameButton;
    private CheckboxObject replayButton;
    private CheckboxObject settingsButton;
    private CheckboxObject exitGameButton;

    // 游戏主内容
    // TODO 多层堆叠
    Stack2DGirdWorld gridWorld;
    MapData currentMap;

    GameHistoryRecoder historyStates;

    private final float DEFAULT_CELL_SIZE = 1f;
    private final float VIEWPORT_RESCALE_RATIO = 1.6f;
    private final float ESCAPE_MENU_ANIMATION_DURATION = 0.2f;
    private final float ESCAPE_MENU_BUTTON_SCALING = 0.011f;
    private final float ESCAPE_MENU_BUTTON_ALIGN_X = 11f;
    private final float ESCAPE_MENU_BUTTON_ALIGN_Y = 2f;

    // TODO 地图选择
    public GameScene(Main gameMain, SokobanLevels levelEnum, SokobanMaps mapEnum, GameParams gameParams) {
        super(gameMain);
        this.levelEnum = levelEnum;
        this.mapEnum = mapEnum;
        this.gameParams = gameParams;

        Logger.info("GameScene", String.format("Enter game: level = %s, map = %s", levelEnum, mapEnum));
        Logger.debug("GameScene", "Game Params = " + new JsonManager().getJsonString(gameParams));
    }

    // 初始化游戏屏幕
    @Override
    public void init() {
        super.init();

        actorHelper = new Actor();
        bgParticle = new BackgroundGrayParticleManager(gameMain);
        bgParticle.startCreateParticles();
        isInEscapeMenu = false;
        moveTrace = new MouseMovingTraceManager(viewport);
        SAIManager = new SingleActionInstanceManager(gameMain);

        // TODO 加载地图，这里是示例
        gridWorld = new Stack2DGirdWorld(gameMain, 20, 12, DEFAULT_CELL_SIZE);
        gridWorld.addLayer();
        gridWorld.getTopLayer().addBox(BoxType.DarkBlueBack, new int[][] {
            {0, 4}, {0, 5}, {0, 6}, {1, 6},
            {2, 16}, {2, 15}, {4, 8}, {4, 9}, {5, 9},
            {5, 15}, {6, 17}, {7, 17}, {8, 17}, {10, 17},
            {7, 10}, {7, 13}, {9, 12}, {9, 11}, {10, 5},
            {10, 11}, {10, 14}, {11, 13}, {11, 12}
        });
        gridWorld.setPosition(8f, 4.5f);

        // 初始化退出菜单
        initEscapeMenu();

        // 如果竞速，则开启计时与计步
        if (gameParams.racing) {
            // TODO
        }

        gridWorld.addActorsToStage(stage);
        stage.addActor(actorHelper);

        // 添加主 Stage 的输入监听
        addStageListener();

        // 渐入动画
        gridWorld.getAllActors().forEach(ActionUtils::FadeInEffect);
    }

    /** 初始化退出菜单 */
    public void initEscapeMenu() {
        escapeFrame = new GameEscapeFrame(gameMain, 11f, 6.5f, 8f, 4.5f);
        
        buttonContainer = new ButtonCheckboxContainers();

        continueGameButton = buttonContainer.create(gameMain, ImageAssets.ContinueGame, false, true, 0.1f, ESCAPE_MENU_BUTTON_SCALING);
        continueGameButton.setPosition(ESCAPE_MENU_BUTTON_ALIGN_X, ESCAPE_MENU_BUTTON_ALIGN_Y + 4.5f);
        continueGameButton.setCheckboxType(false);

        replayButton = buttonContainer.create(gameMain, ImageAssets.PlayAgainButton, false, true, 0.1f, ESCAPE_MENU_BUTTON_SCALING);
        replayButton.setPosition(ESCAPE_MENU_BUTTON_ALIGN_X, ESCAPE_MENU_BUTTON_ALIGN_Y + 3f);
        replayButton.setCheckboxType(false);

        settingsButton = buttonContainer.create(gameMain, ImageAssets.SettingsButton, false, true, 0.1f, ESCAPE_MENU_BUTTON_SCALING);
        settingsButton.setPosition(ESCAPE_MENU_BUTTON_ALIGN_X, ESCAPE_MENU_BUTTON_ALIGN_Y + 1.5f);
        settingsButton.setCheckboxType(false);

        exitGameButton = buttonContainer.create(gameMain, ImageAssets.ExitButton, false, true, 0.1f, ESCAPE_MENU_BUTTON_SCALING);
        exitGameButton.setPosition(ESCAPE_MENU_BUTTON_ALIGN_X, ESCAPE_MENU_BUTTON_ALIGN_Y + 0f);
        exitGameButton.setCheckboxType(false);

        // 继续按钮监听
        continueGameButton.getCheckboxText().addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                closeEscapeMenu();
            }
        });

        // 重玩按钮监听
        replayButton.getCheckboxText().addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                replay();
            }
        });

        // 退出按钮监听
        exitGameButton.getCheckboxText().addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // TODO 保存逻辑
                returnToMapChooseScene();
            }
        });

        // 设置按钮监听
        settingsButton.getCheckboxText().addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                gameMain.getScreenManager().setScreen(new SettingScene(gameMain));
            }
        });

        initHistoryState();
    }

    /** 添加 Stage 输入监听 */
    public void addStageListener() {
        stage.addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                Direction moveDirection = Direction.None;

                // 在退出菜单，不检测操控行为
                if (isInEscapeMenu) return false;

                if (keycode == Keys.W || keycode == Keys.UP) {
                    moveDirection = Direction.Up;
                }
        
                if (keycode == Keys.S || keycode == Keys.DOWN) {
                    moveDirection = Direction.Down;
                }
        
                if (keycode == Keys.A || keycode == Keys.LEFT) {
                    moveDirection = Direction.Left;
                }
        
                if (keycode == Keys.D || keycode == Keys.RIGHT) {
                    moveDirection = Direction.Right;
                }
    
                // 如果进行四种移动之一
                if (moveDirection != Direction.None) {
                    GameStateFrame stateFrame = new GameStateFrame();
                    stateFrame.mapData = currentMap;
                    stateFrame.action = moveDirection;
                    stateFrame.stepCount = historyStates.getTotalFrameNum() + 1;
                    historyStates.addNewFrame(stateFrame);
                    return true;
                }
    
                // 撤销
                if (Gdx.input.isKeyJustPressed(Keys.Z)) {
                    // 如果不是初始状态
                    if (historyStates.getTotalFrameNum() != 0) {
                        // 允许撤销，但是步数和时间都会继续增长
                        historyStates.undo();
                        
                        // TODO 处理画面
                    }
                    return true;
                }
    
                // 重置
                if (Gdx.input.isKeyJustPressed(Keys.R)) {
                    replay();
                    return true;
                }

                return false;
            }
        });
    }

    public void initHistoryState() {
        // TODO 初始历史记录 MapInfo
        historyStates = new GameHistoryRecoder(gameParams);
    }

    /** 绘制屏幕场景 */
    public void draw(float delta) {
        // 更新鼠标跟踪
        moveTrace.setPositionWithUpdate();
        stage.draw();
        UIStage.draw();
    }

    /** 显示退出菜单 */
    public void showEscapeMenu() {
        if (!SAIManager.isInAction(actorHelper)) {
            isInEscapeMenu = true;

            // 缩放添加后渐显
            SAIManager.executeAction(actorHelper, Actions.sequence(
                Actions.run(() -> {
                    addCombinedObjectToUIStage(escapeFrame);
                    addCombinedObjectToUIStage(continueGameButton, settingsButton, exitGameButton, replayButton);
                }),
                Actions.parallel(
                    new ViewportRescaleAction(viewport, VIEWPORT_RESCALE_RATIO, ESCAPE_MENU_ANIMATION_DURATION),
                    Actions.run(() -> {
                        escapeFrame.getAllActors().forEach(actor -> actor.addAction(Actions.fadeIn(ESCAPE_MENU_ANIMATION_DURATION)));
                        continueGameButton.getAllActors().forEach(actor -> actor.addAction(Actions.fadeIn(ESCAPE_MENU_ANIMATION_DURATION)));
                        replayButton.getAllActors().forEach(actor -> actor.addAction(Actions.fadeIn(ESCAPE_MENU_ANIMATION_DURATION)));
                        settingsButton.getAllActors().forEach(actor -> actor.addAction(Actions.fadeIn(ESCAPE_MENU_ANIMATION_DURATION)));
                        exitGameButton.getAllActors().forEach(actor -> actor.addAction(Actions.fadeIn(ESCAPE_MENU_ANIMATION_DURATION)));
                    })
                )
            ));
        }
    }

    /** 隐藏退出菜单 */
    public void closeEscapeMenu() {
        if (!SAIManager.isInAction(actorHelper)) isInEscapeMenu = false;

        // 缩放渐隐后清除
        SAIManager.executeAction(actorHelper, Actions.sequence(
            Actions.parallel(
                new ViewportRescaleAction(viewport, 1 / VIEWPORT_RESCALE_RATIO, ESCAPE_MENU_ANIMATION_DURATION),
                Actions.run(() -> {
                    escapeFrame.getAllActors().forEach(actor -> actor.addAction(Actions.fadeOut(ESCAPE_MENU_ANIMATION_DURATION)));
                    continueGameButton.getAllActors().forEach(actor -> actor.addAction(Actions.fadeOut(ESCAPE_MENU_ANIMATION_DURATION)));
                    replayButton.getAllActors().forEach(actor -> actor.addAction(Actions.fadeOut(ESCAPE_MENU_ANIMATION_DURATION)));
                    settingsButton.getAllActors().forEach(actor -> actor.addAction(Actions.fadeOut(ESCAPE_MENU_ANIMATION_DURATION)));
                    exitGameButton.getAllActors().forEach(actor -> actor.addAction(Actions.fadeOut(ESCAPE_MENU_ANIMATION_DURATION)));
                })
            ),
            Actions.run(() -> {
                escapeFrame.getAllActors().forEach(Actor::remove);
                continueGameButton.getAllActors().forEach(Actor::remove);
                replayButton.getAllActors().forEach(Actor::remove);
                settingsButton.getAllActors().forEach(Actor::remove);
                exitGameButton.getAllActors().forEach(Actor::remove);
            })
        ));
        
    }

    /** 再玩一次 */
    public void replay() {
        // 直接重启屏幕
        gameMain.getScreenManager().setScreenWithoutSaving(new GameScene(gameMain, levelEnum, mapEnum, gameParams));
    }

    @Override
    public void logic(float delta) {}

    @Override
    public void input() {
        // 检测退出行为
        if (Gdx.input.isKeyJustPressed(Keys.ESCAPE)) {
            if (!isInEscapeMenu) showEscapeMenu();
            else closeEscapeMenu();
        }

        // 强制退出
        if (Gdx.input.isKeyPressed(Keys.CONTROL_LEFT) && Gdx.input.isKeyJustPressed(Keys.W)) {
            returnToMapChooseScene();
        }
    }

    /**
     * 返回地图选择场景
     */
    public void returnToMapChooseScene() {
        // TODO 离开教程逻辑
        stage.addAction(Actions.sequence(
            // 离开前处理
            Actions.run(() -> {}),
            // 等待指定时间
            Actions.delay(0.2f),
            // 返回上一界面
            Actions.run(() -> gameMain.getScreenManager().setScreenWithoutSaving(new MapChooseScene(gameMain, levelEnum)))
        ));
    }

    // 资源释放
    @Override
    public void dispose() {
        super.dispose();
    }
}
