package com.sokoban.core.logic;

public enum ObjectType {
    /**
     * 默认物体，这些物体的形态是固定的
     */

    // 物体层
    Air, // 空气
    Wall, // 墙
    Player, // 玩家
    Box, // 箱子

    // 目标点层
    BoxTarget, // 箱子目标位置
    PlayerTarget, // 玩家目标位置
    
    // 装饰层
    Ground, // 深灰色地板

    // 其它
    Unknown, // 未知

    /**
     * 非默认物体，有不同的外貌但实质相同
     */

    BoxGreen,
    BoxBlue,

    GroundDarkGray,
    GroundDarkBlue
}
