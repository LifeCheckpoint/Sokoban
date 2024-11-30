package com.sokoban.core.game;

import java.util.ArrayList;
import java.util.List;

public class SubMapData {
    public int height = 0, width = 0; // 子地图高与宽
    public List<ObjectType[][]> mapLayer = new ArrayList<>();

    public final int LAYER_OBJECT = 0; // 存放地图物体的层索引
    public final int LAYER_TARGET = 1; // 存放地图目标点的层索引

    /**
     * 子地图数据类构造，预留好对应的层
     * @param height 子地图高
     * @param width 子地图宽
     */
    public SubMapData(int height, int width) {
        // 添加 2 层
        mapLayer.add(new ObjectType[height][width]);
        mapLayer.add(new ObjectType[height][width]);
    }

    /**
     * 获得物体层
     * @return 物体层数据
     */
    public ObjectType[][] getObjectLayer() {
        return mapLayer.get(LAYER_OBJECT);
    }

    /**
     * 获得目标点层
     * @return 目标点层数据
     */
    public ObjectType[][] getTargetLayer() {
        return mapLayer.get(LAYER_TARGET);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof SubMapData)) return false;
        SubMapData anotherSubMap = (SubMapData) obj;

        if (anotherSubMap.height != height || anotherSubMap.width != width) return false;
        if (!anotherSubMap.mapLayer.equals(mapLayer)) return false;

        return true;
    }
}
