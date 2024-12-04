package com.sokoban.scenes.manager;

import com.sokoban.core.logic.ObjectType;
import com.sokoban.polygon.BoxObject.BoxType;

/**
 * 转换实际显示工具类
 * @author Life_Checkpoint
 */
public class ActorMapper {

    /**
     * 获得 ObjectType 地图数据的转换类型
     * @param obj ObjectType 物体数据
     * @return BoxType
     */
    public static BoxType mapObjectTypeToActor(ObjectType obj) {
        return switch (obj) {
            case ObjectType.Wall -> BoxType.BlueChest;
            case ObjectType.Player -> BoxType.Player;
            case ObjectType.Box -> BoxType.GreenChest;
            case ObjectType.BoxTarget -> BoxType.BoxTarget;
            case ObjectType.PlayerTarget -> BoxType.PlayerTarget;
            case ObjectType.GroundDarkGray -> BoxType.DarkGrayBack;
            default -> null;
        };
    }
}
