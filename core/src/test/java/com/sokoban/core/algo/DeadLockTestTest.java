package com.sokoban.core.algo;

import java.util.ArrayList;
import java.util.List;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.sokoban.algo.DeadLockTest;
import com.sokoban.core.map.MapData;
import com.sokoban.core.map.MapFileInfo;
import com.sokoban.core.map.MapFileParser;

/**
 * 死锁状态检测测试
 * @author Life_Checkpoint
 */
public class DeadLockTestTest {
    List<MapData> cornerMaps = new ArrayList<>();
    
    @BeforeClass
    public void prepareMaps() {
        cornerMaps.add(MapFileParser.parseMapDataChar(
            new MapFileInfo(), 
            "######\n" + 
            "#---$#\n" + 
            "#.---#\n" + 
            "######"
        ));
        cornerMaps.add(MapFileParser.parseMapDataChar(
            new MapFileInfo(), 
            "----###\n" + 
            "#####$#\n" + 
            "#-----#\n" + 
            "#######"
        ));
        cornerMaps.add(MapFileParser.parseMapDataChar(
            new MapFileInfo(), 
            "###\n" + 
            "#$#\n" + 
            "###"
        ));
        cornerMaps.add(MapFileParser.parseMapDataChar(
            new MapFileInfo(), 
            "###\n" + 
            "#*#\n" + 
            "#-#"
        ));
        cornerMaps.add(MapFileParser.parseMapDataChar(
            new MapFileInfo(), 
            "#$#\n" + 
            "#$-\n" + 
            "#-#"
        ));
    }

    @Test
    public void testCornerCase() {
        Assert.assertTrue(DeadLockTest.cornerLockTest(cornerMaps.get(0).allMaps.get(0), 4, 2, false));
        Assert.assertTrue(DeadLockTest.cornerLockTest(cornerMaps.get(1).allMaps.get(0), 5, 2, false));
        Assert.assertTrue(DeadLockTest.cornerLockTest(cornerMaps.get(2).allMaps.get(0), 1, 1, false));
        Assert.assertFalse(DeadLockTest.cornerLockTest(cornerMaps.get(3).allMaps.get(0), 1, 1, true));
        Assert.assertFalse(DeadLockTest.cornerLockTest(cornerMaps.get(4).allMaps.get(0), 1, 1, true));
        Assert.assertTrue(DeadLockTest.cornerLockTest(cornerMaps.get(4).allMaps.get(0), 1, 1, false));

    }
}
