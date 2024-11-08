package com.sokoban.polygon;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.sokoban.manager.AssetsPathManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;

public class ImageButtonContainer {
    private final float buttonScale = 1.2f;
    private final float scaleTime = 0.2f;
    private float scaling = 0.0065f; // 适用于 64 宽度图像缩放到一般高度
    private AssetsPathManager apManager;

    public ImageButtonContainer(float scaling, AssetsPathManager apManager) {
        this.apManager = apManager;
        this.scaling = scaling;
    }

    public ImageButtonContainer(AssetsPathManager apManager) {
        this.apManager = apManager;
    }

    // 从文件读取图片
    public Drawable readDrawableFromFile(String internalpath) {
        Texture texture = apManager.get(internalpath, Texture.class);
        TextureRegion region = new TextureRegion(texture);
        region.setRegion(0, 0, texture.getWidth(), texture.getHeight());
        return new TextureRegionDrawable(region);
    }

    // 创建按钮
    public Image createButton(String internalpath) {
        return createButton(readDrawableFromFile(internalpath));
    }

    public Image createButton(Drawable drawable) {
        Image button = new Image(drawable);
    
        // 获取素材的原始宽高
        float originalWidth = drawable.getMinWidth();
        float originalHeight = drawable.getMinHeight();
    
        // 设置按钮的显示尺寸
        button.setSize(originalWidth * scaling, originalHeight * scaling);
    
        // 设置按钮的响应区域（保持与显示区域一致）
        button.setTouchable(Touchable.enabled);
    
        // 按钮增大缩小缓动
        button.addListener(new InputListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                button.addAction(Actions.scaleTo(buttonScale, buttonScale, scaleTime, Interpolation.sine));
            }
    
            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                button.addAction(Actions.scaleTo(1f, 1f, scaleTime, Interpolation.sine));
            }
        });
    
        return button;
    }
}
