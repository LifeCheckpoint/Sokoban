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
    // 注，该类最好重构

    /**
     * 随机时间淡入
     * @param actor actor 对象
     */
    public static void FadeInEffect(Actor actor) {
        FadeInEffect(actor, 1f, MathUtils.random(0f, 0.3f));
    }

    /**
     * 指定时间淡入
     * @param actor actor 对象
     * @param delayTime 延迟时间
     */
    public static void FadeInEffect(Actor actor, float delayTime) {
        FadeInEffect(actor, 1f, delayTime);
    }

    /**
     * 淡入
     * @param actor actor 对象
     * @param targetAlpha 目标透明度
     * @param delayTime 延迟时间
     */
    public static void FadeInEffect(Actor actor, float targetAlpha, float delayTime) {
        actor.setColor(actor.getColor().r, actor.getColor().g, actor.getColor().b, 0f);
        actor.addAction(Actions.sequence(
            Actions.delay(delayTime), 
            new FadeToAction(0f, targetAlpha, 0.5f, Interpolation.sine),
            Actions.run(() -> {actor.setColor(actor.getColor().r, actor.getColor().g, actor.getColor().b, targetAlpha);})
        ));
    }

    /**
     * 随机时间淡出
     * @param actor actor 对象
     */
    public static void FadeOutEffect(Actor actor) {
        float delayTime = MathUtils.random(0f, 0.3f);
        actor.setColor(actor.getColor().r, actor.getColor().g, actor.getColor().b, 1f);
        actor.addAction(Actions.sequence(Actions.delay(delayTime), Actions.fadeOut(0.5f, Interpolation.sine)));
    }

    /**
     * 固定时间后，随机时间淡出
     * @param actor actor 对象
     * @param delayTime 延迟时间
     */
    public static void FadeOutEffect(Actor actor, float delayTime) {
        actor.setColor(actor.getColor().r, actor.getColor().g, actor.getColor().b, 1f);
        actor.addAction(Actions.sequence(Actions.delay(delayTime), Actions.delay(MathUtils.random(0f, 0.3f)), Actions.fadeOut(0.5f, Interpolation.sine)));
    }
}
