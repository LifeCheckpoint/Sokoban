package com.sokoban.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.sokoban.Main;
import com.sokoban.manager.MouseMovingTraceManager;
import com.sokoban.manager.SingleActionInstanceManager;
import com.sokoban.polygon.BoxObject.BoxType;
import com.sokoban.polygon.action.ViewportRescaleAction;
import com.sokoban.polygon.combine.Stack2DGirdWorld;
import com.sokoban.scenes.MapChooseScene.Levels;

/**
 * 主游戏类，负责游戏的显示交互
 * @author Life_Checkpoint
 */
public class GameScene extends SokobanScene {
    private MouseMovingTraceManager moveTrace;
    private Levels level;
    private boolean isInEscapeMenu;
    private SingleActionInstanceManager SAIManager;
    private Actor actorHelper;

    private final float DEFAULT_CELL_SIZE = 1f;
    private final float VIEWPORT_RESCALE_RATIO = 1.6f;

    // TODO 多层堆叠
    Stack2DGirdWorld gridWorld;

    public GameScene(Main gameMain, Levels levelEnum) {
        super(gameMain);
        this.level = levelEnum;
    }

    @Override
    public void init() {
        super.init();
        moveTrace = new MouseMovingTraceManager(viewport);
        isInEscapeMenu = false;
        actorHelper = new Actor();

        // 动画单一实例管理
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
        gridWorld.addActorsToStage(stage);
        stage.addActor(actorHelper);
    }

    public void draw(float delta) {
        // 更新鼠标跟踪
        moveTrace.setPositionWithUpdate();

        stage.draw();
    }

    public void showEscapeMenu() {
        if (!SAIManager.isInAction(actorHelper)) isInEscapeMenu = true;
        SAIManager.executeAction(actorHelper, new ViewportRescaleAction(viewport, VIEWPORT_RESCALE_RATIO, 0.2f));
    }

    public void closeEscapeMenu() {
        if (!SAIManager.isInAction(actorHelper)) isInEscapeMenu = false;
        SAIManager.executeAction(actorHelper, new ViewportRescaleAction(viewport, 1 / VIEWPORT_RESCALE_RATIO, 0.2f));
    }

    @Override
    public void logic(float delta) {}

    @Override
    public void input() {
        // 检测游戏操控行为
        if (Gdx.input.isKeyJustPressed(Keys.UP)) {

        }

        if (Gdx.input.isKeyJustPressed(Keys.DOWN)) {
            
        }

        if (Gdx.input.isKeyJustPressed(Keys.LEFT)) {
            
        }

        if (Gdx.input.isKeyJustPressed(Keys.RIGHT)) {
            
        }

        // 检测退出行为
        if (Gdx.input.isKeyJustPressed(Keys.ESCAPE)) {
            if (!isInEscapeMenu) showEscapeMenu();
            else closeEscapeMenu();
        }

        // 强制退出
        if (Gdx.input.isKeyPressed(Keys.CONTROL_LEFT) && Gdx.input.isKeyJustPressed(Keys.W)) {
            returnToPreviousScreen();
        }
    }

    /**
     * 返回上一场景
     */
    public void returnToPreviousScreen() {
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
