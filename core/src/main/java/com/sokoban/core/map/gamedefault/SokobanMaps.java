package com.sokoban.core.map.gamedefault;

public enum SokobanMaps {
    None(""),

    // Tutorial
    Turotial_Tutorial("tutorial"),

    // Origin
    Origin_1("map1"),
    Origin_2("map2"),
    Origin_3("map3"),
    Origin_4("map4"),
    Origin_5("map5"),

    // Moving
    Moving_1("map1"),
    Moving_2("map2"),
    Moving_3("map3"),
    Moving_4("map4"),
    Moving_5("map5"),
    Moving_6("map6");

    private final String mapName;
    SokobanMaps(String mapName) {this.mapName = mapName;}
    public String getMapName() {return mapName;}
}
