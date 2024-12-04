package com.sokoban.core.map;

import com.sokoban.core.game.FileInfo;
import com.sokoban.core.game.Logger;
import com.sokoban.utils.FilePathUtils;

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

    private final String DEFAULT_MAPS_DIRECTORY = "./bin/maps";
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
    public List<MapFileInfo> listAllMaps() {
        List<MapFileInfo> mapList = new ArrayList<>();
        List<FileInfo> fileInfos = FilePathUtils.walk(mapsDirectory, true);

        for (FileInfo fileInfo : fileInfos) {
            if (fileInfo.fileName.endsWith(MAP_FILE_EXTENSION)) {
                String mapName = fileInfo.fileName.replace(MAP_FILE_EXTENSION, "");
                mapList.add(new MapFileInfo(fileInfo.file.getAbsolutePath(), fileInfo.relativePath, mapName));
            }
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
        String mapPath = FilePathUtils.combine(mapsDirectory, levelName, mapName + MAP_FILE_EXTENSION);
        return readMapByPath(mapPath);
    }

    /**
     * 从路径读取地图内容
     * @param path 地图路径
     * @return 地图内容，失败返回 null
     */
    public String readMapByPath(String path) {
        if (!FilePathUtils.exists(path)) {
            Logger.error("MapFileReader", "File does not exist: " + path);
            return null;
        }

        try {
            return Files.readString(Path.of(path));
        } catch (IOException e) {
            Logger.error("MapFileReader", "Error reading map file: " + path + " - " + e.getMessage());
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
        // if (FilePathUtils.exists(path)) {
        //     Logger.error("MapFileReader", "File already exists: " + path);
        //     return false;
        // }

        try {
            String parentDirectory = Path.of(path).getParent().toString();
            FilePathUtils.createDirectories(parentDirectory);
            Files.writeString(
                Path.of(path), 
                content, 
                StandardOpenOption.CREATE,             // 如果文件不存在则创建
                StandardOpenOption.TRUNCATE_EXISTING   // 如果文件存在则清空
            );
            return true;
        } catch (IOException e) {
            Logger.error("MapFileReader", "Error creating map file with content: " + path + " - " + e.getMessage());
            return false;
        }
    }
}
