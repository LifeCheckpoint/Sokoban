package com.sokoban.polygon;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.sokoban.MathUtilsEx;

public class BackgroundParticle extends TextureSquare {
    private final int color = 0x88888888;
    private float startX, startY;
    private float p1X, p1Y;
    private float endX, endY;
    private float lifetime;
    private float age;
    private float fadeOutDuration;
    private float originAlpha;

    public BackgroundParticle(float x, float y, Texture texture) {
        super(texture);

        fadeOutDuration = 1f;
        originAlpha = 1f;

        this.startX = x;
        this.startY = y;

        float size = MathUtils.random(0.1f, 0.2f);
        this.setWidth(size);
        this.setHeight(size);

        this.p1X = MathUtils.random(-0.5f, 0.5f);
        this.p1Y = MathUtils.random(-0.5f, 0.5f);

        // 随机生成结束位置
        this.endX = x + MathUtils.random(-3f, 3f);
        this.endY = y + MathUtils.random(-3f, 3f);

        // 生命周期
        this.lifetime = MathUtils.random(5f, 8f);
        this.age = 0f;

        this.setPosition(startX, startY);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        age += delta;

        // 非死亡状态
        if (age <= lifetime) {
            // 使用贝塞尔曲线移动
            float t = Interpolation.sine.apply(age / lifetime);
            float x = MathUtilsEx.bezier(t, startX, startX + p1X, endX);
            float y = MathUtilsEx.bezier(t, startY, startY + p1Y, endY);

            this.setPosition(x, y);
            return;
        }

        // 生命周期结束（渐出）
        if (age > lifetime && age < lifetime + fadeOutDuration) {
            float t = Interpolation.sine.apply((age - lifetime) / fadeOutDuration);
            this.setAlpha((1 - t) * originAlpha);
            return;
        }

        // 生命周期结束（销毁）
        if (age > lifetime + fadeOutDuration) {
            remove();
            return;
        }
        
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        // 未处理 parentAlpha
        batch.setColor(new Color(color));
        super.draw(batch, parentAlpha);
    }
}
