package com.sokoban.core;

import com.sokoban.core.game.MapData;
import com.sokoban.core.game.MapInfo;
import com.sokoban.core.manager.JsonManager;

/**
 * 地图数据解析类
 * @author StiCK-bot
 * @author Life_Checkpoint
 */
public class MapFileParser {
    /**
     * 地图数据解析器
     * @param mapFileInfo 地图文件信息
     * @param mapFileString 地图数据的文本字串
     * @return 解析得到的 MapData，失败返回 null
     */
    public static MapData parseMapData(MapInfo mapFileInfo, String mapFileString) {
        MapData mapData = new JsonManager().parseJsonToObject(mapFileString, MapData.class); // 将地图文本 Json 序列化为数据
        mapData.mapFileInfo = mapFileInfo; // 补充文件路径
        return mapData;
    }

    /**
     * 序列化地图数据
     * @return 序列化结果
     */
    public static String serializeMapData(MapData mapData) {
        MapData mapDataWithoutFileInfo = new MapData(new MapInfo(), mapData.addtionalInfo, mapData.subMapNums, mapData.allMaps); // 去除文件信息
        return new JsonManager().getJsonString(mapDataWithoutFileInfo);
    }
}
