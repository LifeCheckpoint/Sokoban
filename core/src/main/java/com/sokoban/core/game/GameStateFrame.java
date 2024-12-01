package com.sokoban.core.game;

import java.time.LocalDateTime;

import com.sokoban.core.map.MapFileInfo;

/**
 * 游戏状态帧
 * <br><br>
 * 用于记录步数与历史记录等
 */
public class GameStateFrame {
    public MapFileInfo mapInfo;
    public int stepCount;
    public LocalDateTime frameTime; 
    public Direction action;

    public GameStateFrame() {
        this.mapInfo = null;
        this.stepCount = 0;
        this.frameTime = LocalDateTime.now();
        this.action = Direction.None;
    }

    public GameStateFrame(MapFileInfo mapInfo) {
        this.mapInfo = mapInfo;
        this.stepCount = 0;
        this.frameTime = LocalDateTime.now();
        this.action = Direction.None;
    }
}
