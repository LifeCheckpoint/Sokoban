package com.sokoban.polygon;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;

public class ImageButtonContainer {
    private float maxButtonHeight;
    private final float buttonScale = 1.2f;
    private final float scaleTime = 0.2f;

    public ImageButtonContainer(float maxButtonHeight) {
        this.maxButtonHeight = maxButtonHeight;
    }

    public Drawable readDrawableFromFile(String internalpath) {
        return new TextureRegionDrawable(new TextureRegion(new Texture(internalpath)));
    }

    public ImageButton createButton(String internalpath) {
        return createButton(readDrawableFromFile(internalpath));
    }

    public ImageButton createButton(Drawable drawable) {
        ImageButton button = new ImageButton(drawable);

        // 高宽
        button.setTransform(true);
        button.setSize(button.getPrefWidth() / button.getPrefHeight() * maxButtonHeight, maxButtonHeight);

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
