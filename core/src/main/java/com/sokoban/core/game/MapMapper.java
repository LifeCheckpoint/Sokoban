package com.sokoban.core.game;

// import com.sokoban.core.Logger;

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
            case ObjectType.GroundDarkGray -> 40;
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
            case 20 -> ObjectType.Box;
            case 30 -> ObjectType.BoxTarget;
            case 31 -> ObjectType.PlayerTarget;
            case 40 -> ObjectType.GroundDarkGray;
            default -> ObjectType.Unknown;
        };
    }

    // TODO 需要重新设计
    //
    // /**
    //  * 映射字符到地图元素，与传统地图格式兼容
    //  * @param character 字符
    //  * @return 地图元素
    //  */
    // public static ObjectType MapCharToType(char character) {
    //     return switch (character) {
    //         case '_', '-' -> ObjectType.AIR;
    //         case '#' -> ObjectType.WALL;
    //         case '.' -> ObjectType.BoxTarget;
    //         case '$' -> ObjectType.BOX;
    //         case '@' -> ObjectType.PLAYER;
    //         case '*' -> ObjectType.BoxGetBoxpos;
    //         case '+' -> ObjectType.PlayerGetBoxpos;
    //         default -> ObjectType.Unknown;
    //     };
    // }
    //
    // /**
    //  * 映射地图元素到字符，与 传统地图格式兼容
    //  * @param obj 地图元素
    //  * @return 对应字符
    //  */
    // public static char mapObjectToChar(ObjectType obj) {
    //     return switch (obj) {
    //         case ObjectType.AIR -> '_';
    //         case ObjectType.WALL -> '#';
    //         case ObjectType.BoxTarget -> '.';
    //         case ObjectType.BoxGetBoxpos -> '*';
    //         case ObjectType.BOX -> '$';
    //         case ObjectType.PLAYER -> '@';
    //         case ObjectType.PlayerGetBoxpos -> '+';
    //         default -> '?';
    //     };
    // }
    //
    // /**
    //  * 将字符地图转换为整数地图，与传统地图格式兼容
    //  * @param charMap 字符纯地图
    //  * @return 整数纯地图，失败返回 null
    //  */
    // public static int[][] transformCharMapToIntegerMap(char[][] charMap) {
    //     if (charMap == null) {
    //         Logger.error("MapMapper", "Cannot convert a null char map object");
    //         return null;
    //     }
    //
    //     int[][] intMap = new int[charMap.length][charMap[0].length];
    //
    //     for (int i = 0; i < intMap.length; i++) {
    //         for (int j = 0; j < intMap[0].length; j++) {
    //             intMap[i][j] = mapObjectTypeToNum(MapCharToType(charMap[i][j]));
    //         }
    //     }
    //
    //     return intMap;
    // }
    //
    // /**
    //  * 将整数地图转换为字符地图，与传统地图格式兼容
    //  * @param intMap 整数纯地图
    //  * @return 字符纯地图，失败返回 null
    //  */
    // public static char[][] transformIntegerMapToCharMap(int[][] intMap) {
    //     if (intMap == null) {
    //         Logger.error("MapMapper", "Cannot convert a null char map object");
    //         return null;
    //     }

    //     char[][] charMap = new char[intMap.length][intMap[0].length];

    //     for (int i = 0; i < charMap.length; i++) {
    //         for (int j = 0; j < charMap[0].length; j++) {
    //             charMap[i][j] = mapObjectToChar(MapNumToType(intMap[i][j]));
    //         }
    //     }

    //     return charMap;
    // }
}
