package com.sokoban.core.game;

public enum ObjectType {
    AIR, // 空气
    WALL, // 墙
    PLAYER, // 玩家
    PlayerGetBoxpos, // 处于箱子目标位置的玩家 
    PlayerGetPLpos, // 处于玩家目标位置的玩家
    BOX, // 箱子
    BoxGetPLpos, // 处于玩家目标位置的箱子
    BoxGetBoxpos, // 处于箱子目标位置的箱子
    PosOfBox, // 箱子目标位置
    PosOfPL, // 玩家目标位置
    Unknown // 未知
}
