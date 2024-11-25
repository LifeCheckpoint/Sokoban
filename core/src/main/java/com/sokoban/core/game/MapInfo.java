package com.sokoban.core.game;

/**
 * 地图信息类
 * <br><br>
 * 地图相关信息
 */
public class MapInfo {
    public String path;
    public String levelName;
    public String mapName;

    public MapInfo(String path, String levelName, String mapName) {
        this.path = path;
        this.levelName = levelName;
        this.mapName = mapName;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getLevelName() {
        return levelName;
    }

    public void setLevelName(String levelName) {
        this.levelName = levelName;
    }

    public String getMapName() {
        return mapName;
    }

    public void setMapName(String mapName) {
        this.mapName = mapName;
    }
}