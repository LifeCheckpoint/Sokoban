package com.sokoban.algo;

import com.sokoban.core.logic.ObjectType;
import com.sokoban.core.map.SubMapData;

/**
 * 推算地图中的割点存在性
 * @author Life_Checkpoint
 * @author Claude
 */
public class CutVertexChecker {
    private static final int[] DX = {-1, 1, 0, 0}; // 上下左右移动的X偏移
    private static final int[] DY = {0, 0, -1, 1}; // 上下左右移动的Y偏移

    private SubMapData subMap;
    private int rows;
    private int cols;
    private int time; // DFS访问时间戳
    private int[][] dfn; // DFS序号
    private int[][] low; // 低位值，最小可达祖先编号
    private boolean[][] visited;
    private boolean[][] isCutPoint; // 标记割点

    /**
     * 割点存在性检测类构造
     * @param gameMap ObjectType 地图
     */
    public CutVertexChecker(SubMapData subMap) {
        this.subMap = subMap;
        this.rows = subMap.height;
        this.cols = subMap.width;
        this.dfn = new int[rows][cols];
        this.low = new int[rows][cols];
        this.visited = new boolean[rows][cols];
        this.time = 0;
        this.isCutPoint = new boolean[rows][cols];
    }

    /**
     * 判断某个位置的箱子是否是割点
     * @param y
     * @param x
     * @return 是否为割点
     */
    public boolean isCutVertex(int x, int y) {
        // 吐槽一下，AI 写的时候把坐标弄反了，请留意下面的坐标表述

        // 确保空地
        if (subMap.getObjectLayer()[y][x] != ObjectType.Air) {
            return false;
        }

        // 重置状态
        time = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                visited[i][j] = false;
                dfn[i][j] = 0;
                low[i][j] = 0;
                isCutPoint[i][j] = false;
            }
        }

        // 从箱子周围的一个可达点开始DFS
        int startY = -1, startX = -1;
        for (int i = 0; i < 4; i++) {
            int ny = y + DY[i];
            int nx = x + DX[i];
            if (isValid(ny, nx) && subMap.getObjectLayer()[ny][nx] == ObjectType.Air) {
                startY = ny;
                startX = nx;
                break;
            }
        }

        if (startY == -1) return false; // 箱子周围没有可达点

        // 从起点开始 DFS
        dfs(startY, startX, y, x, -1, -1);

        // 检查移除箱子后是否存在不连通的区域
        int connectedComponents = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (!visited[i][j] && subMap.getObjectLayer()[i][j] == ObjectType.Air && !(i == y && j == x)) {
                    connectedComponents++;
                }
            }
        }

        return connectedComponents > 0; // 连通分量大于零
    }

    /** 
     * DFS 遍历并计算 low 低位值
     */
    private void dfs(int y, int x, int boxY, int boxX, int parentY, int parentX) {
        visited[y][x] = true;
        dfn[y][x] = low[y][x] = ++time;
        int children = 0; // 子节点数量

        for (int i = 0; i < 4; i++) {
            int nx = y + DX[i];
            int ny = x + DY[i];

            // 跳过箱子位置和非法位置
            if (!isValid(nx, ny) || subMap.getObjectLayer()[nx][ny] != ObjectType.Air || (nx == boxY && ny == boxX)) continue;

            // 跳过父节点
            if (nx == parentY && ny == parentX) continue; 

            if (!visited[nx][ny]) {
                children++;
                dfs(nx, ny, boxY, boxX, y, x);
                low[y][x] = Math.min(low[y][x], low[nx][ny]);

                // 对于非根节点，判断是否为割点
                if (parentY != -1 && low[nx][ny] >= dfn[y][x]) {
                    isCutPoint[y][x] = true;
                }
            } else {
                low[y][x] = Math.min(low[y][x], dfn[nx][ny]);
            }
        }

        // 对于根节点，判断是否是割点
        if (parentY == -1 && children > 1) {
            isCutPoint[y][x] = true;
        }
    }

    /** 
     * 检查坐标是否有效
     */
    private boolean isValid(int x, int y) {
        return x >= 0 && x < rows && y >= 0 && y < cols;
    }
}
