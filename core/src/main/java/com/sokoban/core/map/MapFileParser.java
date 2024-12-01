package com.sokoban.core.map;

import java.util.ArrayList;

import com.sokoban.core.Logger;
import com.sokoban.core.manager.JsonManager;

/**
 * 地图数据解析类
 * @author StiCK-bot
 * @author Life_Checkpoint
 */
public class MapFileParser {
    /**
     * 地图数据解析器，解析标准地图格式
     * @param mapFileInfo 地图文件信息
     * @param mapFileString 地图数据的文本字串
     * @return 解析得到的 MapData，失败返回 null
     */
    public static MapData parseMapData(MapFileInfo mapFileInfo, String mapFileString) {
        MapData mapData = new JsonManager().parseJsonToObject(mapFileString, MapData.class); // 将地图文本 Json 序列化为数据
        mapData.mapFileInfo = mapFileInfo; // 补充文件路径
        return mapData;
    }

    /**
     * 序列化地图数据为标准地图格式
     * @return 序列化结果，失败返回 null
     */
    public static String serializeMapData(MapData mapData) {
        MapData mapDataWithoutFileInfo = new MapData(new MapFileInfo(), mapData.addtionalInfo, mapData.allMaps); // 去除文件信息
        return new JsonManager().getJsonString(mapDataWithoutFileInfo);
    }

    /**
     * 将字符地图转换为标准地图数据，与传统地图格式兼容
     * @param charMap 字符地图
     * @return 标准地图数据，失败返回 null
     */
    public static MapData transformCharMapToIntegerMap(char[][] charMap) {
        if (charMap == null || charMap.length == 0 || charMap[0].length == 0) {
            Logger.error("MapMapper", "Cannot convert a null char map object");
            return null;
        }
    
        // 初始化一个地图类
        MapData map = new MapData(new MapFileInfo(), "", new ArrayList<>());
        map.allMaps.add(new SubMapData(charMap.length, charMap[0].length));
    
        // 逐元素转换
        for (int y = 0; y < map.allMaps.getLast().height; y++) {
            for (int x = 0; x < map.allMaps.getLast().width; x++) {
                map.allMaps.getLast().getObjectLayer()[y][x] = MapMapper.MapCharToObjectType(charMap[y][x]);
                map.allMaps.getLast().getTargetLayer()[y][x] = MapMapper.MapCharToTargetType(charMap[y][x]);
            }
        }
    
        return map;
    }

    // 注意，不支持将标准地图转换回字符地图
}
