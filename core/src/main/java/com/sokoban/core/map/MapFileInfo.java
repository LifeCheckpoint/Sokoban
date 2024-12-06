package com.sokoban.core.map;

import com.sokoban.core.map.gamedefault.SokobanLevels;
import com.sokoban.core.map.gamedefault.SokobanMaps;
import com.sokoban.utils.DeepClonable;

/**
 * 地图信息类
 * <br><br>
 * 地图相关信息
 * @author Life_Checkpoint
 */
public class MapFileInfo implements DeepClonable<MapFileInfo> {
    public String path = "";
    public SokobanLevels level;
    public SokobanMaps map;

    public MapFileInfo() {}

    /**
     * 地图文件信息类构造
     * @param path 文件路径，未知为空字符串
     * @param level 大关卡名，未知为空字符串
     * @param map 小关卡（地图）名，未知为空字符串
     */
    public MapFileInfo(String path, SokobanLevels level, SokobanMaps map) {
        this.path = path;
        this.level = level;
        this.map = map;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof MapFileInfo)) return false;
        MapFileInfo anotherMapInfo = (MapFileInfo) obj;

        if (!anotherMapInfo.path.equals(path)) return false;
        if (!anotherMapInfo.level.equals(level)) return false;
        if (!anotherMapInfo.map.equals(map)) return false;

        return true;
    }

    @Override
    public MapFileInfo deepCopy() {
        return new MapFileInfo(path, level, map);
    }
}