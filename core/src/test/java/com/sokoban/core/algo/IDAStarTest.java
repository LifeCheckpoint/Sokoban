package com.sokoban.core.algo;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.sokoban.algo.IDAStar;
import com.sokoban.core.game.Logger;
import com.sokoban.core.map.MapData;
import com.sokoban.core.map.MapFileInfo;
import com.sokoban.core.map.MapFileParser;

public class IDAStarTest {
    List<MapData> cornerMaps = new ArrayList<>();
    
    public void prepareMaps() {
        cornerMaps.add(MapFileParser.parseMapDataChar(
            new MapFileInfo(), 
            "#######\n" +
            "#@ .#.#\n" +
            "#  $# #\n" +
            "# $   #\n" +
            "#  #  #\n" +
            "#######"
        ));
        cornerMaps.add(MapFileParser.parseMapDataChar(
            new MapFileInfo(), 
            "####___\n" +
            "#--###_\n" +
            "#----#_\n" +
            "#-$--#_\n" +
            "###-###\n" +
            "#-$-$-#\n" +
            "#..@..#\n" +
            "#--$--#\n" +
            "###--##\n" +
            "__####_"
        ));
    }

    // 采用普通方法进行测试
    public static void main(String[] args) {
        IDAStarTest test = new IDAStarTest();
        test.prepareMaps();
        IDAStar ida;
        LocalDateTime startTime;
        List<IDAStar.IDAState> states;
        
        // Map 0
        ida = new IDAStar(test.cornerMaps.get(0));
        startTime = LocalDateTime.now();
        states = ida.solve();
        Logger.info(String.format("#0 solve time = %dms", Duration.between(startTime, LocalDateTime.now()).toMillis()));
        for (IDAStar.IDAState state : states) {
            Logger.debug("\n" + state.toString());
        }

        // Map 1
        ida = new IDAStar(test.cornerMaps.get(1));
        startTime = LocalDateTime.now();
        states = ida.solve();
        Logger.info(String.format("#1 solve time = %dms", Duration.between(startTime, LocalDateTime.now()).toMillis()));
        for (IDAStar.IDAState state : states) {
            Logger.debug("\n" + state.toString());
        }
    }
}
