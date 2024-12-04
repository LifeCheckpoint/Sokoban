package com.sokoban.core.logic;

public enum ObjectType {
    // 物体层
    Air, // 空气
    Wall, // 墙
    Player, // 玩家
    Box, // 箱子

    // 目标点层
    BoxTarget, // 箱子目标位置
    PlayerTarget, // 玩家目标位置
    
    // 装饰层
    GroundDarkGray, // 深灰色地板

    // 其它
    Unknown // 未知
}
