package com.sokoban.core.logic;

import com.sokoban.core.map.SubMapData;

/**
 * 将不同形态的物件映射到统一类别，便于逻辑管理
 * <br><br>
 * 设计的时候保持为 ObjectType 不变
 */
public class ObjectClassMapper {
    public static ObjectType typeClass(ObjectType object) {
        if (object == ObjectType.BoxGreen) return ObjectType.Box;
        if (object == ObjectType.BoxBlue) return ObjectType.Box;
        if (object == ObjectType.GroundDarkGray) return ObjectType.Ground;
        if (object == ObjectType.GroundDarkBlue) return ObjectType.Ground;
        return object;
    }

    /**
     * 获得 ObjectType 地图数据的物件类型
     * @param obj ObjectType 物体数据
     * @return 对应物体层
     */
    public static int mapObjectTypeToLayerIndex(ObjectType obj) {
        return switch (typeClass(obj)) {
            case ObjectType.Wall, ObjectType.Player, ObjectType.Box -> SubMapData.LAYER_OBJECT;
            case ObjectType.BoxTarget, ObjectType.PlayerTarget -> SubMapData.LAYER_TARGET;
            case ObjectType.Ground -> SubMapData.LAYER_DECORATION;
            default -> 0;
        };
    }
}
