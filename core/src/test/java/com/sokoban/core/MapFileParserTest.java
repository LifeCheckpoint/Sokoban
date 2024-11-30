package com.sokoban.core;

import static org.testng.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.sokoban.core.game.MapData;
import com.sokoban.core.game.MapInfo;
import com.sokoban.core.game.ObjectType;
import com.sokoban.core.game.Pos;
import com.sokoban.core.game.Things;

public class MapFileParserTest {
    private MapInfo mapInfo;
    private List<String> stringMapDatas = new ArrayList<>();
    private List<MapData> mapDatas = new ArrayList<>();

    private Things[][] transObjTypeToThings(ObjectType[][] objs) {
        Things[][] things = new Things[objs.length][objs[0].length];

        for (int i = 0; i < objs.length; i++) {
            for (int j = 0; i < objs[0].length; j++) {
                things[i][j] = new Things(new Pos(i, j), objs[i][j]);
            }
        }

        return things;
    }

    @BeforeClass
    public void setUp() {
        mapInfo = new MapInfo("path0", "level0", "map0");

        // 测试数据 1
        stringMapDatas.add("some description\n" +
                           "1\n" +
                           "5 4\n" +
                           "1 1 1 1\n" +
                           "1 0 0 1\n" +
                           "1 0 0 2\n" +
                           "1 0 0 1\n" +
                           "1 0 0 0");
        List<Things[][]> mapContent = new ArrayList<>();
        ObjectType[][] layer0 = new ObjectType[][] {
            {ObjectType.WALL, ObjectType.WALL, ObjectType.WALL, ObjectType.WALL},
            {ObjectType.WALL, ObjectType.AIR, ObjectType.AIR, ObjectType.WALL},
            {ObjectType.WALL, ObjectType.AIR, ObjectType.AIR, ObjectType.WALL},
            {ObjectType.WALL, ObjectType.AIR, ObjectType.AIR, ObjectType.WALL},
            {ObjectType.WALL, ObjectType.AIR, ObjectType.AIR, ObjectType.AIR},
        };
        mapContent.add(transObjTypeToThings(layer0));
        mapDatas.add(new MapData(mapInfo, "some description", 1, mapContent));
    }

    @Test
    public void testSubmap0() {
        assertEquals(MapFileParser.parseMapData(mapInfo, stringMapDatas.get(0)), mapDatas.get(0), "Map Data 0 is not equal!");
    }
}
