package com.sokoban.core.map;

import java.util.ArrayList;
import java.util.List;

import com.sokoban.core.game.Logger;
import com.sokoban.core.json.JsonManager;
import com.sokoban.core.logic.Direction;

/**
 * 地图数据解析类
 * @author StiCK-bot
 * @author Life_Checkpoint
 */
public class MapFileParser {
    /**
     * 地图数据解析器，解析标准地图格式并带附加文件信息
     * @param mapFileInfo 地图文件信息
     * @param mapFileString 地图数据的文本字串
     * @return 解析得到的 MapData，失败返回 null
     */
    public static MapData parseMapData(MapFileInfo mapFileInfo, String mapFileString) {
        MapData mapData = parseMapData(mapFileString);
        mapData.mapFileInfo = mapFileInfo; // 补充文件路径
        return mapData;
    }

    /**
     * 地图数据解析器，解析标准地图格式并带储存的文件信息
     * @param mapFileString 地图数据的文本字串
     * @return 解析得到的 MapData，失败返回 null
     */
    public static MapData parseMapData(String mapFileString) {
        MapData mapData = new JsonManager().parseJsonToObject(mapFileString, MapData.class); // 将地图文本 Json 序列化为数据
        if (mapData == null) {
            Logger.error("MapFileParser", "Parse map data failed");
            return null;
        }

        return mapData;
    }


    /**
     * 序列化地图数据为标准地图格式
     * @return 序列化结果，失败返回 null
     */
    public static String serializeMapData(MapData mapData) {
        return new JsonManager().getJsonString(mapData);
    }

    /**
     * 将字符地图转换为标准地图数据，与传统地图格式兼容
     * @param charMap 字符地图
     * @return 标准地图数据，失败返回 null
     */
    public static MapData parseMapData(char[][] charMap) {
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

    /** 
     * 将方向字符转换为标准方向 
     * @param directionChar 代表方向的字符，支持 UDLR udlr
     * @return 解析得到的方向，未知返回 Direction.None
     */
    public static Direction parseDirectionChar(char directionChar) {
        return switch (directionChar) {
            case 'U', 'u' -> Direction.Up;
            case 'D', 'd' -> Direction.Down;
            case 'L', 'l' -> Direction.Left;
            case 'R', 'r' -> Direction.Right;
            default -> Direction.None;
        };
    }

    /**
     * 传统地图数据解析器，解析为标准地图格式
     * @param mapFileInfo 地图文件信息
     * @param mapFileString 地图数据的文本字串
     * @return 解析得到的 MapData，失败返回 null
     */
    public static MapData parseMapDataChar(MapFileInfo mapFileInfo, String mapFileString) {
        List<char[]> currentMap = new ArrayList<>();
        String[] lines = mapFileString.split("\n");

        for (String line : lines) {
            line = line.trim(); // 去除行首尾空白符
            currentMap.add(line.toCharArray()); // 保存地图行
        }

        char[][] charMap = currentMap.toArray(new char[0][]);
        flipHorizontal(charMap); // 由于读入地图是从上往下，所以需要翻转地图

        // 将地图映射到标准类型
        MapData map = parseMapData(charMap);
        map.mapFileInfo = mapFileInfo;
        return map;
    }

    /** 数组沿水平轴反转 */
    private static void flipHorizontal(char[][] matrix) {
        int rows = matrix.length;
        for (int i = 0; i < rows / 2; i++) {
            char[] temp = matrix[i];
            matrix[i] = matrix[rows - 1 - i];
            matrix[rows - 1 - i] = temp;
        }
    }

    // 注意，不支持将标准地图转换回字符地图
}
