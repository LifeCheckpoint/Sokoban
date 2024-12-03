package com.sokoban.core.map.gamedefault;

/**
 * 游戏默认关卡名
 */
public enum SokobanLevels {
    Tutorial("tutorial"),
    Origin("origin"),
    Moving("moving"),
    Random("random");

    private final String levelName;
    SokobanLevels(String levelName) {this.levelName = levelName;}
    public String getLevelName() {return levelName;}
}