package com.sokoban.polygon;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class GridSquare extends Image {
    private Texture texture;

    public GridSquare(Texture texture) {
        super(new TextureRegionDrawable(new TextureRegion(texture)));
        this.texture = texture;
    }

    public Texture getTexture() {
        return texture;
    }

    public void setTexture(Texture newTexture) {
        this.texture = newTexture;
        Drawable drawable = new TextureRegionDrawable(new TextureRegion(newTexture));
        this.setDrawable(drawable);
    }

    // 透明度设置
    public void setAlpha(float alpha) {
        Color color = getColor();
        color.a = alpha;
        setColor(color);
}
}

