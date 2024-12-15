package com.sokoban.algo;

import java.util.List;

import com.sokoban.algo.IDAStar.IDAState;
import com.sokoban.core.map.MapData;

public class SearchAlgo implements Runnable {
    public List<IDAState> result = null;
    public IDAStar starFind;
    private MapData map;
    
    public SearchAlgo(MapData map) {
        this.map = map;
    }

    @Override
    public void run() {
        result = null;
        starFind = new IDAStar(map);
        result = starFind.solve();
    }
}
