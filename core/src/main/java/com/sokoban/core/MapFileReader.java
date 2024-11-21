package com.sokoban.core;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

/**
 * MapFileReader 类
 * <br><br>
 * 支持地图文件的简单读取，以及简单错误处理
 * @author Life_Checkpoint
 * @author ChatGPT
 */
public class MapFileReader {
    private String mapsDirectory;

    private final String DEFAULT_MAPS_DIRECTORY = "./test-files/bin/maps";
    private final String MAP_FILE_EXTENSION = ".map";

    /**
     * MapFileReader 构造
     */
    public MapFileReader() {
        mapsDirectory = DEFAULT_MAPS_DIRECTORY;
    }

    /**
     * MapFileReader 构造
     * @param mapsDirectory 地图库存放位置
     */
    public MapFileReader(String mapsDirectory) {
        this.mapsDirectory = mapsDirectory;
    }

    /**
     * 在指定目录中搜索所有地图
     * @return List<MapInfo> 地图信息 List
     */
    public List<MapInfo> listAllMaps() {
        List<MapInfo> mapList = new ArrayList<>();
        try {
            File mapsDir = new File(mapsDirectory);

            // 地图目录不存在
            if (!mapsDir.exists() || !mapsDir.isDirectory()) {
                System.out.println("MapManager Maps directory does not exist or is not a directory: " + mapsDirectory);
                return mapList;
            }

            // 搜索文件
            Files.walk(mapsDir.toPath())
                    .filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(MAP_FILE_EXTENSION))
                    .forEach(path -> {
                        File file = path.toFile();
                        String relativePath = mapsDir.toPath().relativize(path.getParent()).toString();
                        String mapName = file.getName().replace(MAP_FILE_EXTENSION, "");
                        mapList.add(new MapInfo(path.toString(), relativePath, mapName));
                    });

        } catch (IOException e) {
            System.out.println("MapManager Error listing map files: " + e.getMessage());
        }
        return mapList;
    }

    /**
     * 从 level - mapName 读取地图内容
     * @param levelName level 名称
     * @param mapName 地图名称
     * @return 地图内容
     */
    public String readMapByLevelAndName(String levelName, String mapName) {
        Path mapPath = Path.of(mapsDirectory, levelName, mapName + MAP_FILE_EXTENSION);
        return readMapByPath(mapPath.toString());
    }

    /**
     * 从路径读取地图内容
     * @param path 地图路径
     * @return 地图内容，失败返回 null
     */
    public String readMapByPath(String path) {
        try {
            return Files.readString(Path.of(path));
        } catch (IOException e) {
            System.out.println("MapManager Error reading map file: " + path + " - " + e.getMessage());
            return null;
        }
    }

    /**
     * 新建空地图
     * @param path 地图路径
     * @return 是否建立成功
     */
    public boolean createEmptyMap(String path) {
        return createMapWithContent(path, "");
    }

    /**
     * 创建指定内容地图
     * @param path 地图路径
     * @param content 地图内容
     * @return 是否创建成功
     */
    public boolean createMapWithContent(String path, String content) {
        try {
            File file = new File(path);
            if (file.exists()) {
                System.out.println("MapManager File already exists: " + path);
                return false;
            }
            Files.createDirectories(file.getParentFile().toPath());
            Files.writeString(file.toPath(), content, StandardOpenOption.CREATE_NEW);
            return true;
        } catch (IOException e) {
            System.out.println("MapManager Error creating map file with content: " + path + " - " + e.getMessage());
            return false;
        }
    }
}
