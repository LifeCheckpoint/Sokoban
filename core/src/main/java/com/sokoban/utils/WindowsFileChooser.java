package com.sokoban.utils;

/**
 * 原生系统文件选择器
 * @author Claude
 * @author ChatGPT
 */
public class WindowsFileChooser {
    static {
        System.load(System.getProperty("user.dir") + "/cpp/windows/com/sokoban/utils/file_dialog/x64/Debug/file_dialog.dll");  // 动态库名称
    }

    public static native String openFileChooser(String[] extensions);
    
    public static String selectFile(String[] extensions) {
        return openFileChooser(extensions);
    }
}