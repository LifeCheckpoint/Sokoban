package com.sokoban.core.game;

import com.sokoban.core.MapFileReader;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MapData {
    public String addtionInfo;
    public int mapSize;
    public List<Map> maps;

    public MapData(String addtionInfo, List<Map[][]> maps) {
        this.addtionInfo = addtionInfo;
        this.maps = maps;
    }

    public String getAddtionInfo() {
        return addtionInfo;
    }

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





}
