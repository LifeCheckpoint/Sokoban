package com.sokoban.core.algo;

import java.util.ArrayList;
import java.util.List;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.sokoban.algo.CutVertexChecker;
import com.sokoban.core.map.MapData;
import com.sokoban.core.map.MapFileInfo;
import com.sokoban.core.map.MapFileParser;

/**
 * 割点算法测试
 * @author Life_Checkpoint
 */
public class CutVertexCheckerTest {
    List<MapData> cornerMaps = new ArrayList<>();
    
    @BeforeClass
    public void prepareMaps() {
        cornerMaps.add(MapFileParser.parseMapDataChar(
            new MapFileInfo(), 
            "#######\n" +
            "# #   #\n" +
            "# #  ##\n" +
            "#     #\n" +
            "#   # #\n" +
            "#######"
        ));
        cornerMaps.add(MapFileParser.parseMapDataChar(
            new MapFileInfo(), 
            "#######\n" +
            "# #   #\n" +
            "#   ###\n" +
            "# #   #\n" +
            "#  #  #\n" +
            "#######"
        ));
    }

    @Test
    public void testCornerCase() {
        CutVertexChecker checker0 = new CutVertexChecker(cornerMaps.get(0).allMaps.get(0));
        CutVertexChecker checker1 = new CutVertexChecker(cornerMaps.get(1).allMaps.get(0));

        /*
            #######
            # # o #
            #o#  ##
            #o ooo#
            #   # #
            #######
        */
        Assert.assertTrue(checker0.isCutVertex(1, 2), "Map 0 - (1, 2) should be a cut vertex");
        Assert.assertTrue(checker0.isCutVertex(1, 3), "Map 0 - (1, 3) should be a cut vertex");
        Assert.assertTrue(checker0.isCutVertex(3, 2), "Map 0 - (3, 2) should be a cut vertex");
        Assert.assertTrue(checker0.isCutVertex(4, 2), "Map 0 - (4, 2) should be a cut vertex");
        Assert.assertTrue(checker0.isCutVertex(5, 2), "Map 0 - (5, 2) should be a cut vertex");
        Assert.assertTrue(checker0.isCutVertex(4, 4), "Map 0 - (4, 4) should be a cut vertex");

        /*
            #######
            # #oo #
            #ooo###
            #o#oo #
            #o #  #
            ####### 
        */
        Assert.assertTrue(checker1.isCutVertex(1, 1), "Map 0 - (1, 1) should be a cut vertex");
        Assert.assertTrue(checker1.isCutVertex(1, 2), "Map 0 - (1, 2) should be a cut vertex");
        Assert.assertTrue(checker1.isCutVertex(1, 3), "Map 0 - (1, 3) should be a cut vertex");
        Assert.assertTrue(checker1.isCutVertex(2, 3), "Map 0 - (2, 3) should be a cut vertex");
        Assert.assertTrue(checker1.isCutVertex(3, 3), "Map 0 - (3, 3) should be a cut vertex");
        Assert.assertTrue(checker1.isCutVertex(3, 2), "Map 0 - (3, 2) should be a cut vertex");
        Assert.assertTrue(checker1.isCutVertex(4, 2), "Map 0 - (4, 2) should be a cut vertex");
        Assert.assertTrue(checker1.isCutVertex(3, 4), "Map 0 - (3, 4) should be a cut vertex");
        Assert.assertTrue(checker1.isCutVertex(4, 4), "Map 0 - (4, 4) should be a cut vertex");
    }
}
