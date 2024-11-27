package com.sokoban.utils;

import com.sokoban.core.Logger;

/**
 * 原生系统文件选择器
 * @author Claude
 * @author ChatGPT
 * @author Life_Checkpoint
 */
public class WindowsFileChooser {
    static {
        try {
            // 加载链文件选择对话框链接库
            System.load(System.getProperty("user.dir") + "/cpp/windows/com/sokoban/utils/file_dialog/file_dialog.dll");
        } catch (Error e) {
            Logger.error("WindowsFileChooser", String.format("CRASHED when loads DLL lib %s, because %s", "file_dialog.dll", e.getMessage()));
            // 无法捕获，直接退出
        }
    }

    public static native String openFileChooser(String extensions);
    public static native String saveFileChooser(String extensions);
    
    /**
     * 打开文件
     * @param extensions 拓展名，以 | 作为分隔符
     * @return 选择的文件路径
     */
    public static String selectFile(String extensions) {
        try {
            return openFileChooser(extensions);
        } catch (Exception e) {
            Logger.error("WindowsFileChoosr", "Select file failed: " + e.getMessage());
            return null;
        }
    }

    /**
     * 保存文件
     * @param extensions 拓展名，以 | 作为分隔符
     * @return 选择的文件路径
     */
    public static String saveFile(String extensions) {
        try {
            return saveFileChooser(extensions);
        } catch (Exception e) {
            Logger.error("WindowsFileChooser", "Save file failed: " + e.getMessage());
            return null;
        }
    }
}