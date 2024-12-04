package com.sokoban.utils;

import com.sokoban.core.map.gamedefault.SokobanLevels;
import com.sokoban.core.map.gamedefault.SokobanMaps;

public class EnumUtils {
    public static SokobanLevels nameOfLevel(String name) {
        for (SokobanLevels e : SokobanLevels.values()) if (e.name().equals(name)) return e;
        return SokobanLevels.None;
    }

    public static SokobanMaps nameOfMap(String name) {
        for (SokobanMaps e : SokobanMaps.values()) if (e.name().equals(name)) return e;
        return SokobanMaps.None;
    }
}
