package com.sokoban.polygon.combine;

import java.util.List;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.sokoban.Main;

/**
 * 组合组件抽象父类，规定组件规范
 * @author Life_Checkpoint
 */
public abstract class SokobanCombineObject {
    /**
     * 组件位置及大小
     */
    protected float x, y, width, height;
    protected Main gameMain;

    public SokobanCombineObject(Main gameMain) {
        this.gameMain = gameMain;
    }

    /**
     * 设置组合组件位置，对 x, y, width, height 更新
     * @param x 
     * @param y
     */
    public abstract void setPosition(float x, float y);

    /**
     * 更新组合组件位置
     */
    public void updatePosition() {
        setPosition(x, y);
    }

    /**
     * 将该组件全部添加至 stage
     * @param stage stage 对象
     */
    public abstract void addActorsToStage(Stage stage);

    /**
     * 返回所有组件列表
     * @return 组件列表
     */
    public abstract List<Actor> getAllActors();

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }
}
