package com.sokoban.core.game;

import java.util.ArrayList;
import java.util.List;

public class SubMapData {
    public int height = 0, width = 0; // 子地图高与宽
    public List<ObjectType[][]> mapLayer = new ArrayList<>();

    public static final int LAYER_OBJECT = 0; // 存放地图物体的层索引
    public static final int LAYER_TARGET = 1; // 存放地图目标点的层索引
    public static final int LAYER_DECORATION = 2; // 存放地图装饰的层索引

    /**
     * 子地图数据类构造，预留好对应的层
     * @param height 子地图高
     * @param width 子地图宽
     */
    public SubMapData(int height, int width) {
        // 添加 3 层
        mapLayer.add(new ObjectType[height][width]);
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

    /**
     * 获得装饰层
     * @return 装饰层数据
     */
    public ObjectType[][] getDecorationLayer() {
        return mapLayer.get(LAYER_DECORATION);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof SubMapData)) return false;
        SubMapData anotherSubMap = (SubMapData) obj;

        // 判断宽高是否一致
        if (anotherSubMap.height != height || anotherSubMap.width != width) return false;

        // 判断每一层是否一致
        for (int i = 0; i < mapLayer.size(); i++) {
            if (!java.util.Arrays.deepEquals(mapLayer.get(i), anotherSubMap.mapLayer.get(i))) return false;
        }

        return true;
    }

    /**
     * 复制当前对象
     * @return 新对象，与原对象不是同一个引用
     */
    public SubMapData cpy() {
        SubMapData newMap = new SubMapData(height, width);
        
        // 对于每一个层
        for (int layerIndex = 0; layerIndex < newMap.mapLayer.size(); layerIndex++) {
            // 对于每一行
            for (int line = 0; line < newMap.mapLayer.get(layerIndex).length; line++) {
                newMap.mapLayer.get(layerIndex)[line] = mapLayer.get(layerIndex)[line].clone();
            }
        }

        return newMap;
    }
}
