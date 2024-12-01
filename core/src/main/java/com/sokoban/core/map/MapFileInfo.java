package com.sokoban.core.map;

/**
 * 地图信息类
 * <br><br>
 * 地图相关信息
 * @author Life_Checkpoint
 */
public class MapFileInfo {
    public String path = "";
    public String levelName = "";
    public String mapName = "";

    public MapFileInfo() {}

    /**
     * 地图文件信息类构造
     * @param path 文件路径，未知为空字符串
     * @param levelName 大关卡名，未知为空字符串
     * @param mapName 小关卡（地图）名，未知为空字符串
     */
    public MapFileInfo(String path, String levelName, String mapName) {
        this.path = path;
        this.levelName = levelName;
        this.mapName = mapName;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof MapFileInfo)) return false;
        MapFileInfo anotherMapInfo = (MapFileInfo) obj;

        if (!anotherMapInfo.path.equals(path)) return false;
        if (!anotherMapInfo.levelName.equals(levelName)) return false;
        if (!anotherMapInfo.mapName.equals(mapName)) return false;

        return true;
    }
}