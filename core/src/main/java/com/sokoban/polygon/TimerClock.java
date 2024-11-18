package com.sokoban.polygon;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.sokoban.Main;
import com.sokoban.manager.APManager.SpineAssets;
import com.sokoban.polygon.actioninterface.ClockEndCallback;

/**
 * 一个小计时器
 * @author Life_Checkpoint
 */
public class TimerClock extends SpineObject {
    private boolean timerStop = false;

    private final String TIMER_ANIMATION = "animation";
    private final float DEFAULT_SIZE = 0.5f;
    private final float FADEOUT_DURATION = 0.3f;

    /**
     * 计时器小组件构造
     * @param gameMain 全局句柄
     * @param followingObject 跟随物体
     * @param timeDuration 计时时间
     */
    public TimerClock(Main gameMain, Actor followingObject, float timeDuration) {
        super(gameMain, SpineAssets.Timer);
        init(followingObject, timeDuration, null, false);
    }

    /**
     * 计时器小组件构造
     * @param gameMain 全局句柄
     * @param followingObject 跟随物体
     * @param timeDuration 计时时间
     * @param callback 结束后要执行的回调函数
     * @param blocking 是否等待渐隐结束后再进行回调
     */
    public TimerClock(Main gameMain, Actor followingObject, float timeDuration, ClockEndCallback callback, boolean blocking) {
        super(gameMain, SpineAssets.Timer);
        init(followingObject, timeDuration, callback, blocking);
    }

    private void init(Actor followingObject, float timeDuration, ClockEndCallback callback, boolean blocking) {
        setSize(DEFAULT_SIZE, DEFAULT_SIZE);
        setPosition(followingObject.getX() + followingObject.getWidth() / 2, followingObject.getY() + followingObject.getHeight());
        stayAnimationAtFirst(TIMER_ANIMATION); // 虽然很莫名其妙但是能跑起来的代码就不要去碰它了
        // setAnimation(0, TIMER_ANIMATION, false);
        setAnimationTotalTime(0, timeDuration);
        addAction(Actions.sequence(
            Actions.delay(timeDuration),
            callback == null ? Actions.run(this::fadeOut) : Actions.run(() -> fadeOut(callback, blocking))
        ));
    }

    /**
     * 渐隐小组件，不执行操作
     */
    public void fadeOut() {
        timerStop = true;
        addAction(Actions.sequence(
            Actions.fadeOut(FADEOUT_DURATION, Interpolation.sine)
        ));
    }

    /**
     * 渐隐小组件并执行 Lambda
     * @param callback 结束后要执行的回调函数
     * @param blocking 是否等待渐隐结束后再进行回调
     */
    public void fadeOut(ClockEndCallback callback, boolean blocking) {
        timerStop = true;
        if (blocking) {
            addAction(Actions.sequence(
                Actions.fadeOut(FADEOUT_DURATION, Interpolation.sine),
                Actions.run(callback::clockEnd),
                Actions.run(this::remove)
            ));
        } else {
            addAction(Actions.sequence(
                Actions.parallel(
                    Actions.fadeOut(FADEOUT_DURATION, Interpolation.sine),
                    Actions.run(callback::clockEnd)
                ),
                Actions.run(this::remove)
            ));
        }
    }

    /**
     * 取消目前回调后渐隐
     */
    public void cancel() {
        timerStop = true;
        clearActions();
        fadeOut();
    }

    public boolean isTimerStop() {
        return timerStop;
    }
}
