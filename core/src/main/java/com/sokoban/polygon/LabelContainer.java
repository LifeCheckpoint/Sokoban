package com.sokoban.polygon;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

public class LabelContainer extends Label {
    private float maxWidth;  // 最大宽
    private float maxHeight; // 最大高
    private BitmapFont font; // 字体

    public LabelContainer(CharSequence text, BitmapFont font, Color fontColor, float maxWidth, float maxHeight) {
        super(text, new LabelStyle(font, fontColor));
        this.font = font;
        this.maxWidth = maxWidth;
        this.maxHeight = maxHeight;

        setFontScaleToFit();
    }

    public LabelContainer(CharSequence text) {
        super(text, new LabelStyle(new BitmapFont(Gdx.files.internal("fonts/meta-normal.ttf")), Color.WHITE));
        this.maxWidth = 20f;
        this.maxHeight = 0.3f;
        this.font = new BitmapFont(Gdx.files.internal("fonts/meta-normal.ttf"));

        setFontScaleToFit();
    }

    // 设置最大宽
    public void setMaxWidth(float maxWidth) {
        this.maxWidth = maxWidth;
        setFontScaleToFit();
    }

    // 设置最大高
    public void setMaxHeight(float maxHeight) {
        this.maxHeight = maxHeight;
        setFontScaleToFit();
    }

    // 设置字体
    public void setFont(BitmapFont font) {
        this.font = font;
        getStyle().font = font;
        setFontScaleToFit();
    }

    // 调整字体缩放以适应最大宽度和高度
    private void setFontScaleToFit() {
        if (font == null || maxWidth <= 0 || maxHeight <= 0) {
            return;
        }

        // 测量文本的原始宽度和高度
        float textWidth = font.getRegion().getRegionWidth();
        float textHeight = font.getCapHeight();

        // 计算适合的缩放比例
        float scaleX = maxWidth / textWidth;
        float scaleY = maxHeight / textHeight;
        float scale = Math.min(scaleX, scaleY); // 使用最小缩放比例，保证在最大宽高范围内

        // 设置缩放比例
        setFontScale(scale);
        setSize(getPrefWidth() * scale, getPrefHeight() * scale);
    }

    // 设置文本内容
    @Override
    public void setText(CharSequence newText) {
        super.setText(newText);
        setFontScaleToFit(); // 每次更新文本后重新调整缩放
    }
}
