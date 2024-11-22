package com.sokoban.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.sokoban.Main;
import com.sokoban.manager.APManager;
import com.sokoban.manager.BackgroundGrayParticleManager;
import com.sokoban.manager.MouseMovingTraceManager;
import com.sokoban.manager.SingleActionInstanceManager;
import com.sokoban.polygon.BoxObject.BoxType;
import com.sokoban.polygon.action.ViewportRescaleAction;
import com.sokoban.polygon.combine.CheckboxObject;
import com.sokoban.polygon.combine.GameEscapeFrame;
import com.sokoban.polygon.combine.Stack2DGirdWorld;
import com.sokoban.polygon.container.ButtonCheckboxContainers;
import com.sokoban.scenes.MapChooseScene.Levels;
import com.sokoban.utils.ActionUtils;

/**
 * 主游戏类，负责游戏的显示交互
 * @author Life_Checkpoint
 */
public class GameScene extends SokobanScene {
    // 状态
    private Levels level;
    private boolean isInEscapeMenu;

    // 功能增强
    private BackgroundGrayParticleManager bgParticle;
    private MouseMovingTraceManager moveTrace;
    private SingleActionInstanceManager SAIManager;
    private Actor actorHelper;

    // Escape Menu
    private ButtonCheckboxContainers buttonContainer;
    private GameEscapeFrame escapeFrame;
    private CheckboxObject continueGameButton;
    private CheckboxObject settingsButton;
    private CheckboxObject exitGameButton;

    // 游戏主内容
    // TODO 多层堆叠
    Stack2DGirdWorld gridWorld;

    private final float DEFAULT_CELL_SIZE = 1f;
    private final float VIEWPORT_RESCALE_RATIO = 1.6f;
    private final float ESCAPE_MENU_ANIMATION_DURATION = 0.2f;
    private final float ESCAPE_MENU_BUTTON_SCALING = 0.011f;
    private final float ESCAPE_MENU_BUTTON_ALIGN_X = 13f;
    private final float ESCAPE_MENU_BUTTON_ALIGN_Y = 3.5f;

    public GameScene(Main gameMain, Levels levelEnum) {
        super(gameMain);
        this.level = levelEnum;
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

        gridWorld.addActorsToStage(stage);
        stage.addActor(actorHelper);

        // 渐入动画
        gridWorld.getAllActors().forEach(ActionUtils::FadeInEffect);
    }

    // 初始化退出菜单，暂时不显示
    public void initEscapeMenu() {
        escapeFrame = new GameEscapeFrame(gameMain, 11f, 6.5f, 8f, 4.5f);
        
        buttonContainer = new ButtonCheckboxContainers();

        continueGameButton = buttonContainer.create(gameMain, APManager.ImageAssets.ContinueGame, false, true, 0.1f, ESCAPE_MENU_BUTTON_SCALING);
        continueGameButton.setPosition(ESCAPE_MENU_BUTTON_ALIGN_X, ESCAPE_MENU_BUTTON_ALIGN_Y + 3f);
        continueGameButton.setCheckboxType(false);

        settingsButton = buttonContainer.create(gameMain, APManager.ImageAssets.SettingsButton, false, true, 0.1f, ESCAPE_MENU_BUTTON_SCALING);
        settingsButton.setPosition(ESCAPE_MENU_BUTTON_ALIGN_X, ESCAPE_MENU_BUTTON_ALIGN_Y + 1.5f);
        settingsButton.setCheckboxType(false);

        exitGameButton = buttonContainer.create(gameMain, APManager.ImageAssets.ExitButton, false, true, 0.1f, ESCAPE_MENU_BUTTON_SCALING);
        exitGameButton.setPosition(ESCAPE_MENU_BUTTON_ALIGN_X, ESCAPE_MENU_BUTTON_ALIGN_Y + 0f);
        exitGameButton.setCheckboxType(false);

        // 继续按钮监听
        continueGameButton.getCheckboxText().addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                closeEscapeMenu();
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
    }

    public void draw(float delta) {
        // 更新鼠标跟踪
        moveTrace.setPositionWithUpdate();

        stage.draw();
    }

    // 显示退出菜单
    public void showEscapeMenu() {
        if (!SAIManager.isInAction(actorHelper)) {
            isInEscapeMenu = true;

            // 缩放添加后渐显
            SAIManager.executeAction(actorHelper, Actions.sequence(
                Actions.run(() -> {
                    addCombinedObjectToStage(escapeFrame);
                    addCombinedObjectToStage(continueGameButton, settingsButton, exitGameButton);
                }),
                Actions.parallel(
                    new ViewportRescaleAction(viewport, VIEWPORT_RESCALE_RATIO, ESCAPE_MENU_ANIMATION_DURATION),
                    Actions.run(() -> {
                        escapeFrame.getAllActors().forEach(actor -> actor.addAction(Actions.fadeIn(ESCAPE_MENU_ANIMATION_DURATION)));
                        continueGameButton.getAllActors().forEach(actor -> actor.addAction(Actions.fadeIn(ESCAPE_MENU_ANIMATION_DURATION)));
                        settingsButton.getAllActors().forEach(actor -> actor.addAction(Actions.fadeIn(ESCAPE_MENU_ANIMATION_DURATION)));
                        exitGameButton.getAllActors().forEach(actor -> actor.addAction(Actions.fadeIn(ESCAPE_MENU_ANIMATION_DURATION)));
                    })
                )
            ));
        }
    }

    // 隐藏退出菜单
    public void closeEscapeMenu() {
        if (!SAIManager.isInAction(actorHelper)) isInEscapeMenu = false;

        // 缩放渐隐后清除
        SAIManager.executeAction(actorHelper, Actions.sequence(
            Actions.parallel(
                new ViewportRescaleAction(viewport, 1 / VIEWPORT_RESCALE_RATIO, ESCAPE_MENU_ANIMATION_DURATION),
                Actions.run(() -> {
                    escapeFrame.getAllActors().forEach(actor -> actor.addAction(Actions.fadeOut(ESCAPE_MENU_ANIMATION_DURATION)));
                    continueGameButton.getAllActors().forEach(actor -> actor.addAction(Actions.fadeOut(ESCAPE_MENU_ANIMATION_DURATION)));
                    settingsButton.getAllActors().forEach(actor -> actor.addAction(Actions.fadeOut(ESCAPE_MENU_ANIMATION_DURATION)));
                    exitGameButton.getAllActors().forEach(actor -> actor.addAction(Actions.fadeOut(ESCAPE_MENU_ANIMATION_DURATION)));
                })
            ),
            Actions.run(() -> {
                escapeFrame.getAllActors().forEach(Actor::remove);
                continueGameButton.getAllActors().forEach(Actor::remove);
                settingsButton.getAllActors().forEach(Actor::remove);
                exitGameButton.getAllActors().forEach(Actor::remove);
            })
        ));
        
    }

    @Override
    public void logic(float delta) {}

    @Override
    public void input() {
        // 不在退出菜单，检测方向操控行为
        if (!isInEscapeMenu) {
            if (Gdx.input.isKeyJustPressed(Keys.UP) || Gdx.input.isKeyJustPressed(Keys.W)) {

            }
    
            if (Gdx.input.isKeyJustPressed(Keys.DOWN) || Gdx.input.isKeyJustPressed(Keys.A)) {
                
            }
    
            if (Gdx.input.isKeyJustPressed(Keys.LEFT) || Gdx.input.isKeyJustPressed(Keys.S)) {
                
            }
    
            if (Gdx.input.isKeyJustPressed(Keys.RIGHT) || Gdx.input.isKeyJustPressed(Keys.D)) {
                
            }
        }

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
        stage.addAction(Actions.sequence(
            // 离开前处理
            Actions.run(() -> {}),
            // 等待指定时间
            Actions.delay(0.2f),
            // 返回上一界面
            Actions.run(() -> gameMain.getScreenManager().setScreenWithoutSaving(new MapChooseScene(gameMain, level)))
        ));
    }

    // 资源释放
    @Override
    public void dispose() {
        super.dispose();
    }
}
