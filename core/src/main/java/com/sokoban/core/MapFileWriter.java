package com.sokoban.core;

public class MapFileWriter {
    int[][] map = null;
    public static void main (String[] args) {
        MapFileReader reader = new MapFileReader();
        String mapContent = reader.readMapByLevelAndName("tutorial", "map1");
        String[] lines = mapContent.split("\n");

        String addtionInfo = lines[0];

        int subMapNum = Integer.parseInt(lines[1]);
        String[] setMap = lines[2].split(" ");
        int[][] map = new int[Integer.parseInt(setMap[0])][Integer.parseInt(setMap[1])];
        for(int i = 3; i < map.length; i++) {
            String[] line = lines[i].split(" ");
            for(int j = 0; j < map[i].length; j++) {
                map[i][j] = Integer.parseInt(line[j]);
            }
        }
    }
}









