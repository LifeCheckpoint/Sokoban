package com.sokoban.polygon.action;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Action;

public class FadeToAction extends Action {
    private float targetAlpha;
    private float duration;
    private float startAlpha;
    private float elapsedTime = 0f;
    private Interpolation interpolation;

    public FadeToAction(float startAlpha, float targetAlpha, float duration, Interpolation interpolation) {
        this.startAlpha = startAlpha;
        this.targetAlpha = targetAlpha;
        this.duration = duration;
        this.interpolation = interpolation;
    }

    @Override
    public boolean act(float delta) {
        elapsedTime += delta;
        float alpha = interpolation.apply(elapsedTime / duration) * (targetAlpha - startAlpha) + startAlpha;

        actor.getColor().a = alpha;

        // 动画完成后返回 true
        if (elapsedTime >= duration) {
            actor.getColor().a = targetAlpha; // 确保最终透明度设置正确
            return true; // 动作完成
        }

        return false;
    }
}
