package com.sokoban.manager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * 视图鼠标移动追踪
 * @author Claude
 * @author Life_Checkpoint
 */
public class MouseMovingTraceManager {
    private Viewport viewport;
    private final float maxScreenOffset = 1f;
    private final float screenMoveScaling = 0.03f;
    private Vector2 mousePos;
    private Vector2 screenCenter;
    private Vector2 mouse2CenterOffsetScaled;

    public MouseMovingTraceManager(Viewport viewport) {
        setViewPort(viewport);
    }

    public void setViewPort(Viewport viewport) {
        this.viewport = viewport;
        this.mousePos = new Vector2();
        this.mouse2CenterOffsetScaled = new Vector2();
        this.screenCenter = new Vector2(viewport.getWorldWidth() / 2, viewport.getWorldHeight() / 2);
    }

    /** 
     * 设置相机位置并更新
     */
    public void setPositionWithUpdate() {
        // 计算鼠标位置世界坐标以及偏移矢量
        mousePos.set(Gdx.input.getX(), Gdx.input.getY());
        viewport.unproject(mousePos);
        mouse2CenterOffsetScaled = mousePos.cpy().sub(screenCenter).scl(screenMoveScaling);

        // 防止移出
        if (mouse2CenterOffsetScaled.len() > maxScreenOffset) mouse2CenterOffsetScaled.setLength(maxScreenOffset);
        
        // 更新相机位置
        viewport.getCamera().position.set(mouse2CenterOffsetScaled.add(screenCenter), 0);
        viewport.getCamera().update();
    }

    /** 
     * 设置相机位置（含组件）并更新
     * <br><br>
     * 参数均为<b>世界坐标</b>
     */
    public void setPositionWithUpdate(Actor centralActor, float actorOriginalX, float actorOriginalY) {
        Vector2 actorDeltaWorldPos = new Vector2(centralActor.getX() - actorOriginalX, centralActor.getY() - actorOriginalY);

        // 计算鼠标位置世界坐标以及偏移矢量
        mousePos.set(Gdx.input.getX(), Gdx.input.getY());
        viewport.unproject(mousePos);
        mouse2CenterOffsetScaled = mousePos.cpy().sub(screenCenter).scl(screenMoveScaling);
        
        // 更新相机位置
        viewport.getCamera().position.set(mouse2CenterOffsetScaled.add(screenCenter).add(actorDeltaWorldPos), 0);
        viewport.getCamera().update();
    }

    public Viewport getViewPort() {
        return viewport;
    }
    public float getMaxScreenOffset() {
        return maxScreenOffset;
    }
    public float getScreenMoveScaling() {
        return screenMoveScaling;
    }
}
