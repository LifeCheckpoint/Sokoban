package com.sokoban.core;

public class MapFileWriter {
    public static void main (String[] args) {
        MapFileReader reader = new MapFileReader();
        String mapContent = reader.readMapByLevelAndName("tutorial", "map1");
        String[] lines = mapContent.split("\n");
        for (int i = 0; i < lines.length; i++)
            lines[i] = lines[i].trim();
        String addtionInfo = lines[0];
        System.out.println(addtionInfo);

        int subMapNum = Integer.parseInt(lines[1]);
        if (subMapNum == 1) {
            String[] setMap = lines[2].split(" ");
            Map map = new Map();
            map.setMap(new int[Integer.parseInt(setMap[0])][Integer.parseInt(setMap[1])]);
            for(int i = 3; i < map.getMap().length; i++) {
                String[] line = lines[i].split(" ");
                for(int j = 0; j < map.getMap()[i].length; j++) {
                    map.getMap()[i][j] = Integer.parseInt(line[j]);
                }
            }
/*            for(int i = 0; i < map.length; i++) {
                for(int j = 0; j < map[i].length; j++) {
                    System.out.print(map[i][j] + " ");
                }
            }*/
        } else if (subMapNum == 2) {
            String[] setMap1 = lines[2].split(" ");
            String[] setMap2 = lines[2+Integer.parseInt(setMap1[0])+1].split(" ");
            int[][] map1 = new int[Integer.parseInt(setMap1[0])][Integer.parseInt(setMap1[1])];
            int[][] map2 = new int[Integer.parseInt(setMap2[0])][Integer.parseInt(setMap2[1])];

            for(int i = 3; i < map1.length; i++) {
                String[] line = lines[i].split(" ");
                for(int j = 0; j < map1[i].length; j++) {
                    map1[i][j] = Integer.parseInt(line[j]);
                }
            }
            for(int i = 3; i < map2.length; i++) {
                String[] line = lines[i].split(" ");
                for(int j = 0; j < map2[i].length; j++) {
                    map2[i][j] = Integer.parseInt(line[j]);
                }
            }
        }
    }
}









