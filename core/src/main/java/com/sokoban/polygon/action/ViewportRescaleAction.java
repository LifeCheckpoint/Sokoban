package com.sokoban.polygon.action;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * 视角整体缩放动画
 * @author Life_Checkpoint
 */
public class ViewportRescaleAction extends Action {
    private Viewport viewport;
    private float rescaleRatioRelative;
    private float duration;
    private float timeElapsed = 0;
    private float initialWidth;
    private float initialHeight;

    /**
     * 视角整体缩放动画
     * @param viewport 视口
     * @param rescaleRatio 视口相对于当前大小的缩放率，1.0f 不变
     * @param duration 持续时间
     */
    public ViewportRescaleAction(Viewport viewport, float rescaleRatio, float duration) {
        this.viewport = viewport;
        this.rescaleRatioRelative = rescaleRatio - 1;
        this.duration = duration;
        this.initialWidth = viewport.getWorldWidth();
        this.initialHeight = viewport.getWorldHeight();
    }

    @Override
    public boolean act(float delta) {
        timeElapsed += delta;
        float timestamp = Math.min(timeElapsed / duration, 1f);
        float nowRatio = Interpolation.pow3In.apply(timestamp) * rescaleRatioRelative;

        // 更新视口大小与相机
        viewport.setWorldSize(initialWidth * (nowRatio + 1), initialHeight * (nowRatio + 1));
        viewport.apply();

        // 动画结束
        return timestamp >= 1f;
    }

}
