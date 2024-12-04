package com.sokoban.core.logic;

import com.sokoban.core.map.SubMapData;

/**
 * 逻辑核心辅助方法
 * <br><br>
 * 写在主方法可能稍显凌乱
 */
public class PlayerCoreUtils {
    /** 判断是否为箱子 */
    public static boolean isBox(ObjectType object) {
        return object == ObjectType.Box;
    }
    
    /** 判断是否为玩家 */
    public static boolean isPlayer(ObjectType object) {
        return object == ObjectType.Player;
    }

    /** 判断是否为墙 */
    public static boolean isWall(ObjectType object) {
        return object == ObjectType.Wall;
    }

    /** 判断指定对象是否是可以被占据的，例如空气 */
    public static boolean isWalkable(ObjectType object) {
        return object == ObjectType.Air;
    }

    /**
     * 给定方向，给出相对位移坐标
     * @return 相对位移坐标，例如 Up -> (0, 1)
     */
    public static Pos getDeltaPos(Direction direction){
        return switch (direction) {
            case Direction.Up -> new Pos(0, 1);
            case Direction.Down -> new Pos(0, -1);
            case Direction.Left -> new Pos(-1, 0);
            case Direction.Right -> new Pos(1, 0);
            default -> new Pos(0, 0);
        };
    }

    /**
     * 给定相对位移坐标，给出方向
     * @return 相对位移坐标，例如 (0, 1) -> UP, 失败返回 Direction.None
     */
    public static Direction getDeltaDirection(Pos deltaPos){
        if (deltaPos.equals(new Pos(0, 1))) return Direction.Up;
        if (deltaPos.equals(new Pos(0, -1))) return Direction.Down;
        if (deltaPos.equals(new Pos(-1, 0))) return Direction.Left;
        if (deltaPos.equals(new Pos(1, 0))) return Direction.Right;
        return Direction.None;
    }

    /** 物块移动，调用该方法获得位移字符串 */
    public static String toObjectMoveString(int subMapIndex, int X, int Y, int toX, int toY) {
        return String.format("%d %d %d %d %d %d", subMapIndex, SubMapData.LAYER_OBJECT, X, Y, toX, toY);
    }
}