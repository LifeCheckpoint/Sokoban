package com.sokoban.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.sokoban.Main;
import com.sokoban.manager.AssetsPathManager;
import com.sokoban.manager.BackgroundGrayParticleManager;
import com.sokoban.polygon.WhiteProgressBar;

public class LoadingScene extends SokoyoScene {

    private BackgroundGrayParticleManager bgParticle;
    private Image label;
    private WhiteProgressBar progressBar;
    private SpriteBatch batch;
    private SokoyoScene targetScreen;
    private float progress;
    private AssetsPathManager assetsPathManager;

    public LoadingScene(Main gameMain, SokoyoScene targetScreen, AssetsPathManager assetsPathManager) {
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
        progressBar = new WhiteProgressBar();
        progressBar.setPosition((stage.getWidth() - progressBar.getWidth()) / 2, 0.2f * viewport.getWorldHeight());

        // 加载文本标签设置
        label = new Image(new TextureRegionDrawable(new TextureRegion(AssetsPathManager.textureLoad("loading_assets.png"))));
        label.setScale(0.005f);
        label.setPosition((stage.getWidth() - label.getWidth() * 0.005f) / 2, 0.3f * viewport.getWorldHeight());

        stage.addActor(progressBar);
        stage.addActor(label);

        // 开始实际加载 Assets
        assetsPathManager.startAssetsLoading();
    }

    // 渲染进度并更新
    private void draw() {
        if (assetsPathManager.update()) {
            // 所有资源加载完成后，切换到目标界面
            gameMain.getScreenManager().setScreenWithoutSaving(targetScreen);
        } else {
            // 获取并更新进度值
            progress = assetsPathManager.getProgress();

            // 清除屏幕并绘制加载UI
            batch.begin();
            stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 60f)); // 更新舞台逻辑
            stage.draw();

            // 更新进度条
            progressBar.setValue(progress);
            batch.end();
        }
    }

    @Override
    public void render(float delta) {
        draw();
    }

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        batch.dispose();
        super.dispose();
    }
}
