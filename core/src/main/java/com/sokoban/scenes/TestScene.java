package com.sokoban.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.sokoban.Main;
import com.sokoban.manager.APManager;
import com.sokoban.polygon.FontExtractor;

public class TestScene extends SokoyoScene {
    private Main gameMain;

    public TestScene(Main gameMain) {
        super(gameMain);
        this.gameMain = gameMain;
    }

    @Override
    public void init() {
        super.init();
        APManager.ImageAssets[] metaNormalEnum = new APManager.ImageAssets[1];
        metaNormalEnum[0] = APManager.ImageAssets.FontpageMetaNormal;
        FontExtractor fontExtractor = new FontExtractor(gameMain, metaNormalEnum, gameMain.getAssetsPathManager().fileObj("font/meta-normal.fnt").readString());
        Image testChar = fontExtractor.getCharImage('A');
        testChar.setPosition(3f, 3f);
        stage.addActor(testChar);
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
