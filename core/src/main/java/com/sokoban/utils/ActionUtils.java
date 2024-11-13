package com.sokoban.utils;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.sokoban.polygon.action.FadeToAction;

/**
 * 动效实用工具
 * @author Life_Checkpoint
 */
public class ActionUtils {
    /**
     * 随机时间淡入
     * @param actor actor 对象
     */
    public static void FadeInEffectRand(Actor actor) {
        FadeInEffectRand(actor, 1f);
    }

    /**
     * 随机时间淡入
     * @param actor actor 对象
     * @param targetAlpha 目标透明度
     */
    public static void FadeInEffectRand(Actor actor, float targetAlpha) {
        float delayTime = MathUtils.random(0f, 0.3f);
        actor.getColor().a = 0f;
        actor.addAction(Actions.sequence(Actions.delay(delayTime), new FadeToAction(0f, targetAlpha, 0.5f, Interpolation.sine))); // 添加淡入动作
    }

    /**
     * 随机时间淡出
     * @param actor actor 对象
     */
    public static void FadeOutEffectRand(Actor actor) {
        float delayTime = MathUtils.random(0f, 0.3f);
        actor.getColor().a = 1f;
        actor.addAction(Actions.sequence(Actions.delay(delayTime), Actions.fadeOut(0.5f, Interpolation.sine))); // 添加淡入动作
    }
}
