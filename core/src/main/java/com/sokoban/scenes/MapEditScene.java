package com.sokoban.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.sokoban.Main;
import com.sokoban.assets.ImageAssets;
import com.sokoban.core.Logger;
import com.sokoban.polygon.combine.HintMessageBox;
import com.sokoban.polygon.combine.TopMenu;
import com.sokoban.polygon.container.ImageButtonContainer;
import com.sokoban.polygon.manager.BackgroundGrayParticleManager;
import com.sokoban.utils.ActionUtils;
import com.sokoban.utils.WindowsFileChooser;

import net.dermetfan.gdx.scenes.scene2d.ui.FileChooser;

/**
 * 地图编辑界面
 * @author Life_Checkpoint
 */
public class MapEditScene extends SokobanScene {

    // Background 粒子
    private BackgroundGrayParticleManager bgParticle;

    // 按钮
    private Image layerUpButton, layerDownButton;

    // 菜单
    private TopMenu topMenu;

    public MapEditScene(Main gameMain) {
        super(gameMain);
    }

    public void init() {
        super.init();

        // 调整上下层按钮
        ImageButtonContainer selectorButtonContainer = new ImageButtonContainer(gameMain);
        layerUpButton = selectorButtonContainer.create(ImageAssets.UpSquareButton);
        layerUpButton.setSize(0.6f, 0.6f);
        layerUpButton.setPosition(0.8f, 4f);
        layerDownButton = selectorButtonContainer.create(ImageAssets.DownSquareButton);
        layerDownButton.setSize(0.6f, 0.6f);
        layerDownButton.setPosition(0.8f, 3f);

        topMenu = new TopMenu(gameMain, 0.2f);
        topMenu.setPosition(4.5f, 5f);



        // 返回
        topMenu.getExitButton().addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                exitEditScene();
            }
        });

        // 新建
        topMenu.getNewButton().addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                newFile();
            }
        });

        // 打开
        topMenu.getOpenButton().addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                openFile();
            }
        });

        bgParticle = new BackgroundGrayParticleManager(gameMain);
        bgParticle.startCreateParticles();

        ActionUtils.FadeInEffect(layerUpButton);
        ActionUtils.FadeInEffect(layerDownButton);
        topMenu.getAllActors().forEach(ActionUtils::FadeInEffect);

        // 添加 UI
        addActorsToStage(layerDownButton, layerUpButton);
        addCombinedObjectToStage(topMenu);
    }

    /**
     * 新建文件
     */
    public void newFile() {
        // TODO 保存逻辑
    }

    /**
     * 打开文件
     */
    public void openFile() {
        String[] placeHolder = new String[1];
        String filePath = WindowsFileChooser.selectFile(placeHolder);
        Logger.debug("MapEditScene", filePath);
    }

    /**
     * 退出编辑器
     */
    public void exitEditScene() {
        // TODO 保存逻辑
        gameMain.getScreenManager().returnPreviousScreen();
    }

    @Override
    public void input() {
        // 退出
        if (Gdx.input.isKeyJustPressed(Keys.ESCAPE)) {
            // TODO 保存逻辑
            exitEditScene();
        }
    }

    @Override
    public void draw(float delta) {
        
        stage.draw();
    }

    @Override
    public void logic(float delta) {}

    @Override
    public void hide() {}

    // 资源释放
    @Override
    public void dispose() {
        super.dispose();
    }
}