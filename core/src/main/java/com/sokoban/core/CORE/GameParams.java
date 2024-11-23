package com.sokoban.core.CORE;

/**
 * 游戏参数类，传递一些游戏参数
 * @author Life_Checkpoint
 */
public class GameParams {
    /** 是否为竞速（计时 / 计步）模式 */
    public boolean racing;
    /** 彩蛋 */
    public boolean colorfulEgg;

    public GameParams() {
        racing = false;
        colorfulEgg = false;
    }
}
