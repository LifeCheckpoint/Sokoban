package com.sokoban.polygon.combine;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.sokoban.Main;
import com.sokoban.core.Logger;

/**
 * 多层堆叠的 GirdWorld，用于多层对象显示
 * <br><br>
 * 整体看上去还是一层
 * @author Life_Checkpoint
 */
public class Stack2DGirdWorld extends SokobanCombineObject {
    /** 网格世界长宽 */
    private int gridWidth, gridHeight;
    private float cellSize;

    /** 
     * 堆叠网格世界内容
     * <br><br>
     * 遍历方式为层（下到上）、行、列 (L -> H -> W)
     */
    List<GirdWorld> stack2DGridWorld;

    public Stack2DGirdWorld(Main gameMain, int gridWidth, int gridHeight, float cellSize) {
        super(gameMain);
        init(gridWidth, gridHeight, cellSize);
    }

    private void init(int gridWidth, int gridHeight, float cellSize) {
        this.gridWidth = gridWidth;
        this.gridHeight = gridHeight;
        this.cellSize = cellSize;

        stack2DGridWorld = new ArrayList<>();
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

        for (GirdWorld gridLayer : stack2DGridWorld) gridLayer.setPosition(x, y);
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
     * 添加新层
     */
    public void addLayer() {
        stack2DGridWorld.add(new GirdWorld(gameMain, gridWidth, gridHeight, cellSize));
        getTopLayer().setPosition(x, y);
    }

    /**
     * 添加已有层
     * @param girdWorldLayer 网格小世界
     */
    public void addLayer(GirdWorld girdWorldLayer) {
        stack2DGridWorld.add(girdWorldLayer);
        getTopLayer().setPosition(x, y);
    }

    /**
     * 获取对应层的 GridWorld 对象
     * @param layer 层数，0 为最底层
     * @return GridWorld 对象
     */
    public GirdWorld getLayer(int layer) {
        if (layer < 0 || layer >= stack2DGridWorld.size()) {
            Logger.error("Stack2DGridWorld", String.format("%d is not a valid layer, Expect (0, %d)", layer, stack2DGridWorld.size()));
            return null;
        }

        return stack2DGridWorld.get(layer);
    }

    /**
     * 获得顶层 GridWorld
     * @return 顶层 GridWorld
     */
    public GirdWorld getTopLayer() {
        return getLayer(stack2DGridWorld.size() - 1);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Actor> getAllActors() {
        List<Actor> actors = new ArrayList<>();

        // 按照堆叠顺序
        for (GirdWorld gridLayer : stack2DGridWorld) actors.addAll(gridLayer.getAllActors());
        return actors;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addActorsToStage(Stage stage) {
        getAllActors().forEach(stage::addActor);
    }

    public List<GirdWorld> getStack2DGridWorld() {
        return stack2DGridWorld;
    }

    public void setStack2DGridWorld(List<GirdWorld> stack2DGridWorld) {
        this.stack2DGridWorld = stack2DGridWorld;
    }

}
