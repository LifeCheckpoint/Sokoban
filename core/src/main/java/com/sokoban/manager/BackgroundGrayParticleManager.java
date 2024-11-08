package com.sokoban.manager;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Timer;
import com.sokoban.polygon.BackgroundParticle;

public class BackgroundGrayParticleManager {
    private final float particleCreateInverval = 1f;
    private List<BackgroundParticle> backgroundParticle;
    private Stage stage;
    private Texture particleTexture;
    private float minX, minY, maxX, maxY;
    private Timer.Task creatingTask;

    public BackgroundGrayParticleManager(Stage stage, AssetsPathManager apManager) {
        // 初始化背景粒子
        particleTexture = apManager.get("particle1.png", Texture.class);
        backgroundParticle = new ArrayList<>();
        setStage(stage);

        // 设置发射范围
        setMinX(0f);
        setMinY(0f);
        setMaxX(16f);
        setMaxY(9f);

        creatingTask = new Timer.Task() {
            @Override
            public void run() {
                addNewParticle();
            }
        };
    }

    public BackgroundGrayParticleManager(Stage stage, AssetsPathManager apManager, float minX, float minY, float maxX, float maxY) {
        // 初始化背景粒子
        particleTexture = apManager.get("particle1.png", Texture.class);
        backgroundParticle = new ArrayList<>();
        setStage(stage);

        // 设置发射范围
        setMinX(minX);
        setMinY(minY);
        setMaxX(maxX);
        setMaxY(maxY);

        creatingTask = new Timer.Task() {
            @Override
            public void run() {
                addNewParticle();
            }
        };
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

    public List<BackgroundParticle> getBackgroundParticle() {
        return backgroundParticle;
    }
    public float getParticleCreateInverval() {
        return particleCreateInverval;
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
