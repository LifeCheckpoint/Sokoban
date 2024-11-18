package com.sokoban.polygon;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
// import com.sokoban.enums.ParticleEnums;
import com.sokoban.utils.MathUtilsEx;

// 继承自 TextureSquare 的粒子类
public class BackgroundParticle extends TextureSquare {
    private float startX, startY;
    private float p1X, p1Y;
    private float endX, endY;

    private float lifetime;
    private float fadeInDuration;
    private float fadeOutDuration;
    
    private float age = 0f;
    private float originAlpha = 1f;

    public enum ParticleStatue {
        In, Live, Out, Dead;
    }
    
    public BackgroundParticle(float startX, float startY, Texture texture) {
        super(texture);
        float particleSize = MathUtils.random(0.05f, 0.15f);
        init(startX, startY, texture, particleSize, (particleSize - 0.05f) / (0.15f - 0.05f));
    }

    public BackgroundParticle(float startX, float startY, Texture texture, float minParticleSize, float maxParticleSize) {
        super(texture);
        float particleSize = MathUtils.random(minParticleSize, maxParticleSize);
        init(startX, startY, texture, particleSize, (particleSize - minParticleSize) / (maxParticleSize - minParticleSize));
    }

    /**
     * 初始化粒子
     * @param startX 粒子出发 X 坐标
     * @param startY 粒子出发 Y 坐标
     * @param texture 粒子贴图素材
     * @param particleSize 粒子大小
     * @param sizeRatio 粒子相对大小，用于计算位移影响
     */
    private void init(float startX, float startY, Texture texture, float particleSize, float sizeRatio) {
        setSize(particleSize);

        // 优化速度分配曲线
        sizeRatio = Interpolation.pow3In.apply(sizeRatio);
        
        setTrace(
            startX, 
            startY, 
            MathUtils.random(-1f * (1 - sizeRatio), 1f * (1 - sizeRatio)),
            MathUtils.random(-1f * (1 - sizeRatio), 1f * (1 - sizeRatio)),
            MathUtils.random(-4f * (1 - sizeRatio), 4f * (1 - sizeRatio)),
            MathUtils.random(-4f * (1 - sizeRatio), 4f * (1 - sizeRatio))
        );
        
        setLifePeriod(
            0.5f,
            MathUtils.random(5f, 8f),
            1f
        );

        this.originAlpha = MathUtils.random(0.2f, 1f);
        this.setPosition(startX, startY);
    }

    // 设置粒子贝塞尔路径
    public void setTrace(float startX, float startY, float p1X, float p1Y, float endDX, float endDY) {
        this.startX = startX;
        this.startY = startY;

        this.p1X = p1X;
        this.p1Y = p1X;

        this.endX = startX + endDX;
        this.endY = startY + endDY;
    }

    // 设置粒子生命周期
    public void setLifePeriod(float fadeInDuration, float lifetime, float fadeOutDuration) {
        this.fadeInDuration = fadeInDuration;
        this.lifetime = lifetime;
        this.fadeOutDuration = fadeOutDuration;
    }

    // 设置粒子大小
    public void setSize(float size) {
        this.setWidth(size);
        this.setHeight(size);
    }

    // 获取粒子当前生命状态
    public ParticleStatue getParticleLifePeriod() {
        if (age <= fadeInDuration) return ParticleStatue.In;
        if (age <= fadeInDuration + lifetime) return ParticleStatue.Live;
        if (age <= fadeInDuration + lifetime + fadeOutDuration) return ParticleStatue.Out;
        return ParticleStatue.Dead;
    }

    // 获取粒子当前生命状态相对时间
    public float getPeriodDeltaTime() {
        ParticleStatue period = getParticleLifePeriod();
        if (period == ParticleStatue.In) return age;
        if (period == ParticleStatue.Live) return age - fadeInDuration;
        if (period == ParticleStatue.Out) return age - fadeInDuration - lifetime;
        return age - fadeInDuration - lifetime - fadeOutDuration;
    }

    // 粒子渲染逻辑
    @Override
    public void act(float delta) {
        super.act(delta);
        age += delta;
        
        ParticleStatue period = getParticleLifePeriod();
        if (period == ParticleStatue.In) {
            float t = Interpolation.sine.apply(getPeriodDeltaTime() / fadeInDuration);
            this.setAlpha(t * originAlpha);
            return;
        }

        if (period == ParticleStatue.Live) {
            float t = Interpolation.sine.apply(getPeriodDeltaTime() / lifetime);
            float x = MathUtilsEx.bezier(t, startX, startX + p1X, endX);
            float y = MathUtilsEx.bezier(t, startY, startY + p1Y, endY);
            this.setPosition(x, y);
            return;
        }

        if (period == ParticleStatue.Out) {
            float t = Interpolation.sine.apply(getPeriodDeltaTime() / fadeOutDuration);
            this.setAlpha((1 - t) * originAlpha);
            return;
        }

        if (period == ParticleStatue.Dead) {
            remove();
            super.remove();
            clear();
            return;
        }
        
    }

    // Getter & Setter

    public float getStartX() {
        return startX;
    }

    public float getStartY() {
        return startY;
    }

    public float getP1X() {
        return p1X;
    }

    public float getP1Y() {
        return p1Y;
    }

    public float getEndX() {
        return endX;
    }

    public float getEndY() {
        return endY;
    }

    public float getLifetime() {
        return lifetime;
    }

    public float getFadeInDuration() {
        return fadeInDuration;
    }

    public float getFadeOutDuration() {
        return fadeOutDuration;
    }

    public float getAge() {
        return age;
    }

    public float getOriginAlpha() {
        return originAlpha;
    }
}
