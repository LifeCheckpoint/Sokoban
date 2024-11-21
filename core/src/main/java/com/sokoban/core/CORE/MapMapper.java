package com.sokoban.core.CORE;

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
}
