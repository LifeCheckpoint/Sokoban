package com.sokoban.polygon;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.sokoban.manager.AssetsPathManager;

public class ImageLabelContainer {
    private float maxLabelHeight;
    private AssetsPathManager apManager;

    public ImageLabelContainer(float maxLabelHeight, AssetsPathManager apManager) {
        this.maxLabelHeight = maxLabelHeight;
        this.apManager = apManager;
    }

    public Image resetLabel(Image label, String internalpath, float maxLabelHeight) {
        return resetLabel(label, readDrawableFromFile(internalpath), maxLabelHeight);
    }

    public Image resetLabel(Image label, Drawable drawable, float maxLabelHeight) {
        label.setDrawable(drawable);
        label.setSize(label.getPrefWidth() / label.getPrefHeight() * maxLabelHeight, maxLabelHeight);
        return label;
    }

    public Image resetLabel(Image label, String internalpath) {
        return resetLabel(label, readDrawableFromFile(internalpath));
    }

    public Image resetLabel(Image label, Drawable drawable) {
        label.setDrawable(drawable);
        label.setSize(label.getPrefWidth() / label.getPrefHeight() * maxLabelHeight, maxLabelHeight);
        return label;
    }

    public Drawable readDrawableFromFile(String internalpath) {
        return new TextureRegionDrawable(new TextureRegion(apManager.get(internalpath, Texture.class)));
    }

    public Image createLabel(String internalpath) {
        return createLabel(readDrawableFromFile(internalpath));
    }

    public Image createLabel(Drawable drawable) {
        Image label = new Image(drawable);
        label.setSize(label.getPrefWidth() / label.getPrefHeight() * maxLabelHeight, maxLabelHeight);
        return label;
    }

    public Image createLabel(String internalpath, float maxLabelHeight) {
        return createLabel(readDrawableFromFile(internalpath), maxLabelHeight);
    }

    public Image createLabel(Drawable drawable, float maxLabelHeight) {
        Image label = new Image(drawable);
        label.setSize(label.getPrefWidth() / label.getPrefHeight() * maxLabelHeight, maxLabelHeight);
        return label;
    }
}
