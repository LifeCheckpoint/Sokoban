package com.sokoban.core.map;

import com.sokoban.core.logic.ObjectType;

public class MapMapper {
    /**
     * 映射地图元素到整数
     * @param obj 地图元素
     * @return 对应整数
     */
    public static int mapObjectTypeToNum(ObjectType obj) {
        return switch (obj) {
            case ObjectType.Air -> 0;
            case ObjectType.Wall -> 1;
            case ObjectType.Player -> 10;
            case ObjectType.Box -> 20;
            case ObjectType.BoxTarget -> 30;
            case ObjectType.PlayerTarget -> 31;
            case ObjectType.Ground -> 40;

            case ObjectType.BoxGreen -> 20;
            case ObjectType.BoxBlue -> 21;
            case ObjectType.GroundDarkGray -> 40;
            case ObjectType.GroundDarkBlue -> 41;

            case ObjectType.Unknown -> -1;
        };
    }

    /**
     * 映射整数到地图元素
     * @param num 整数
     * @return 地图元素
     */
    public static ObjectType MapNumToType(int num) {
        return switch (num) {
            case 0 -> ObjectType.Air;
            case 1 -> ObjectType.Wall;
            case 10 -> ObjectType.Player;
            case 20 -> ObjectType.BoxGreen;
            case 21 -> ObjectType.BoxBlue;
            case 30 -> ObjectType.BoxTarget;
            case 31 -> ObjectType.PlayerTarget;
            case 40 -> ObjectType.GroundDarkGray;
            case 41 -> ObjectType.GroundDarkBlue;
            default -> ObjectType.Unknown;
        };
    }

    
    /**
     * 映射字符到地图物块，与传统地图格式兼容
     * @param character 字符
     * @return 地图元素，如果字符转换后为非物块类型将返回 Air
     */
    public static ObjectType MapCharToObjectType(char character) {
        return switch (character) {
            case '_', '-' -> ObjectType.Air;
            case '#' -> ObjectType.Wall;
            case '$', '*' -> ObjectType.Box;
            case '@', '+' -> ObjectType.Player;
            default -> ObjectType.Air;
        };
    }
    
    /**
     * 映射字符到地图目标点，与传统地图格式兼容
     * @param character 字符
     * @return 地图元素，如果字符转换后为非目标点类型将返回 Air
     */
    public static ObjectType MapCharToTargetType(char character) {
        return switch (character) {
            case '.', '*', '+' -> ObjectType.BoxTarget;
            default -> ObjectType.Air;
        };
    }

    /**
     * 将整数地图数组映射到 ObjectType 地图数组
     * @param intMap 整数地图数组
     * @return ObjectType 地图数组
     */
    public static ObjectType[][] mapIntegerMapToObjectTypeMap (int[][] intMap) {
        ObjectType[][] objMap = new ObjectType[intMap.length][intMap[0].length];
        for (int i = 0; i < intMap.length; i++) {
            for (int j = 0; j < intMap[0].length; j++) {
                objMap[i][j] = MapNumToType(intMap[i][j]);
            }
        }
        return objMap;
    }

    /**
     * 将 ObjectType 地图数组映射到整数地图数组
     * @param objMap ObjectType 地图数组
     * @return 整数地图数组
     */
    public static int[][] mapObjectTypeMapToIntegerMap (ObjectType[][] objMap) {
        int[][] intMap = new int[objMap.length][objMap[0].length];
        for (int i = 0; i < objMap.length; i++) {
            for (int j = 0; j < objMap[0].length; j++) {
                intMap[i][j] = mapObjectTypeToNum(objMap[i][j]);
            }
        }
        return intMap;
    }
}