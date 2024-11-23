package com.sokoban.scenes;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.sokoban.Main;
import com.sokoban.core.Logger;
import com.sokoban.manager.APManager;
import com.sokoban.manager.BackgroundGrayParticleManager;
import com.sokoban.polygon.WhiteProgressBar;
import com.sokoban.polygon.container.ImageLabelContainer;
import com.sokoban.utils.ActionUtils;

/**
 * 素材加载界面
 * @author Life_Checkpoint
 */
public class LoadingScene extends SokobanScene {

    private BackgroundGrayParticleManager bgParticle;
    private Image label;
    private WhiteProgressBar progressBar;
    private SpriteBatch batch;
    private SokobanScene targetScreen;
    private float progress;
    private APManager assetsPathManager;

    public LoadingScene(Main gameMain, SokobanScene targetScreen, APManager assetsPathManager) {
        super(gameMain);
        this.targetScreen = targetScreen;
        this.assetsPathManager = assetsPathManager;
    }

    @Override
    public void init() {
        super.init();

        // 初始化背景粒子系统
        bgParticle = new BackgroundGrayParticleManager(gameMain);
        bgParticle.startCreateParticles();

        // 初始化用于渲染的SpriteBatch
        batch = new SpriteBatch();

        // 进度条设置
        progressBar = new WhiteProgressBar(gameMain);
        progressBar.setPosition((stage.getWidth() - progressBar.getWidth()) / 2, 0.2f * viewport.getWorldHeight());

        // 加载文本标签设置
        label = new ImageLabelContainer(gameMain, 0.007f).create(APManager.ImageAssets.LoadingAssetsLabel);
        label.setPosition((stage.getWidth() - label.getWidth()) / 2, 0.3f * viewport.getWorldHeight());

        ActionUtils.FadeInEffect(progressBar);
        ActionUtils.FadeInEffect(label);

        addActorsToStage(progressBar, label);

        // 开始实际加载 Assets
        assetsPathManager.startAssetsLoading();
    }

    // 渲染进度并更新
    @Override
    public void draw(float delta) {
        progress = assetsPathManager.getProgress();

        // 清除屏幕并绘制加载UI
        batch.begin();
        stage.draw();

        // 更新进度条
        progressBar.setValue(progress);
        batch.end();
    }

    @Override
    public void logic(float delta) {
        // 加载资源，并在所有资源加载完成后，切换到目标界面
        try {
            if (assetsPathManager.update()) gameMain.getScreenManager().setScreenWithoutSaving(targetScreen);
        } catch (Exception e) {
            Logger.error("LoadingScene", "Oops... It seems some assets dosen't read correctly: " + e.getMessage());
            Logger.error("LoadingScene", "Game will continue to load but the crash could happen unpredictablly");
        }
    }

    @Override
    public void input() {}

    @Override
    public void dispose() {
        batch.dispose();
        super.dispose();
    }
}
