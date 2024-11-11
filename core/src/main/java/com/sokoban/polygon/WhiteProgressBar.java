package com.sokoban.polygon;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.sokoban.manager.APManager;

public class WhiteProgressBar extends Actor {
    private float minValue = 0;  // 最小值
    private float maxValue = 1;  // 最大值
    private float value = 0;     // 当前值

    // 边界框外部矩形
    private float outerBorderWidth = 8.5f;
    private float outerBorderHeight = 0.3f;
    
    // 边界框内部矩形
    private float innerBorderWidth = 8.3f;
    private float innerBorderHeight = 0.28f;
    
    // 进度条
    private float barWidth = 8f;
    private float barHeight = 0.25f;

    // 内外矩形曼哈顿距离
    private float deltaIO = (outerBorderWidth - innerBorderWidth) / 2;
    private float deltaBI = (innerBorderWidth - barWidth) / 2;

    private Color borderColor = new Color(0.2f, 0.2f, 0.2f, 1);  // 外边框颜色
    private Color fillColor = new Color(0.3f, 0.7f, 0.3f, 1);    // 填充颜色
    private Color backgroundColor = new Color(0.1f, 0.1f, 0.1f, 1); // 背景颜色

    private Texture whitePixel;

    public WhiteProgressBar() {
        setWidth(outerBorderWidth);
        setHeight(outerBorderHeight);
        whitePixel = APManager.textureLoad("white_pixel.png");
    }

    // 设置进度条值
    public void setValue(float value) {
        this.value = Math.max(minValue, Math.min(value, maxValue));
    }

    // 设置进度条最小值
    public void setMinValue(float minValue) {
        this.minValue = minValue;
    }

    // 设置进度条最大值
    public void setMaxValue(float maxValue) {
        this.maxValue = maxValue;
    }

    // 获取进度条当前值
    public float getValue() {
        return value;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);

        // 边界外部矩形
        batch.setColor(borderColor);
        batch.draw(whitePixel, getX(), getY(), barWidth, barHeight);

        // 边界内部矩形
        batch.setColor(backgroundColor);
        batch.draw(whitePixel, getX() + deltaIO, getY() + deltaIO, innerBorderWidth, innerBorderHeight);

        // 进度条矩形
        batch.setColor(fillColor);
        batch.draw(whitePixel, getX() + deltaIO + deltaBI, getY() + deltaIO + deltaBI, barWidth * value / (maxValue - minValue), barHeight);
    }
}
