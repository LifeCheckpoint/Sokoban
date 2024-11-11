package com.sokoban.manager;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Timer;
import com.sokoban.Main;
import com.sokoban.polygon.BackgroundParticle;
import com.sokoban.scenes.SokoyoScene;

public class BackgroundGrayParticleManager {
    private float particleCreateInverval = 1f;
    private float minX, minY, maxX, maxY;
    private List<BackgroundParticle> backgroundParticle;
    private SokoyoScene thisScene;
    private Stage stage;
    private Texture particleTexture;
    private Timer.Task creatingTask;

    public BackgroundGrayParticleManager(Main gameMain) {
        setInjectRange(0f, 0f, 16f, 9f);
        init(gameMain);
    }

    public BackgroundGrayParticleManager(Main gameMain, float minX, float minY, float maxX, float maxY) {
        setInjectRange(minX, minY, maxX, maxY);
        init(gameMain);
    }

    public BackgroundGrayParticleManager(Main gameMain, float minX, float minY, float maxX, float maxY, float particleCreateInverval) {
        setParticleCreateInverval(particleCreateInverval);
        setInjectRange(minX, minY, maxX, maxY);
        init(gameMain);
    }

    // 初始化
    private void init(Main gameMain) {
        this.thisScene = gameMain.getScreenManager().getCurrentScreen();
        this.stage = thisScene.getStage();

        // 初始化背景粒子
        particleTexture = gameMain.getAssetsPathManager().get(AssetsPathManager.ImageAssets.ParticleGray);
        backgroundParticle = new ArrayList<>();
        setStage(stage);

        creatingTask = new Timer.Task() {
            /**
             * 创建新粒子
             * 如果场景切出，计时器任务被忽略
             */
            @Override
            public void run() {
                if (gameMain.getScreenManager().getCurrentScreen().equals(thisScene)) addNewParticle();
            }
        };
    }

    // 设置发射范围
    public void setInjectRange(float minX, float minY, float maxX, float maxY) {
        setMinX(minX);
        setMinY(minY);
        setMaxX(maxX);
        setMaxY(maxY);
    }

    // 创建新粒子
    private void addNewParticle() {
            final float x = MathUtils.random(minX, maxX), y = MathUtils.random(minY, maxY);
            BackgroundParticle newParticle = new BackgroundParticle(x, y, particleTexture);
            backgroundParticle.add(newParticle);
            stage.addActor(newParticle);
    }

    public void startCreateParticles() {
        Timer.schedule(creatingTask, 0, particleCreateInverval);
    }

    public void stopCreateParticles() {
        if (creatingTask != null) {
            creatingTask.cancel();
        }
    }

    public void setParticleCreateInverval(float particleCreateInverval) {
        this.particleCreateInverval = particleCreateInverval;
    }
    public float getParticleCreateInverval() {
        return particleCreateInverval;
    }
    public List<BackgroundParticle> getBackgroundParticle() {
        return backgroundParticle;
    }
    public Stage getStage() {
        return stage;
    }
    public void setStage(Stage stage) {
        this.stage = stage;
    }
    public float getMinX() {
        return minX;
    }
    public void setMinX(float minX) {
        this.minX = minX;
    }
    public float getMinY() {
        return minY;
    }
    public void setMinY(float minY) {
        this.minY = minY;
    }
    public float getMaxX() {
        return maxX;
    }
    public void setMaxX(float maxX) {
        this.maxX = maxX;
    }
    public float getMaxY() {
        return maxY;
    }
    public void setMaxY(float maxY) {
        this.maxY = maxY;
    }
}
