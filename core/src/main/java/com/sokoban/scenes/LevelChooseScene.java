package com.sokoban.scenes;

import com.badlogic.gdx.Gdx;
import com.sokoban.Main;

public class LevelChooseScene extends SokoyoScene {
    

    public LevelChooseScene(Main gameMain) {
        super(gameMain);
    }

    @Override
    public void init() {
        super.init();
        
    }

    // 重绘逻辑
    private void draw() {
        // stage 更新
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    // 主渲染帧
    @Override
    public void render(float delta) {
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
