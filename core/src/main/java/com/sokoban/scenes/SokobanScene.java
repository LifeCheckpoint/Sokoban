package com.sokoban.scenes;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.sokoban.Main;
import com.sokoban.polygon.combine.SokobanCombineObject;

/** 
 * Scene 的统一父类
 * <br><br>
 * 拥有控制输入处理器和初始化权
 * @author Life_Checkpoint
 */
public abstract class SokobanScene extends ApplicationAdapter implements Screen {
    /** Scene 初始化标志 */
    protected boolean initFlag = false;

    /** 舞台 */
    protected Stage stage;
    /** FitViewport 视口，比例为 16: 9 */
    protected Viewport viewport;
    /** 游戏全局句柄 */
    protected Main gameMain;

    /** 画面更新固定逻辑步长 */
    private static final float UPDATE_TIME_STEP = 1 / 100f;
    /** 单一帧内画面更新最大次数 */
    private static final int FRAME_MAX_UPDATES = 5;
    /** 当前累积的真实时间 */
    private float accumulatorIntegratedTime = 0f;

    /**
     * 基类初始化，需要传入 gameMain
     * @param gameMain 全局句柄
     */
    public SokobanScene(Main gameMain) {
        this.gameMain = gameMain;
    }

    public Main getGameMain() {
        return gameMain;
    }

    public Stage getStage() {
        return stage;
    }

    /**
     * 屏幕切换到显示状态
     */
    @Override
    public void show() {
        if (!initFlag) {
            init();
            initFlag = true;  // 初始化执行一次
        }
        Gdx.input.setInputProcessor(stage); // 设置输入处理器
    }

    /**
     * 屏幕切换到隐藏状态
     */
    @Override
    public void hide() {}

    // 可在子类中覆盖并在初始化前调用 super.init() 确保基本初始化
    protected void init() {
        viewport = new FitViewport(16, 9); // 初始化视口
        stage = new Stage(viewport);       // 初始化舞台
    }

    /**
     * 抽象输入调用，子类必须实现
     */
    public abstract void input();

    /**
     * 抽象渲染调用，子类必须实现
     */
    public abstract void draw(float delta);

    /**
     * 抽象逻辑调用，子类必须实现
     */
    public abstract void logic(float delta);

    /**
     * 屏幕渲染主方法，如果进行同步帧率步更新则必须重写
     * <br><br>
     * 注意，<b>方法执行顺序为 input -> logic -> draw </b>，并且 input 与 logic 方法仅会执行<b>一次</b>
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
        draw(UPDATE_TIME_STEP);
    };

    /**
     * 场景销毁
     */
    @Override
    public void dispose() {
        if (stage != null) stage.dispose();
    }

    /**
     * 将所有 Actor 加入 Stage
     */
    public void addActorsToStage(Actor... actors) {
        for(Actor actor : actors) stage.addActor(actor);
    }

    /**
     * 将所有 CombinedObject 加入 Stage
     */
    public void addCombinedObjectToStage(SokobanCombineObject... combineObjects) {
        for(SokobanCombineObject combineObject : combineObjects) combineObject.addActorsToStage(stage);;
    }
}
