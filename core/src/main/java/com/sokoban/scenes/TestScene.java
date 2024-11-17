package com.sokoban.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.sokoban.Main;
import com.sokoban.polygon.BoxObject.BoxType;
import com.sokoban.polygon.combine.GirdWorld;
// import com.sokoban.manager.APManager;
// import com.sokoban.polygon.SpineObject;
import com.sokoban.polygon.combine.HintMessageBox;
import com.sokoban.polygon.combine.ImageFontStringObject;

public class TestScene extends SokobanScene {
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

        // Spine 背景玩家装饰测试
        // SpineObject playerObject = new SpineObject(gameMain, APManager.SpineAssets.TestPlayer1);
        // playerObject.setAnimation(0, "left", true);

        // playerObject.setPosition(4f, 4f);
        // playerObject.setSize(1f, 1f);

        // SpineObject playerObject2 = new SpineObject(gameMain, APManager.SpineAssets.Player1);
        // playerObject2.setAnimation(0, "down", true);

        // playerObject2.setPosition(8f, 4f);
        // playerObject2.setSize(1f, 1f);

        // stage.addActor(playerObject2);
        // stage.addActor(playerObject);

        // 小世界测试
        GirdWorld girdWorld = new GirdWorld(gameMain, 6, 5, 0.8f);
        girdWorld.addBox(BoxType.CornerRightDown, 0, 0);
        girdWorld.addBox(BoxType.CornerRightDown, 4, 0);
        girdWorld.addBox(BoxType.CornerRightDown, 0, 5);
        girdWorld.addBox(BoxType.CornerRightDown, 4, 5);
        girdWorld.remove(4, 5);
        girdWorld.addBox(BoxType.CornerRightDown, 4, 4);
        girdWorld.addActorsToStage(stage);
    }

    // 重绘逻辑
    public void draw(float delta) {
        stage.draw();
    }

    @Override
    public void logic(float delta) {}

    @Override
    public void input() {
        if (Gdx.input.isKeyJustPressed(Keys.SPACE)) System.out.println("space");
    }

    // 资源释放
    @Override
    public void dispose() {
        super.dispose();
    }
}
