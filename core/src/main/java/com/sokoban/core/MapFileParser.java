package com.sokoban.core;

import java.util.ArrayList;
import java.util.List;

import com.sokoban.core.game.MapData;
import com.sokoban.core.game.MapInfo;
import com.sokoban.core.game.MapMapper;
import com.sokoban.core.game.Pos;
import com.sokoban.core.game.Things;

/**
 * 地图数据解析类
 * @author StiCK-bot
 * @author Life_Checkpoint
 * @author ChatGPT
 */
public class MapFileParser {
    private static final String MAP_SEPARATOR = "-";

    /**
     * 地图数据解析器
     * @param mapFileInfo 地图文件信息
     * @param mapFileString 地图数据的文本字串
     * @return 解析得到的 MapData，失败返回 null
     */
    public static MapData parseMapData(MapInfo mapFileInfo, String mapFileString) {
        // 地图数据
        MapData mapData = new MapData();
        mapData.mapFileInfo = mapFileInfo;

        // 分割地图文本内容每一行
        String[] lines = preprocessLines(mapFileString);

        // 检查地图文件的有效性
        if (!validateLineCount(lines)) {
            Logger.error("MapFileParser", String.format("Fail to parse map, expected lines >= 4, get %d", lines.length));
            return null;
        }

        // 解析附加信息与子地图数量
        if (!parseMetadata(lines, mapData)) {
            return null;
        }

        // 解析子地图文本数据
        List<List<String>> submapFileTexts = extractSubmapData(lines, mapData.subMapNums);
        if (submapFileTexts == null) {
            return null;
        }

        // 解析子地图内容
        if (!parseSubmaps(submapFileTexts, mapData)) {
            return null;
        }

        return mapData;
    }

    /**
     * 预处理地图文件行数据，移除首尾空格
     * @param mapFileString 地图文件的原始文本
     * @return 处理后的行数组
     */
    private static String[] preprocessLines(String mapFileString) {
        String[] lines = mapFileString.split("\n");
        for (int i = 0; i < lines.length; i++) {
            lines[i] = lines[i].trim();
        }
        return lines;
    }

    /**
     * 验证地图文件行数是否满足最低要求
     * @param lines 地图文件行数组
     * @return 是否满足要求
     */
    private static boolean validateLineCount(String[] lines) {
        return lines.length >= 4;
    }

    /**
     * 解析地图的附加信息和子地图数量
     * @param lines 地图文件行数组
     * @param mapData 地图数据对象
     * @return 是否解析成功
     */
    private static boolean parseMetadata(String[] lines, MapData mapData) {
        mapData.addtionalInfo = lines[0];
        try {
            mapData.subMapNums = Integer.parseInt(lines[1]);
            return true;
        } catch (Exception e) {
            Logger.error("MapFileParser", String.format("Fail to parse map, expect to get the num of sub maps, get %s", lines[1]));
            return false;
        }
    }

    /**
     * 提取子地图文本数据
     * @param lines 地图文件行数组
     * @param subMapNums 子地图数量
     * @return 子地图文本列表，解析失败返回 null
     */
    private static List<List<String>> extractSubmapData(String[] lines, int subMapNums) {
        List<List<String>> submapFileTexts = new ArrayList<>();
        for (int i = 0; i < subMapNums; i++) {
            submapFileTexts.add(new ArrayList<>());
        }

        try {
            int currentSubmapIndex = 0;
            for (int i = 2; i < lines.length; i++) {

                // 如果是分隔符
                if (lines[i].equals(MAP_SEPARATOR)) {
                    currentSubmapIndex += 1;
                    if (currentSubmapIndex >= subMapNums) {
                        throw new Exception(String.format(
                            "Sub map index exceeds defined sub map count. Index: %d, Defined: %d",
                            currentSubmapIndex, subMapNums
                        ));
                    }

                // 不是分隔符
                } else {
                    submapFileTexts.get(currentSubmapIndex).add(lines[i]);
                }
            }
            return submapFileTexts;
        } catch (Exception e) {
            Logger.error("MapFileParser", String.format("Fail to parse map because %s", e.getMessage()));
            return null;
        }
    }

    /**
     * 解析所有子地图的内容
     * @param submapFileTexts 子地图的文本内容
     * @param mapData 地图数据对象
     * @return 是否解析成功
     */
    private static boolean parseSubmaps(List<List<String>> submapFileTexts, MapData mapData) {
        try {
            for (int subMapIndex = 0; subMapIndex < mapData.subMapNums; subMapIndex++) {
                parseSingleSubmap(submapFileTexts.get(subMapIndex), mapData, subMapIndex);
            }
            return true;
        } catch (Exception e) {
            Logger.error("MapFileParser", String.format("Fail to parse map because %s", e.getMessage()));
            return false;
        }
    }

    /**
     * 解析单个子地图的内容
     * @param subMapContent 子地图的文本内容
     * @param mapData 地图数据对象
     * @param subMapIndex 当前子地图的索引
     * @throws Exception 如果解析失败抛出异常
     */
    private static void parseSingleSubmap(List<String> subMapContent, MapData mapData, int subMapIndex) throws Exception {
        for (int lineIndex = 0; lineIndex < subMapContent.size(); lineIndex++) {
            if (lineIndex == 0) {
                parseSubmapDimensions(subMapContent.get(lineIndex), mapData, subMapIndex);
            } else {
                parseSubmapRow(subMapContent.get(lineIndex), mapData, subMapIndex, lineIndex - 1);
            }
        }

        if (subMapContent.size() - 1 != mapData.allMaps.get(subMapIndex).length) {
            throw new Exception(String.format("Sub map %d row count does not match height definition. Expected: %d, get: %d",
                subMapIndex, mapData.allMaps.get(subMapIndex).length, subMapContent.size() - 1));
        }
    }

    /**
     * 解析子地图的高宽信息
     * @param dimensionLine 高宽定义行
     * @param mapData 地图数据对象
     * @param subMapIndex 当前子地图索引
     * @throws Exception 如果解析失败抛出异常
     */
    private static void parseSubmapDimensions(String dimensionLine, MapData mapData, int subMapIndex) throws Exception {
        String[] HW = dimensionLine.split(" ");
        if (HW.length != 2) {
            throw new Exception(String.format(
                "The first line of sub map %d is not valid. Expect height and width two numbers, get %s",
                subMapIndex, 
                dimensionLine
            ));
        }
        int height = Integer.parseInt(HW[0]);
        int width = Integer.parseInt(HW[1]);
        mapData.allMaps.add(new Things[height][width]);
    }

    /**
     * 解析子地图的一行内容
     * @param rowContent 行内容
     * @param mapData 地图数据对象
     * @param subMapIndex 当前子地图索引
     * @param rowIndex 当前行索引
     * @throws Exception 如果解析失败抛出异常
     */
    private static void parseSubmapRow(String rowContent, MapData mapData, int subMapIndex, int rowIndex) throws Exception {
        String[] lineThings = rowContent.split(" ");
        Things[][] subMap = mapData.allMaps.get(subMapIndex);
        if (lineThings.length != subMap[0].length) {
            throw new Exception(String.format(
                "The width of sub map %d is not equal to definition. Expect %d, get %d",
                subMapIndex, 
                subMap[0].length, 
                lineThings.length
            ));
        }

        for (int i = 0; i < lineThings.length; i++) {
            subMap[rowIndex][i] = new Things(new Pos(rowIndex, i), MapMapper.MapNumToType(Integer.parseInt(lineThings[i])));
        }
    }
}
