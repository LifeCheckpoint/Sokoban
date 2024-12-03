package com.sokoban.core.map.gamedefault;

public enum SokobanMaps {
    // Tutorial
    Turotial_Tutorial("tutorial"),

    // Origin
    Origin_1("Origin - 1"),
    Origin_2("Origin - 2"),
    Origin_3("Origin - 3"),
    Origin_4("Origin - 4"),
    Origin_5("Origin - 5");

    private final String mapName;
    SokobanMaps(String mapName) {this.mapName = mapName;}
    public String getMapName() {return mapName;}
}
