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
    protected boolean initFlag = false;

    protected Stage stage;
    protected Viewport viewport;
    protected Main gameMain;

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
     * 抽象主渲染，强制子类实现
     */
    @Override
    public abstract void render(float delta);

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
