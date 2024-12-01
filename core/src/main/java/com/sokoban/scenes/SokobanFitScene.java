package com.sokoban.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.sokoban.Main;
import com.sokoban.polygon.combine.SokobanCombineObject;

/**
 * 拥有固定 Stage 与可变 Stage 的 Scene 超类
 * <br><br>
 * 基于每个 Stage 控制输入处理权和初始化权
 * @author Life_Checkpoint
 */
public abstract class SokobanFitScene extends SokobanScene {
    /** Scene 初始化标志 */
    protected boolean initFlag = false;

    /** 舞台 */
    protected Stage UIStage;
    /** FitViewport 视口，比例为 16: 9 */
    protected Viewport UIViewport;
    /** 输入处理器 */
    protected InputMultiplexer inputMultiplexer;
    /** 游戏全局句柄 */
    protected Main gameMain;

    /** 画面更新固定逻辑步长 */
    public static final float UPDATE_TIME_STEP = 1 / 100f;
    /** 单一帧内画面更新最大次数 */
    public static final int FRAME_MAX_UPDATES = 5;
    /** 当前累积的真实时间 */
    protected float accumulatorIntegratedTime = 0f;

    /**
     * 基类初始化，需要传入 gameMain
     * @param gameMain 全局句柄
     */
    public SokobanFitScene(Main gameMain) {
        super(gameMain);
        this.gameMain = gameMain;
    }

    public Main getGameMain() {
        return gameMain;
    }

    public Stage getStage() {
        return stage;
    }

    /**
     * 重写显示方法，设置多输入处理器
     * <br><br>
     * {@inheritDoc}
     */
    @Override
    public void show() {
        if (!initFlag) {
            init();
            initFlag = true;  // 初始化执行一次
        }

        inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(UIStage);
        inputMultiplexer.addProcessor(stage);

        Gdx.input.setInputProcessor(inputMultiplexer); // 设置多输入处理器
    }

    /**
     * 屏幕切换到隐藏状态
     */
    @Override
    public void hide() {}

    /** 
     * 屏幕初始化
     * <br><br>
     * 注意，<b>调用 super.init() 确保超类初始化</b>
     */
    protected void init() {
        // 设置可变 UI Stage
        viewport = new FitViewport(16, 9);
        stage = new Stage(viewport);
        // 设置固定 UI Stage
        UIViewport = new FitViewport(16, 9);
        UIStage = new Stage(UIViewport);
    }

    /**
     * 抽象输入调用，子类必须实现
     * <br><br>
     * input 方法仅会执行<b>一次</b>
     */
    public abstract void input();

    /**
     * 抽象渲染调用，子类必须实现
     * <br><br>
     * 方法会执行<b>多次</b>以确保帧率同步
     */
    public abstract void draw(float delta);

    /**
     * 抽象逻辑调用，子类必须实现
     * <br><br>
     * logic 方法仅会执行<b>一次</b>
     */
    public abstract void logic(float delta);

    /**
     * 渲染固定舞台，对渲染方法进行重写
     * <br><br>
     * {@inheritDoc}
     */
    @Override
    public void render(float delta) {
        // 限制 delta 防止异常时间波动
        delta = Math.min(delta, 0.25f);
        accumulatorIntegratedTime += delta;

        // 达到累计时间，才会执行逻辑步
        if (accumulatorIntegratedTime >= UPDATE_TIME_STEP) {
            input();
            logic(UPDATE_TIME_STEP);
        }

        int updates = 0;
        // 累积时间超出更新时间步，且更新次数少于最大更新次数，进行 draw 额外的更新
        while (accumulatorIntegratedTime >= UPDATE_TIME_STEP && updates < FRAME_MAX_UPDATES) {
            draw(UPDATE_TIME_STEP);
            accumulatorIntegratedTime -= UPDATE_TIME_STEP;
            updates++;
        }

        // 如果超出最大更新次数，清空剩余时间
        if (updates >= FRAME_MAX_UPDATES) {
            accumulatorIntegratedTime = 0f;
        }

        // stage 应该与真实时间同步进行
        stage.act(delta);
        UIStage.act(delta);
        draw(UPDATE_TIME_STEP);
    };

    /**
     * 场景销毁
     */
    @Override
    public void dispose() {
        super.dispose();
        if (UIStage != null) UIStage.dispose();
    }

    /**
     * 将所有 Actor 加入 UIStage
     */
    public void addActorsToUIStage(Actor... actors) {
        for(Actor actor : actors) UIStage.addActor(actor);
    }

    /**
     * 将所有 CombinedObject 加入 UIStage
     */
    public void addCombinedObjectToUIStage(SokobanCombineObject... combineObjects) {
        for(SokobanCombineObject combineObject : combineObjects) combineObject.addActorsToStage(UIStage);;
    }
}