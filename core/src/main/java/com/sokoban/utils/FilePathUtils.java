package com.sokoban.utils;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import com.sokoban.core.game.FileInfo;
import com.sokoban.core.game.Logger;

/**
 * 文件路径增效工具
 * @author Life_Checkpoint
 */
public class FilePathUtils {

    /**
     * 判断文件或目录路径存在性
     * @param path 文件或目录路径
     * @return 是否存在
     */
    public static boolean exists(String path) {
        try {
            return new File(path).exists();
        } catch (Exception e) {
            Logger.error("FilePathUtils", String.format("Can't determine whether path %s exist because %s", path, e.getMessage()));
            return false;
        }
    }

    /**
     * 创建目录
     * @param path 目录路径
     * @return 是否成功
     */
    public static boolean createDirectories(String path) {
        try {
            return new File(path).mkdirs();
        } catch (Exception e) {
            Logger.error("FilePathUtils", String.format("Can't create path %s because %s", path, e.getMessage()));
            return false;
        }
    }

    /**
     * 删除文件或目录
     * @param path 文件或目录路径
     * @return 是否成功
     */
    public static boolean delete(String path) {
        return new File(path).delete();
    }

    /**
     * 组合多个路径片段为一个完整路径
     * @param paths 路径片段
     * @return 合并后的路径字符串，失败返回 null
     */
    public static String combine(String... paths) {
        if (paths == null || paths.length == 0) {
            Logger.error("FilePathUtils", "Paths to combine cannot be null or empty.");
            return null;
        }

        // 使用 Paths.get 动态拼接路径
        Path combinedPath = Paths.get(paths[0]);
        for (int i = 1; i < paths.length; i++) {
            combinedPath = combinedPath.resolve(paths[i]);
        }

        // 转为系统兼容的路径字符串
        return combinedPath.toString();
    }

    /**
     * 文件遍历
     * @param path 遍历路径
     * @param onlyFile 是否只遍历文件
     * @return List<FileInfo> 类型遍历结果，失败或空目录返回空 List
     */
    public static List<FileInfo> walk(String path, boolean onlyFile) {
        List<FileInfo> filesList = new ArrayList<>();

        try {
            File mapsDir = new File(path);

            // 地图目录不存在
            if (!mapsDir.exists() || !mapsDir.isDirectory()) {
                Logger.error("FilePathUtils", "walking path " + path + " is not exist");
                return filesList;
            }

            // 搜索文件
            Files.walk(mapsDir.toPath())
                .filter(onlyFile ? Files::isRegularFile : (f -> true))
                .forEach(filePath -> {
                    File file = filePath.toFile();
                    FileInfo fileInfo = new FileInfo();

                    fileInfo.file = file;
                    fileInfo.relativePath = mapsDir.toPath().relativize(filePath.getParent()).toString();
                    fileInfo.fileName = file.getName();

                    filesList.add(fileInfo);
                });

            return filesList;

        } catch (Exception e) {
            Logger.error("FilePathUtils", "Can't listing files because " + e.getMessage());
            return new ArrayList<>();
        }
    }
}
