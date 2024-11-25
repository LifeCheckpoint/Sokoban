package com.sokoban.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.sokoban.Main;
import com.sokoban.assets.ImageAssets;
import com.sokoban.core.Logger;
import com.sokoban.polygon.combine.CheckboxObject;
import com.sokoban.polygon.container.ButtonCheckboxContainers;
import com.sokoban.polygon.container.ImageButtonContainer;
import com.sokoban.polygon.manager.BackgroundGrayParticleManager;
import com.sokoban.polygon.manager.MouseMovingTraceManager;
import com.sokoban.utils.ActionUtils;

/**
 * 关于界面
 * @author Life_Checkpoint
 */
public class SelectArchiveScene extends SokobanScene {
    private GameWelcomeScene gameWelcomeScene;

    // 画面相机跟踪
    private MouseMovingTraceManager moveTrace;

    // Background 粒子
    private BackgroundGrayParticleManager bgParticle;

    // UI
    private ImageButtonContainer buttonContainer;
    private ButtonCheckboxContainers buttonCheckboxContainer;
    private Image returnButton;
    private CheckboxObject save1Button;
    private CheckboxObject save2Button;
    private CheckboxObject save3Button;

    public SelectArchiveScene(Main gameMain, GameWelcomeScene gameWelcomeScene) {
        super(gameMain);
        this.gameWelcomeScene = gameWelcomeScene;
    }

    public void init() {
        super.init();

        moveTrace = new MouseMovingTraceManager(viewport);

        buttonContainer = new ImageButtonContainer(gameMain);
        buttonCheckboxContainer = new ButtonCheckboxContainers();

        returnButton = buttonContainer.create(ImageAssets.LeftArrowButton);
        returnButton.setPosition(0.5f, 8f);

        // 返回按钮监听
        returnButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                gameMain.getScreenManager().returnPreviousScreen();
            }
        });

        // 存档选择按钮
        save1Button = buttonCheckboxContainer.create(gameMain, ImageAssets.Save1Button, false, true);
        save2Button = buttonCheckboxContainer.create(gameMain, ImageAssets.Save2Button, false, true);
        save3Button = buttonCheckboxContainer.create(gameMain, ImageAssets.Save3Button, false, true);
        save1Button.setPosition(8f - save1Button.getWidth() / 2, 5.8f);
        save2Button.setPosition(8f - save1Button.getWidth() / 2, 4.5f);
        save3Button.setPosition(8f - save1Button.getWidth() / 2, 3.2f);
        save1Button.setCheckboxType(false);
        save2Button.setCheckboxType(false);
        save3Button.setCheckboxType(false);

        // 点击读取存档按钮事件
        save1Button.getCheckboxText().addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                readArchives(0);
            }
        });
        save2Button.getCheckboxText().addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                readArchives(1);
            }
        });
        save3Button.getCheckboxText().addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                readArchives(2);
            }
        });

        bgParticle = new BackgroundGrayParticleManager(gameMain);
        bgParticle.startCreateParticles();

        ActionUtils.FadeInEffect(returnButton);
        save1Button.getAllActors().forEach(ActionUtils::FadeInEffect);
        save2Button.getAllActors().forEach(ActionUtils::FadeInEffect);
        save3Button.getAllActors().forEach(ActionUtils::FadeInEffect);

        addActorsToStage(returnButton);
        addCombinedObjectToStage(save1Button, save2Button, save3Button);
    }

    private void readArchives(int archiveIndex) {
        // TODO 显示存档信息，将确定逻辑放到对应按钮
        Logger.info("SelectArchiveScene", "Switch to archive #" + (archiveIndex + 1));
        gameWelcomeScene.setCurrentArchive(gameMain.getLoginUser().getSaveArchives().get(archiveIndex), archiveIndex + 1);
        gameMain.getScreenManager().returnPreviousScreen();
    }

    @Override
    public void input() {
        // 退出
        if (Gdx.input.isKeyJustPressed(Keys.ESCAPE)) {
            gameMain.getScreenManager().returnPreviousScreen();
        }
    }

    @Override
    public void draw(float delta) {
        // 相机跟踪
        moveTrace.setPositionWithUpdate();
        
        stage.draw();
    }

    @Override
    public void logic(float delta) {}

    @Override
    public void hide() {}
}