package com.sokoban.core;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Logger 类
 * @author ChatGPT
 */
public class Logger {
    // 日志等级
    public enum LogLevel {
        INFO, WARNING, ERROR, DEBUG
    }

    // ANSI 转义序列
    private static final String RESET = "\u001B[0m";
    private static final String COLOR_INFO = "\u001B[32m"; // 绿色
    private static final String COLOR_WARNING = "\u001B[33m"; // 黄色
    private static final String COLOR_ERROR = "\u001B[31m"; // 红色
    private static final String COLOR_DEBUG = "\u001B[34m"; // 蓝色

    // 当前日志等级
    public static LogLevel loggerLevel = LogLevel.DEBUG;

    /** 
     * 主日志输出
     * @param level 日志等级
     * @param moduleName 模块名称
     * @param message 日志信息 
     */
    public static void log(LogLevel level, String moduleName, String message) {
        if (!screenLevel(level)) return;
        String shortTime = getShortTime();
        String color = getColorForLevel(level);
        System.out.println(color + String.format("[%s] [%s] [%s] %s", level, moduleName, shortTime, message) + RESET);
    }

    /** 
     * 主日志输出
     * @param level 日志等级
     * @param message 日志信息 
     */
    public static void log(LogLevel level, String message) {
        if (!screenLevel(level)) return;
        String shortTime = getShortTime();
        String color = getColorForLevel(level);
        System.out.println(color + String.format("[%s] [%s] %s", level, shortTime, message) + RESET);
    }

    /**
     * 日志等级过滤
     * @param level 当前日志等级
     * @return 是否应被输出
     */
    public static boolean screenLevel(LogLevel level) {
        if ((level == LogLevel.DEBUG) && loggerLevel == LogLevel.INFO) return false;
        if ((level == LogLevel.DEBUG || level == LogLevel.INFO) && loggerLevel == LogLevel.WARNING) return false;
        if ((level == LogLevel.DEBUG || level == LogLevel.INFO || level == LogLevel.WARNING) && loggerLevel == LogLevel.ERROR) return false;
        return true;
    }

    public static void info(String moduleName, String message) {
        log(LogLevel.INFO, moduleName, message);
    }

    public static void info(String message) {
        log(LogLevel.INFO, message);
    }

    public static void warning(String moduleName, String message) {
        log(LogLevel.WARNING, moduleName, message);
    }

    public static void warning(String message) {
        log(LogLevel.WARNING, message);
    }

    public static void error(String moduleName, String message) {
        log(LogLevel.ERROR, moduleName, message);
    }

    public static void error(String message) {
        log(LogLevel.ERROR, message);
    }

    public static void debug(String moduleName, String message) {
        log(LogLevel.DEBUG, moduleName, message);
    }

    public static void debug(String message) {
        log(LogLevel.DEBUG, message);
    }

    /** 
     * 获取短时间格式 
     * @return 短文本格式当前时间
     */
    private static String getShortTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        return formatter.format(new Date());
    }

    /**
     * 获取日志等级对应颜色
     * @param level 日志等级
     * @return 颜色 ANSI 转义字符
     */
    private static String getColorForLevel(LogLevel level) {
        switch (level) {
            case INFO:
                return COLOR_INFO;
            case WARNING:
                return COLOR_WARNING;
            case ERROR:
                return COLOR_ERROR;
            case DEBUG:
                return COLOR_DEBUG;
            default:
                return RESET;
        }
    }
}
