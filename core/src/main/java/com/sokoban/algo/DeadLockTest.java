package com.sokoban.algo;

import java.util.Arrays;
import java.util.Set;

import com.sokoban.core.logic.ObjectType;
import com.sokoban.core.logic.PlayerCoreUtils;
import com.sokoban.core.map.SubMapData;

/**
 * 地图死锁检测算法
 * <br><br>
 * 算法概述详见文档
 * @author Life_Checkpoint
 */
public class DeadLockTest {

    /**
     * 检测地图对应位置是否为角落死锁
     * @param subMap 子地图
     * @param x
     * @param y
     * @return 是否为角落死锁
     */
    public static boolean cornerLockTest(SubMapData subMap, int x, int y, boolean boxAsAir) {
        ObjectType[][] objectLayer = subMap.getObjectLayer();
        if (subMap.getTargetLayer()[y][x] == ObjectType.BoxTarget) return false;

        int[][] deltaPoses = new int[][] {{0, 1}, {1, 0}, {0, -1}, {-1, 0}};
        boolean[] lockPos = new boolean[4];
        for (int directionIndex = 0; directionIndex < 4; directionIndex++) {
            int[] deltaPos = deltaPoses[directionIndex];
            int newX = x + deltaPos[0], newY = y + deltaPos[1];
            
            boolean boxLock = (!boxAsAir) && PlayerCoreUtils.isBox(objectLayer[newY][newX]); // 锁箱判定：不将箱子视为空气，且确实存在箱子
            boolean wallLock = PlayerCoreUtils.isWalkable(objectLayer[newY][newX]) && PlayerCoreUtils.isBox(objectLayer[newY][newX]); // 锁墙判定：存在空气、箱子以外的物体
            lockPos[directionIndex] = boxLock || wallLock;
        }

        return (lockPos[0] && lockPos[1]) || (lockPos[1] && lockPos[2]) || (lockPos[2] && lockPos[3]) || (lockPos[3] && lockPos[0]);
    }

    /**
     * 获得连通块标记
     * @param subMapData 子地图
     * @param boxAsAir 将箱子视为空气
     * @return 连通块标记数组，相同的块会被标记为相同 > 0 数字，非空块将被标记为 0
     */
    public static int[][] getConnectedLabel(SubMapData subMap, boolean boxAsAir) {
        int[][] labels = new int[subMap.height][subMap.width];
        ObjectType[][] objectLayer = subMap.getObjectLayer();        
        for (int i = 0; i < labels.length; i++) Arrays.fill(labels[i], -1); // 全部初始化为 -1

        int currentTag = 1;
        for (int y = 0; y < subMap.height; y++) {
            for (int x = 0; x < subMap.width; x++) {

                // 为所有还未被染色的连通块染色
                if (labels[y][x] == -1) {
                    labeling(labels, objectLayer, currentTag, x, y, boxAsAir);
                    currentTag += 1;
                }
            }
        }

        return labels;
    }

    /**
     * 对连通块标记进行拓展
     * @param labels 连通块数组
     * @param labelValue 当前连通块标记值
     */
    private static void labeling(int[][] labels, ObjectType[][] objectMap, int labelValue, int x, int y, boolean boxAsAir) {
        int[][] deltaPoses = new int[][] {{0, 1}, {0, -1}, {-1, 0}, {1, 0}};
        
        for (int[] deltaPos : deltaPoses) {
            int newX = x + deltaPos[0], newY = y + deltaPos[1];

            if (outRange(objectMap, newX, newY) || labels[newY][newX] != -1) continue; // 越界或者已被标记，跳过

            if (PlayerCoreUtils.isWalkable(objectMap[newY][newX]) || (boxAsAir && PlayerCoreUtils.isBox(objectMap[newY][newX]))) { // 空快
                labels[newY][newX] = labels[y][x]; // 对节点进行染色
                labeling(labels, objectMap, labelValue, newX, newY, boxAsAir); // 拓展标记
            } else {
                labels[newY][newX] = 0; // 非空块
            }
        }
    }

    private static <T> boolean outRange(T[][] array, int x, int y) {
        return y < 0 || x < 0 || y >= array.length || x >= array[0].length; 
    }

    /**
     * 判断地图中是否存在死锁箱
     * @param subMap 子地图
     * @return 是否存在死锁
     */
    public static boolean lockTest(SubMapData subMap) {
        ObjectType[][] objectLayer = subMap.getObjectLayer();
        for (int y = 0; y < subMap.height; y++) {
            for (int x = 0; x < subMap.width; x++) {
                if (PlayerCoreUtils.isBox(objectLayer[y][x])) {
                    // 死锁判断
                    if (cornerLockTest(subMap, x, y, false)) return true; // 角落死锁
                }
            }
        }
        return false;
    }

    /**
     * 判断地图中是否存在死锁箱
     * @param subMap 子地图
     * @return 是否存在死锁
     */
    public static boolean lockTest(SubMapData subMap, Set<int[]> boxesPosition) {
        for (int[] poses : boxesPosition) if (cornerLockTest(subMap, poses[0], poses[1], false)) {
            return true; // 角落死锁
        }
        return false;
    } 
}
