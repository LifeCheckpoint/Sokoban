package com.sokoban.core.map;

import java.util.ArrayList;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.sokoban.core.logic.ObjectType;

public class MapFileParserTest {
    MapData map = new MapData();
    String result;
    
    @BeforeClass
    public void setUp() {
        map.addtionalInfo = "Test for additional Info.\nWell.";
        map.mapFileInfo = new MapFileInfo();
        map.allMaps = new ArrayList<>();
        map.allMaps.add(new SubMapData(11, 45));
        map.allMaps.add(new SubMapData(14, 19));

        ObjectType[][] objectLayer1 = map.allMaps.get(0).getObjectLayer();
        ObjectType[][] targetLayer1 = map.allMaps.get(0).getTargetLayer();
        ObjectType[][] decLayer1 = map.allMaps.get(0).getDecorationLayer();

        ObjectType[][] objectLayer2 = map.allMaps.get(1).getObjectLayer();
        ObjectType[][] targetLayer2 = map.allMaps.get(1).getTargetLayer();
        ObjectType[][] decLayer2 = map.allMaps.get(1).getDecorationLayer();

        objectLayer1[0][0] = ObjectType.Box;
        objectLayer1[1][1] = ObjectType.Player;
        objectLayer1[2][2] = ObjectType.Wall;
        targetLayer1[0][0] = ObjectType.PlayerTarget;
        targetLayer1[1][1] = ObjectType.BoxTarget;
        decLayer1[3][3] = ObjectType.GroundDarkGray;

        objectLayer2[0][13] = ObjectType.Wall;
        objectLayer2[4][9] = ObjectType.Air;
        objectLayer2[8][7] = ObjectType.Unknown;
        targetLayer2[6][6] = ObjectType.PlayerTarget;
        decLayer2[4][5] = ObjectType.GroundDarkGray;
    }

    @Test
    public void testParseToJson() {
        result = MapFileParser.serializeMapData(map);
        MapData parsedMap = MapFileParser.parseMapData(new MapFileInfo(), result);
        Assert.assertEquals(parsedMap, map, "Not equal map");
    }
}
