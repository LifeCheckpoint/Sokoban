package com.sokoban.core.game;

import java.util.ArrayList;
import java.util.List;

/**
 * 地图关卡数据类
 * @author StiCK-bot
 * @author Life_Checkpoint
 */
public class MapData {
    public MapInfo mapFileInfo = new MapInfo(); // 地图文件信息
    public String addtionalInfo = ""; // 地图附加信息
    public int subMapNums = 0; // 子地图数量
    public List<Things[][]> allMaps = new ArrayList<>(); // 所有子地图

    /**
     * 地图数据类无参构造，属性可以稍后填入
     */
    public MapData() {}

    /**
     * 地图数据类构造
     * @param mapFileInfo 地图文件数据
     * @param addtionInfo 地图附加数据文本
     * @param subMapNums 子地图数量
     * @param allMaps 所有子地图
     */
    public MapData(MapInfo mapFileInfo, String addtionInfo, int subMapNums, List<Things[][]> allMaps) {
        this.mapFileInfo = mapFileInfo;
        this.addtionalInfo = addtionInfo;
        this.subMapNums = subMapNums;
        this.allMaps = allMaps;
    }

    // public 属性无需使用 getter / setter
}
