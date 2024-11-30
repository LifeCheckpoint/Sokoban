package com.sokoban.core.game;

/**
 * 地图信息类
 * <br><br>
 * 地图相关信息
 * @author Life_Checkpoint
 */
public class MapInfo {
    public String path = "";
    public String levelName = "";
    public String mapName = "";

    public MapInfo() {}

    /**
     * 地图文件信息类构造
     * @param path 文件路径，未知为空字符串
     * @param levelName 大关卡名，未知为空字符串
     * @param mapName 小关卡（地图）名，未知为空字符串
     */
    public MapInfo(String path, String levelName, String mapName) {
        this.path = path;
        this.levelName = levelName;
        this.mapName = mapName;
    }
}