package com.sokoban;

/**
 * 地图符号字串与整数的映射类
 */
public class MapSignMapping {
    // 地图元素字串常量
    public static final char WALL_C = '#';
    public static final char PLAYER_C = 'P';
    public static final char BOX_C = 'B';
    public static final char TARGET_C = '_';
    public static final char BOX_ON_TARGET_C = '@';
    public static final char PLAYER_ON_TARGET_C = '$';
    public static final char EMPTY_C = ' ';

    // 移动方向字串常量
    public static final char UP_C = 'U';
    public static final char DOWN_C = 'D';
    public static final char LEFT_C = 'L';
    public static final char RIGHT_C = 'R';

    /**
     * 检查单个字符是否为有效的地图字符
     * 
     * @param ch 要检查的字符
     * @return 是否是有效的地图字符
     */
    public static boolean isValidMapChar(char ch) {
        return switch (ch) {
            case WALL_C, PLAYER_C, BOX_C, TARGET_C, BOX_ON_TARGET_C, PLAYER_ON_TARGET_C, EMPTY_C -> true;
            default -> false;
        };
    }

    /**
     * 检查单个字符是否为有效的行动字符
     * 
     * @param ch 要检查的字符
     * @return 是否是有效的地图字符
     */
    public static boolean isValidMoveChar(char ch) {
        return switch (ch) {
            case UP_C, DOWN_C, LEFT_C, RIGHT_C -> true;
            default -> false;
        };
    }
}
