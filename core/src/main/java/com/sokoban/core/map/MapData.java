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
<<<<<<< HEAD

    public void setAddtionInfo(String addtionInfo) {
        this.addtionInfo = addtionInfo;
    }

    public List<Map> getMaps() {
        return maps;
    }

    public void setMaps(List<Map> maps) {
        this.maps = maps;
    }

    public String[] getMapReader(String levelName, String mapName) {
        MapFileReader reader = new MapFileReader();
        String mapContent = reader.readMapByLevelAndName("tutorial", "map1");
        String[] lines = mapContent.split("\n");
        for (int i = 0; i < lines.length; i++)
            lines[i] = lines[i].trim();
        return lines;
    }


    public void MapDataManager(MapData mapData) {
        String[] Lines = mapData.getMapReader("tutorial", "map1");//这里应该要读取什么输入
        this.addtionInfo = Lines[0];
        this.mapSize = Integer.parseInt(Lines[1]);
        List<Map> allmaps = new ArrayList<>();


    }





=======
>>>>>>> 21b519a4b3c1abf1d41b1f074aa03882cdbc9dbb
}
