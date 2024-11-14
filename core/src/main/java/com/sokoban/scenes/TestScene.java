package com.sokoban.scenes;

import com.badlogic.gdx.Gdx;
import com.sokoban.Main;
import com.sokoban.polygon.combine.HintMessageBox;
import com.sokoban.polygon.combine.ImageFontStringObject;

public class TestScene extends SokoyoScene {
    private Main gameMain;

    public TestScene(Main gameMain) {
        super(gameMain);
    }

    @Override
    public void init() {
        super.init();
        ImageFontStringObject testString = new ImageFontStringObject(gameMain, "Hello World", 0.02f);
        testString.setPosition(3f, 3f);
        testString.addActorsToStage(stage);

        ImageFontStringObject testString2 = new ImageFontStringObject(gameMain, "Hello World But MsgBox!", 0.02f);
        HintMessageBox msgBox = new HintMessageBox(gameMain, testString2);
        msgBox.setPosition(8f, 1f);
        msgBox.addActorsToStage(stage);
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
