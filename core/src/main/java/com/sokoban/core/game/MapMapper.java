package com.sokoban.core.game;

import com.sokoban.core.Logger;

public class MapMapper {
    /**
     * 映射地图元素到整数
     * @param obj 地图元素
     * @return 对应整数
     */
    public static int mapObjectTypeToNum(ObjectType obj) {
        return switch (obj) {
            case ObjectType.AIR -> 0;
            case ObjectType.WALL -> 1;
            case ObjectType.PLAYER -> 10;
            case ObjectType.PlayerGetBoxpos -> 11;
            case ObjectType.PlayerGetPLpos -> 12;
            case ObjectType.BOX -> 20;
            case ObjectType.BoxGetPLpos -> 21;
            case ObjectType.PosOfBox -> 30;
            case ObjectType.PosOfPL -> 31;
            case ObjectType.BoxGetBoxpos -> 32;
            case ObjectType.Unknown -> 100;
        };
    }

    /**
     * 映射整数到地图元素
     * @param num 整数
     * @return 地图元素
     */
    public static ObjectType MapNumToType(int num) {
        return switch (num) {
            case 0 -> ObjectType.AIR;
            case 1, 2, 3, 4, 5, 6, 7, 8, 9 -> ObjectType.WALL;
            case 10 -> ObjectType.PLAYER;
            case 11 -> ObjectType.PlayerGetBoxpos;
            case 12 -> ObjectType.PlayerGetPLpos;
            case 20 -> ObjectType.BOX;
            case 21 -> ObjectType.BoxGetPLpos;
            case 30 -> ObjectType.PosOfBox;
            case 31 -> ObjectType.PosOfPL;
            case 32, 33, 34, 35, 36, 37, 38, 39 -> ObjectType.BoxGetBoxpos;
            default -> ObjectType.Unknown;
        };
    }

    /**
     * 映射字符到地图元素，与传统地图格式兼容
     * @param character 字符
     * @return 地图元素
     */
    public static ObjectType MapCharToType(char character) {
        return switch (character) {
            case '_', '-' -> ObjectType.AIR;
            case '#' -> ObjectType.WALL;
            case '.' -> ObjectType.PosOfBox;
            case '$' -> ObjectType.BOX;
            case '@' -> ObjectType.PLAYER;
            case '*' -> ObjectType.BoxGetBoxpos;
            case '+' -> ObjectType.PlayerGetBoxpos;
            default -> ObjectType.Unknown;
        };
    }

    /**
     * 映射地图元素到字符，与 传统地图格式兼容
     * @param obj 地图元素
     * @return 对应字符
     */
    public static char mapObjectToChar(ObjectType obj) {
        return switch (obj) {
            case ObjectType.AIR -> '_';
            case ObjectType.WALL -> '#';
            case ObjectType.PosOfBox -> '.';
            case ObjectType.BoxGetBoxpos -> '*';
            case ObjectType.BOX -> '$';
            case ObjectType.PLAYER -> '@';
            case ObjectType.PlayerGetBoxpos -> '+';
            default -> '?';
        };
    }

    /**
     * 将字符地图转换为整数地图，与传统地图格式兼容
     * @param charMap 字符纯地图
     * @return 整数纯地图，失败返回 null
     */
    public static int[][] transformCharMapToIntegerMap(char[][] charMap) {
        if (charMap == null) {
            Logger.error("MapMapper", "Cannot convert a null char map object");
            return null;
        }

        int[][] intMap = new int[charMap.length][charMap[0].length];

        for (int i = 0; i < intMap.length; i++) {
            for (int j = 0; j < intMap[0].length; j++) {
                intMap[i][j] = mapObjectTypeToNum(MapCharToType(charMap[i][j]));
            }
        }

        return intMap;
    }

    /**
     * 将整数地图转换为字符地图，与传统地图格式兼容
     * @param intMap 整数纯地图
     * @return 字符纯地图，失败返回 null
     */
    public static char[][] transformIntegerMapToCharMap(int[][] intMap) {
        if (intMap == null) {
            Logger.error("MapMapper", "Cannot convert a null char map object");
            return null;
        }

        char[][] charMap = new char[intMap.length][intMap[0].length];

        for (int i = 0; i < charMap.length; i++) {
            for (int j = 0; j < charMap[0].length; j++) {
                charMap[i][j] = mapObjectToChar(MapNumToType(intMap[i][j]));
            }
        }

        return charMap;
    }
}
