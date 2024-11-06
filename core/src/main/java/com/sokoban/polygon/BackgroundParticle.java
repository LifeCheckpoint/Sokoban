package com.sokoban.polygon;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.sokoban.MathUtilsEx;

public class BackgroundParticle extends Actor {
    private final int color = 0x88888888;
    private float startX, startY;
    private float endX, endY;
    private float lifetime;
    private float age;

    public BackgroundParticle(float x, float y) {
        this.startX = x;
        this.startY = y;

        // 设置随机大小、透明度和颜色
        float size = MathUtils.random(0.01f, 0.02f);
        this.setWidth(size);
        this.setHeight(size);

        // 随机生成结束位置
        this.endX = x + MathUtils.random(-0.1f, 0.1f);
        this.endY = y + MathUtils.random(-0.1f, 0.1f);

        // 生命周期
        this.lifetime = MathUtils.random(5f, 8f);
        this.age = 0f;

        this.setPosition(startX, startY);
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        age += delta;

        // 使用贝塞尔曲线移动
        float t = MathUtils.clamp(age / lifetime, 0f, 1f);
        float x = MathUtilsEx.bezier(t, startX, startX + MathUtils.random(-0.05f, 0.05f), endX);
        float y = MathUtilsEx.bezier(t, startY, startY + MathUtils.random(-0.05f, 0.05f), endY);

        this.setPosition(x, y);

        // 在生命周期结束时消失
        if (age >= lifetime) {
            this.addAction(Actions.fadeOut(1f));  // 在1秒内消失
            remove();
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        // 未处理 parentAlpha
        batch.setColor(new Color(color));
        super.draw(batch, parentAlpha);
    }
}
