package com.sokoban.core;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.sokoban.core.game.MapInfo;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;

public class MapFileReaderTest {

    private final String TEST_DIR = "./test-files/bin/maps";
    private MapFileReader mapFileReader;

    private String activeTestDir;

    @BeforeClass
    public void setUp() throws IOException {
        mapFileReader = new MapFileReader(TEST_DIR);
        activeTestDir = checkOrCreateTestDirectory();
        createTestEnvironment();
    }

    @Test
    public void testListAllMaps() {
        List<MapInfo> maps = mapFileReader.listAllMaps();
        Assert.assertEquals(maps.size(), 9, "Expected 9 maps in total.");
        Assert.assertTrue(maps.stream().anyMatch(map -> map.levelName.equals("level1") && map.mapName.equals("map1")),
                "Map level1/map1 should be present.");
        Assert.assertTrue(maps.stream().anyMatch(map -> map.levelName.equals("level3") && map.mapName.equals("map3")),
                "Map level3/map3 should be present.");
    }

    @Test
    public void testReadMapByLevelAndName() {
        String mapContent = mapFileReader.readMapByLevelAndName("level1", "map1");
        Assert.assertEquals(mapContent, "Content of map1 in level1", "Map content should match the expected value.");
    }

    @Test
    public void testReadMapByPath() {
        String path = Path.of(activeTestDir, "level2", "map2.map").toString();
        String mapContent = mapFileReader.readMapByPath(path);
        Assert.assertEquals(mapContent, "Content of map2 in level2", "Map content should match the expected value.");
    }

    @Test
    public void testCreateAndReadNewMaps() {
        String level4Path = Path.of(activeTestDir, "level4").toString();
        String emptyMapPath = Path.of(level4Path, "emptyMap.map").toString();
        String filledMapPath = Path.of(level4Path, "filledMap.map").toString();

        if (new File(emptyMapPath).exists()) new File(emptyMapPath).delete();
        if (new File(filledMapPath).exists()) new File(filledMapPath).delete();

        new File(level4Path).mkdirs();

        boolean isEmptyCreated = mapFileReader.createEmptyMap(emptyMapPath);
        Assert.assertTrue(isEmptyCreated, "Empty map should be created successfully.");
        String emptyMapContent = mapFileReader.readMapByPath(emptyMapPath);
        Assert.assertEquals(emptyMapContent, "", "Empty map content should be empty.");

        boolean isContentCreated = mapFileReader.createMapWithContent(filledMapPath, "Filled map content");
        Assert.assertTrue(isContentCreated, "Filled map should be created successfully.");
        String filledMapContent = mapFileReader.readMapByPath(filledMapPath);
        Assert.assertEquals(filledMapContent, "Filled map content", "Filled map content should match.");
    }

    private String checkOrCreateTestDirectory() throws IOException {
        // 清除相关目录内容
        File targetDir = new File(TEST_DIR);
        if (targetDir.exists()) targetDir.delete();

        // 重建
        Files.createDirectories(targetDir.toPath());
        return TEST_DIR;
    }

    private void createTestEnvironment() throws IOException {
        Path baseDir = Path.of(activeTestDir);
        Files.createDirectories(baseDir);

        createLevelWithMaps("level1", "map1", "map2");
        createLevelWithMaps("level2", "map1", "map2");
        createLevelWithMaps("level3", "map1", "map2", "map3");
    }

    private void createLevelWithMaps(String levelName, String... mapNames) throws IOException {
        Path levelDir = Path.of(activeTestDir, levelName);
        Files.createDirectories(levelDir);
        for (String mapName : mapNames) {
            Path mapPath = levelDir.resolve(mapName + ".map");
            Files.writeString(mapPath, "Content of " + mapName + " in " + levelName);
        }
    }

}
