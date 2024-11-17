package com.sokoban.polygon.combine;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.sokoban.Main;
import com.sokoban.polygon.BoxObject;
import com.sokoban.polygon.SpineObject;
import com.sokoban.polygon.BoxObject.BoxType;

/**
 * 网格世界类，渲染在网格中的 Spine
 * @author Life_Checkpoint
 */
public class GirdWorld extends SokobanCombineObject {
    /** 网格世界长宽 */
    private int gridWidth, gridHeight;
    private float cellSize;

    /** 
     * 网格世界内容
     * <br><br>
     * 如果内容不存在则被标记为 null，遍历方式为先行后列 (H -> W)
     */
    Actor[][] gridSpineObjects;

    public GirdWorld(Main gameMain, int gridWidth, int gridHeight, float cellSize) {
        super(gameMain);
        init(gridWidth, gridHeight, cellSize);
    }

    private void init(int gridWidth, int gridHeight, float cellSize) {
        this.gridWidth = gridWidth;
        this.gridHeight = gridHeight;
        this.cellSize = cellSize;

        gridSpineObjects = new SpineObject[gridHeight][gridWidth];
        setPosition(8f, 4.5f);
    }

    /**
     * {@inheritDoc}
     * <br><br>
     * 传入坐标为<b>中心</b>坐标
     */
    @Override
    public void setPosition(float x, float y) {
        // 变换为左下角坐标
        this.width = gridWidth * cellSize;
        this.height = gridHeight * cellSize;
        this.x = x - width / 2;
        this.y = y - height / 2;

        for (int i = 0; i < gridHeight; i++) {
            for (int j = 0; j < gridWidth; j++) {
                Vector2 pos = getCellPosition(i, j);
                if (gridSpineObjects[i][j] != null) gridSpineObjects[i][j].setPosition(pos.x, pos.y);
            }
        }
    }

    /**
     * 返回指定格子的左下角世界坐标，左下角为 (0, 0)
     * @param row 行，0 ~ gridHeight
     * @param column 列，0 ~ gridWidth
     * @return Vector2 坐标
     */
    public Vector2 getCellPosition(int row, int column) {
        Vector2 position = new Vector2(getX(), getY());

        // 相对左下角位移
        position.y += row * cellSize;
        position.x += column * cellSize;

        return position;
    }

    /**
     * 添加普通 Actor 组件，自动设置位置
     * @param actor Actor 组件
     * @param row 行，0 ~ gridHeight
     * @param column 列，0 ~ gridWidth
     */
    public void add(Actor actor, int row, int column) {
        Vector2 pos = getCellPosition(row, column);
        actor.setPosition(pos.x, pos.y);
        gridSpineObjects[row][column] = actor;
    }

    /**
     * 添加普通 Actor 组件，自动设置位置
     * @param actor Actor 组件
     * @param location 行 -> 列，0 ~ gridHeight， 0 ~ gridWidth
     */
    public void add(Actor actor, int[][] location) {
        for (int i = 0; i < location.length; i++) {
            Vector2 pos = getCellPosition(location[i][0], location[i][1]);
            gridSpineObjects[location[i][0]][location[i][1]] = actor;
            actor.setPosition(pos.x, pos.y);
        }
    }

    /**
     * 添加箱子类组件
     * @param boxType 箱子类型
     * @param row 行，0 ~ gridHeight
     * @param column 列，0 ~ gridWidth
     */
    public void addBox(BoxType boxType, int row, int column) {
        Vector2 pos = getCellPosition(row, column);
        gridSpineObjects[row][column] = new BoxObject(gameMain, boxType, cellSize, pos.x, pos.y);
    }

    /**
     * 添加箱子类组件
     * @param boxType 箱子类型
     * @param location 行 -> 列，0 ~ gridHeight， 0 ~ gridWidth
     */
    public void addBox(BoxType boxType, int[][] location) {
        for (int i = 0; i < location.length; i++) {
            Vector2 pos = getCellPosition(location[i][0], location[i][1]);
            gridSpineObjects[location[i][0]][location[i][1]] = new BoxObject(gameMain, boxType, cellSize, pos.x, pos.y);
        }
    }

    /**
     * 移除指定坐标组件
     * @param row 行，0 ~ gridHeight
     * @param column 列，0 ~ gridWidth
     */
    public void remove(int row, int column) {
        gridSpineObjects[row][column].clearActions();
        gridSpineObjects[row][column] = null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Actor> getAllActors() {
        List<Actor> actors = new ArrayList<>();
        for (int i = 0; i < gridHeight; i++) {
            for (int j = 0; j < gridWidth; j++) {
                if (gridSpineObjects[i][j] != null) actors.add(gridSpineObjects[i][j]);
            }
        }
        return actors;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addActorsToStage(Stage stage) {
        getAllActors().forEach(stage::addActor);
    }
}
