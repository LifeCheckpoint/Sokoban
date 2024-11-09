package com.sokoban.polygon;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.esotericsoftware.spine.*;
import com.sokoban.Main;

/**
 * Spine 对象类，支持移动缩放与动画切换
 */
public class SpineObject extends Actor {
    private TextureAtlas playerAtlas;
    private SkeletonJson json;
    private SkeletonData playerSkeletonData;
    private AnimationStateData playerAnimationData;
    private PolygonSpriteBatch batch;
    private SkeletonRenderer skeletonRenderer;
    private Skeleton skeleton;
    private AnimationState animationState;
    
    private float scaleX = 1f;
    private float scaleY = 1f;

    public SpineObject(Main gameMain, String atlasFilePath, String skeletonDataJsonPath) {
        // 加载纹理图集
        playerAtlas = gameMain.getAssetsPathManager().get(atlasFilePath, TextureAtlas.class);
        
        // 创建Skeleton数据
        json = new SkeletonJson(playerAtlas);
        json.setScale(1f); // 可设置全局缩放
        
        playerSkeletonData = json.readSkeletonData(gameMain.getAssetsPathManager().fileObj(skeletonDataJsonPath));
        playerAnimationData = new AnimationStateData(playerSkeletonData);
        
        batch = new PolygonSpriteBatch();
        skeletonRenderer = new SkeletonRenderer();
        skeletonRenderer.setPremultipliedAlpha(true); // 设置混合模式
        
        skeleton = new Skeleton(playerSkeletonData);
        animationState = new AnimationState(playerAnimationData);
        
        // 初始化Actor尺寸
        setSize(2f, 2f);
        setPosition(0f, 0f);
    }

    @Override
    public void setPosition(float x, float y) {
        super.setPosition(x, y);
        if (skeleton != null) {
            skeleton.setPosition(x, y);
        }
    }

    @Override
    public void setScale(float scaleX, float scaleY) {
        this.scaleX = scaleX;
        this.scaleY = scaleY;
        if (skeleton != null) {
            skeleton.getRootBone().setScale(scaleX, scaleY);
        }
    }

    @Override
    public void setSize(float width, float height) {
        super.setSize(width, height);
        // 根据设置的尺寸计算缩放比例
        float originalWidth = skeleton.getData().getWidth();
        float originalHeight = skeleton.getData().getHeight();
        if (originalWidth != 0 && originalHeight != 0) {
            setScale(width / originalWidth, height / originalHeight);
        }
    }

    public void setAnimation(int trackIndex, String animationName, boolean loop) {
        animationState.setAnimation(trackIndex, animationName, loop);
    }

    public void addAnimation(int trackIndex, String animationName, boolean loop, float delay) {
        animationState.addAnimation(trackIndex, animationName, loop, delay);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        animationState.update(delta);
        animationState.apply(skeleton);
        skeleton.updateWorldTransform();
    }

    @Override
    public void draw(Batch parentBatch, float parentAlpha) {
        float worldX = getX();
        float worldY = getY();
        
        // 暂停父级batch的绘制
        parentBatch.end();
        
        // 使用PolygonSpriteBatch绘制
        batch.begin();
        batch.setProjectionMatrix(parentBatch.getProjectionMatrix());
        batch.setTransformMatrix(parentBatch.getTransformMatrix());
        
        // 更新骨骼位置和缩放
        skeleton.setPosition(worldX, worldY);
        skeleton.getRootBone().setScale(scaleX, scaleY);
        skeleton.updateWorldTransform();
        
        // 绘制骨骼
        skeletonRenderer.draw(batch, skeleton);
        batch.end();
        
        // 恢复父级batch的绘制
        parentBatch.begin();
    }

    public void dispose() {
        if (batch != null) batch.dispose();
        if (playerAtlas != null) playerAtlas.dispose();
    }
}