package com.sokoban.scenes;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.sokoban.Main;

// 统一父类，控制输入处理器和初始化
public abstract class SokoyoScene extends ApplicationAdapter implements Screen {
    private boolean initFlag = false;

    // 子类控制权
    protected Stage stage;
    protected Viewport viewport;
    protected Main gameMain;

    public SokoyoScene(Main gameMain) {
        this.gameMain = gameMain;
    }

    public Main getGameMain() {
        return gameMain;
    }

    @Override
    public void show() {
        if (!initFlag) {
            init();
            initFlag = true;  // 初始化执行一次
        }
        Gdx.input.setInputProcessor(stage); // 设置输入处理器
    }

    @Override
    public void hide() {}

    // 可在子类中覆盖并在初始化前调用 super.init() 确保基本初始化
    protected void init() {
        viewport = new FitViewport(16, 9); // 初始化视口
        stage = new Stage(viewport);       // 初始化舞台
    }

    // 抽象主渲染，强制子类实现
    @Override
    public abstract void render(float delta);

    @Override
    public void dispose() {
        if (stage != null) stage.dispose();
    }
}
