package com.sokoban.core.map;

import java.util.ArrayList;
import java.util.List;

/**
 * 地图关卡数据类
 * <br><br>
 * 地图数据通过 JsonManager 进行序列化，存储时 mapFileInfo 使用默认占位符
 * @author StiCK-bot
 * @author Life_Checkpoint
 */
public class MapData {
    public MapFileInfo mapFileInfo = new MapFileInfo(); // 地图文件信息
    public String addtionalInfo = ""; // 地图附加信息
    public List<SubMapData> allMaps = new ArrayList<>(); // 所有子地图

    /**
     * 地图数据类无参构造，属性可以稍后填入
     */
    public MapData() {}

    /**
     * 地图数据类构造
     * @param mapFileInfo 地图文件数据
     * @param addtionInfo 地图附加数据文本
     * @param allMaps 所有子地图
     */
    public MapData(MapFileInfo mapFileInfo, String addtionInfo, List<SubMapData> allMaps) {
        this.mapFileInfo = mapFileInfo;
        this.addtionalInfo = addtionInfo;
        this.allMaps = allMaps;
    }

    // public 属性无需使用 getter / setter

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof MapData)) return false;

        MapData anotherMapData = (MapData) obj;

        // 注意，不检查文件路径一致性
        // if (!anotherMapData.mapFileInfo.equals(mapFileInfo)) return false;
        if (!anotherMapData.addtionalInfo.equals(addtionalInfo)) return false;
        if (!anotherMapData.allMaps.equals(allMaps)) return false;

        return true;
    }
}
