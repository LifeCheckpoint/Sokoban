package com.sokoban.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.sokoban.Main;
import com.sokoban.assets.ImageAssets;
import com.sokoban.core.Logger;
import com.sokoban.polygon.combine.HintMessageBox;
import com.sokoban.polygon.container.ImageButtonContainer;
import com.sokoban.polygon.manager.BackgroundGrayParticleManager;
import com.sokoban.utils.ActionUtils;

/**
 * 地图编辑界面
 * @author Life_Checkpoint
 */
public class MapEditScene extends SokobanScene {

    // Background 粒子
    private BackgroundGrayParticleManager bgParticle;

    // 按钮
    private ImageButtonContainer buttonContainer;
    private Image returnButton;
    private Image layerUpButton, layerDownButton;

    public MapEditScene(Main gameMain) {
        super(gameMain);
    }

    public void init() {
        super.init();

        buttonContainer = new ImageButtonContainer(gameMain);

        returnButton = buttonContainer.create(ImageAssets.LeftArrowButton);
        returnButton.setPosition(0.5f, 8f);

        // 调整上下层按钮
        ImageButtonContainer selectorButtonContainer = new ImageButtonContainer(gameMain);
        layerUpButton = selectorButtonContainer.create(ImageAssets.UpSquareButton);
        layerUpButton.setSize(0.6f, 0.6f);
        layerUpButton.setPosition(0.8f, 4f);
        layerDownButton = selectorButtonContainer.create(ImageAssets.DwonSquareButton);
        layerDownButton.setSize(0.6f, 0.6f);
        layerDownButton.setPosition(0.8f, 3f);

        // 返回按钮监听
        returnButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                gameMain.getScreenManager().returnPreviousScreen();
            }
        });

        bgParticle = new BackgroundGrayParticleManager(gameMain);
        bgParticle.startCreateParticles();

        ActionUtils.FadeInEffect(returnButton);
        ActionUtils.FadeInEffect(layerUpButton);
        ActionUtils.FadeInEffect(layerDownButton);

        // 添加 UI
        addActorsToStage(returnButton, layerDownButton, layerUpButton);
    }

    @Override
    public void input() {
        // 退出
        if (Gdx.input.isKeyJustPressed(Keys.ESCAPE)) {
            // TODO 保存逻辑
            gameMain.getScreenManager().returnPreviousScreen();
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