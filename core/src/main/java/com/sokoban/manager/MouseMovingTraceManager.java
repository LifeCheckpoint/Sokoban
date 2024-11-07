package com.sokoban.manager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.Viewport;

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

    public Viewport getViewPort() {
        return viewport;
    }

    // 设置相机位置并更新
    public void setPositionWithUpdate() {
        setPositionWithUpdate(Gdx.input.getX(), Gdx.input.getY());
    }

    public void setPositionWithUpdate(int ScreenCoordinateX, int ScreenCoordinateY) {
        // 计算鼠标位置世界坐标以及偏移矢量
        mousePos.set(ScreenCoordinateX, ScreenCoordinateY);
        viewport.unproject(mousePos);
        mouse2CenterOffsetScaled = mousePos.cpy().sub(screenCenter).scl(screenMoveScaling);

        // 防止移出
        if (mouse2CenterOffsetScaled.len() > maxScreenOffset) mouse2CenterOffsetScaled.setLength(maxScreenOffset);
        
        // 更新相机位置
        viewport.getCamera().position.set(mouse2CenterOffsetScaled.add(screenCenter), 0);
        viewport.getCamera().update();
    }

    public float getMaxScreenOffset() {
        return maxScreenOffset;
    }

    public float getScreenMoveScaling() {
        return screenMoveScaling;
    }
}
