package com.sokoban.core;

import java.util.ArrayList;
import java.util.List;

public class MapFileWriter {
    int[][] map;
    public int[][] getMap() {
        return map;
    }
    MapFileReader reader = new MapFileReader();



    public void convertToMap(List<String> readlines) {
        this.map = new int[readlines.size()][];

        for (int i = 0; i < readlines.size(); i++) {
            String[] pieces = readlines.get(i).split(",");
            this.map[i] = new int[pieces.length];
            for (int j = 0; j < pieces.length; j++) {
                this.map[i][j] = Integer.parseInt(pieces[j]);
            }
        }
    }

    public List<String> convertToList() {
        List<String> lines = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        for (int[] ints : this.map) {
            sb.setLength(0);
            for (int anInt : ints) {
                sb.append(anInt).append(",");
            }
            sb.setLength(sb.length() - 1);
            lines.add(sb.toString());
        }
        return lines;
    }

}









