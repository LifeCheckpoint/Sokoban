package com.sokoban.polygon.container;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.sokoban.Main;
import com.sokoban.manager.APManager;

/**
 * 图像容器超类，支持对图像缩放的良好控制
 * @author Life_Checkpoint
 */
public class ImageContainer {
    protected float scaling = 0.0065f; // 适用于 64 高度图像缩放到一般高度
    protected APManager apManager;

    public ImageContainer(Main gameMain, float scaling) {
        this.apManager = gameMain.getAssetsPathManager();
        this.scaling = scaling;
    }

    public ImageContainer(Main gameMain) {
        this.apManager = gameMain.getAssetsPathManager();
    }

    public ImageContainer() {}

    // 从文件读取图片
    protected Drawable readDrawableFromFile(APManager.ImageAssets resourceEnum) {
        Texture texture = apManager.get(resourceEnum);
        TextureRegion region = new TextureRegion(texture);
        region.setRegion(0, 0, texture.getWidth(), texture.getHeight());
        return new TextureRegionDrawable(region);
    }

    /**
     * 创建图片组件
     * @param internalpath Image 路径
     * @param torchable 指定是否允许交互
     * @return Image 组件
     */
    protected Image create(APManager.ImageAssets resourceEnum, boolean torchable) {
        return create(readDrawableFromFile(resourceEnum), torchable);
    }

    /**
     * 创建图片组件
     * @param drawable drawable 绘制对象
     * @param torchable 指定是否允许交互
     * @return Image 组件
     */
    protected Image create(Drawable drawable, boolean torchable) {
        return create(drawable, torchable, this.scaling);
    }

    /**
     * 创建图片组件
     * @param drawable drawable 绘制对象
     * @param torchable 指定是否允许交互
     * @param scaling 缩放比例
     * @return Image 组件
     */
    protected Image create(Drawable drawable, boolean torchable, float scaling) {
        Image imageObject = new Image(drawable);
    
        // 获取素材原始宽高
        float originalWidth = drawable.getMinWidth();
        float originalHeight = drawable.getMinHeight();
    
        // 设置显示尺寸
        imageObject.setSize(originalWidth * scaling, originalHeight * scaling);
    
        // 设置响应区域 保持与显示区域一致
        if (torchable) imageObject.setTouchable(Touchable.enabled);
    
        return imageObject;
    }

    public void setScaling(float scaling) {
        this.scaling = scaling;
    }

    public float getScaling() {
        return scaling;
    }
}
