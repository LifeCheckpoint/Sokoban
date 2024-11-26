package com.sokoban.core;

import java.util.List;

public class test {
    public static void main(String[] args) {
        MapFileWriter gobangMap = new MapFileWriter();
        FileUtil fileUtil = new AdvancedFileUtil();
        List<String> lines = fileUtil.readFileToList("./test-files/bin/maps");
        gobangMap.convertToMap(lines);

        gobangMap.printMap();

        fileUtil.writeFileFromList("./test-files/bin/maps", gobangMap.convertToList());

    }
}
