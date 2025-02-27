package com.sokoban.scenes;

import com.badlogic.gdx.Gdx;
// import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.sokoban.Main;
import com.sokoban.polygon.InputTextField;
import com.sokoban.polygon.BoxObject.BoxType;
import com.sokoban.polygon.combine.GirdWorld;
// import com.sokoban.manager.APManager;
// import com.sokoban.polygon.SpineObject;
import com.sokoban.polygon.combine.HintMessageBox;
import com.sokoban.polygon.combine.ImageFontStringObject;
import com.sokoban.polygon.combine.QuestDialog;
import com.sokoban.polygon.combine.TopMenu;

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

        // 文本框测试
        InputTextField textField = new InputTextField(gameMain, 30);
        textField.setPosition(3f, 6f);

        // 菜单栏测试
        TopMenu topMenu = new TopMenu(gameMain, 0.2f);
        topMenu.setPosition(8f, 3f);
        addCombinedObjectToStage(topMenu);

        // 询问框测试
        QuestDialog questDialog = new QuestDialog(gameMain, "This is my frame ... QAQ ... This is my frame ... QAQ ... This is my frame ... QAQ ... ");
        questDialog.setPosition(8f, 4.5f);
        
        addActorsToStage(textField);
        questDialog.addActorsToStage(stage);
    }

    // 重绘逻辑
    public void draw(float delta) {
        stage.draw();
    }

    @Override
    public void logic(float delta) {}

    @Override
    public void input() {
        if (Gdx.input.isKeyJustPressed(Keys.SPACE)) {
            // Windows 平台似乎不能使用
            // Gdx.input.getTextInput(new Input.TextInputListener() {
            //     @Override
            //     public void input(String inputText) {
            //         System.out.println(inputText);
            //     }
        
            //     @Override
            //     public void canceled() {
            //         // 如果用户取消输入
            //         System.out.println("Input canceled");
            //     }
            // }, "Input", "hello?", "input sth.");  // 弹出输入框，默认文本是当前文本
        }
    }

    // 资源释放
    @Override
    public void dispose() {
        super.dispose();
    }
}
