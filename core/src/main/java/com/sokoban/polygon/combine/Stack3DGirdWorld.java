package com.sokoban.polygon.combine;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.sokoban.Main;
import com.sokoban.core.Logger;
import com.sokoban.polygon.actioninterface.Stack3DUpdateIn;
import com.sokoban.polygon.actioninterface.Stack3DUpdateOut;

/**
 * 多层堆叠的 GirdWorld，用于多层对象显示
 * <br><br>
 * 该类将会逐地图层显示
 * @author Life_Checkpoint
 */
public class Stack3DGirdWorld extends SokobanCombineObject {
    private int gridWidth, gridHeight;
    private float cellSize;

    /**
     * 当前展示层
     */
    private int showLayer = -1;

    /** 
     * 三维网格世界内容
     * <br><br>
     * 遍历方式为地图层（下到上）、层（下到上）、行、列 (ML -> L -> H -> W)
     */
    List<Stack2DGirdWorld> stack3DGridWorld;

    public Stack3DGirdWorld(Main gameMain, int gridWidth, int gridHeight, float cellSize) {
        super(gameMain);
        init(gridWidth, gridHeight, cellSize);
    }

    private void init(int gridWidth, int gridHeight, float cellSize) {
        this.gridWidth = gridWidth;
        this.gridHeight = gridHeight;
        this.cellSize = cellSize;

        stack3DGridWorld = new ArrayList<>();
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

        for (Stack2DGirdWorld stack2DLayer : stack3DGridWorld) stack2DLayer.setPosition(x, y);
    }

    /**
     * 添加新堆叠 2D 层
     */
    public void addStack2DLayer() {
        stack3DGridWorld.add(new Stack2DGirdWorld(gameMain, gridWidth, gridHeight, cellSize));
        // 中心坐标
        getTopLayer().setPosition(x + width / 2, y + height / 2);
    }

    /**
     * 添加已有堆叠 2D 层
     * @param stack2DLayer 堆叠 2D 层
     */
    public void addStack2DLayer(Stack2DGirdWorld stack2DLayer) {
        stack3DGridWorld.add(stack2DLayer);
        getTopLayer().setPosition(x, y);
    }

    /**
     * 获取对应层的 堆叠 2D 层 对象
     * @param layer 层数，0 为最底层
     * @return GridWorld 对象
     */
    public Stack2DGirdWorld getStack2DLayer(int layer) {
        if (layer < 0 || layer >= stack3DGridWorld.size()) {
            Logger.error("Stack3DGridWorld", String.format("%d is not a valid layer, Expect (0, %d)", layer, stack3DGridWorld.size()));
            return null;
        }

        return stack3DGridWorld.get(layer);
    }

    /**
     * 获得当前展示的层索引
     * @return 当前层索引
     */ 
    public int getShowLayer() {
        return showLayer;
    }

    /**
     * 设置当前展示层索引
     * @param showLayer 新层索引，如果希望仅隐藏请设置为 -1
     * @param updateInCallback 物体切入时的回调
     * @param updateOutCallback 物体切出时的回调
     */
    public void setShowLayer(int showLayer, Stack3DUpdateIn updateInCallback, Stack3DUpdateOut updateOutCallback) {
        if ((showLayer < 0 || showLayer >= stack3DGridWorld.size()) && showLayer != -1) {
            Logger.error("Stack3DGridWorld", String.format("%d is not a valid layer, Expect [0, %d] or -1", showLayer, stack3DGridWorld.size()));
            return;
        }

        if (showLayer != -1) updateOutCallback.updateOut(getStack2DLayer(showLayer)); // 切出回调
        this.showLayer = showLayer;
        if (showLayer != -1) updateInCallback.updateIn(getStack2DLayer(showLayer)); // 切入回调
    }

    /**
     * 获得顶层 GridWorld
     * @return 顶层 GridWorld
     */
    public Stack2DGirdWorld getTopLayer() {
        return getStack2DLayer(stack3DGridWorld.size() - 1);
    }

    /**
     * 请勿使用这个方法为 Stage 添加元素
     * <br><br>
     * 使用 <b>setShowLayer</b> 为 Actor 执行回调
     * {@inheritDoc}
     */
    @Override
    public List<Actor> getAllActors() {
        List<Actor> actors = new ArrayList<>();

        // 按照堆叠顺序
        for (Stack2DGirdWorld Stack2DLayer : stack3DGridWorld) actors.addAll(Stack2DLayer.getAllActors());
        return actors;
    }

    /**
     * 请勿使用这个方法为 Stage 添加元素
     * <br><br>
     * 使用 <b>setShowLayer</b> 为 Actor 执行回调
     * {@inheritDoc}
     */
    @Override
    public void addActorsToStage(Stage stage) {
        getAllActors().forEach(stage::addActor);
    }

    public List<Stack2DGirdWorld> getStack3DGridWorld() {
        return stack3DGridWorld;
    }

    public void setStackGridWorld(List<Stack2DGirdWorld> stack3DGridWorld) {
        this.stack3DGridWorld = stack3DGridWorld;
    }

}
