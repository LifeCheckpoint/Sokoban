package com.sokoban.core.map.gamedefault;

public enum SokobanMaps {
    None(""),

    // Tutorial
    Turotial_Tutorial("tutorial"),

    // Origin
    Origin_1("Origin1"),
    Origin_2("Origin2"),
    Origin_3("Origin3"),
    Origin_4("Origin4"),
    Origin_5("Origin5");

    private final String mapName;
    SokobanMaps(String mapName) {this.mapName = mapName;}
    public String getMapName() {return mapName;}
}
