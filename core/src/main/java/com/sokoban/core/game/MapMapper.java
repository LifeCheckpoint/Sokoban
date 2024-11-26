package com.sokoban.core.game;

public class MapMapper {
    public static ObjectType MapNumToType(int num) {
        if (num == 0)
            return ObjectType.AIR;
        else if (num >= 1 && num <= 9) {
            return ObjectType.WALL;
        } else if (num == 10) {
            return ObjectType.PLAYER;
        } else if (num == 11) {
            return ObjectType.PlayerGetBoxpos;
        } else if (num == 12) {
            return ObjectType.PlayerGetPLpos;
        } else if (num == 20) {
            return ObjectType.BOX;
        } else if (num == 21) {
            return ObjectType.BoxGetPLpos;
        } else if (num == 30) {
            return ObjectType.PosOfBox;
        } else if (num == 31) {
            return ObjectType.PosOfPL;
        } else
            return ObjectType.Unknown;
    }

    /**
     * 映射字符到地图元素，这是为了与传统地图格式兼容
     * @param character 字符
     * @return 地图元素
     */
    public static ObjectType MapCharToType(char character) {
        switch (character) {
            case '_':
            case '-':
                return ObjectType.AIR;
            case '#':
                return ObjectType.WALL;
            case '.':
                return ObjectType.PosOfBox;
            case '$':
                return ObjectType.BOX;
            case '@':
                return ObjectType.PLAYER;
            case '*':
                return ObjectType.PosOfBox; // 应该是已经放好的箱子
            case '+':
                return ObjectType.PlayerGetBoxpos;
            default:
                return ObjectType.Unknown;
        }
    }
}
