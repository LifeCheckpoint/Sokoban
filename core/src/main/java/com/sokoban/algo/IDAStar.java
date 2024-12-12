package com.sokoban.algo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.sokoban.core.logic.Direction;
import com.sokoban.core.logic.ObjectType;
import com.sokoban.core.logic.PlayerCore;
import com.sokoban.core.logic.PlayerCoreUtils;
import com.sokoban.core.logic.Pos;
import com.sokoban.core.map.MapData;
import com.sokoban.core.map.SubMapData;

/**
 * IDA* 算法，搜索推箱子的可能解
 */
public class IDAStar {
    private MapData map; // 地图数据
    private int subMapIndex; // 子地图索引
    private SubMapData subMap, subMapWithoutBox; // 子地图
    private PlayerCore playerCore; // 逻辑核心
    private IDAState goalState = null;  // 添加成员变量存储目标状态
    private int boxNum = 0;

    // 采用邻接矩阵 + 表双用存储箱子目标点，采用表存储玩家目标点
    private Set<int[]> boxTargetList; // 箱子目标点表

    private static final int MAX = Integer.MAX_VALUE;
    private static final int LAMBDA_DEEPIN = 1;

    /** 在 IDA* 中性能高一些的状态类 */
    public static class IDAState {
        int playerX, playerY;
        Set<int[]> boxesPos;
        IDAState parent;

        IDAState(int playerX, int playerY, Set<int[]> boxesPos) {
            this(playerX, playerY, boxesPos, null);
        }

        IDAState(int playerX, int playerY, Set<int[]> boxesPos, IDAState parent) {
            this.playerX = playerX;
            this.playerY = playerY;
            this.boxesPos = boxesPos;
            this.parent = parent;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof IDAState)) return false;
            IDAState other = (IDAState) o;
            return playerX == other.playerX && 
                playerY == other.playerY && 
                boxesPos.equals(other.boxesPos);
        }

        @Override
        public int hashCode() {
            int hash = 1;
            for (int[] box : boxesPos) hash += (box[0] + 37) * box[1];
            return 17 * playerX + playerY + hash; 
        }

        @Override
        public String toString() {
            String text = String.format("Player (%d, %d)\n", playerX, playerY);
            for (int[] boxPos : boxesPos) {
                text += String.format("Box -> (%d, %d)\n", boxPos[0], boxPos[1]);
            }
            return text + "----------";
        }
    }

    /** 求解器构造 */
    public IDAStar(MapData map) {
        this.map = map;
        playerCore = new PlayerCore();
    }

    /** 曼哈顿距离 */
    public int manhattanDistance(int x1, int y1, int x2, int y2) {
        return Math.abs(x1 - x2) + Math.abs(y1 - y2);
    }

    /** 启发式损失函数 */
    private int heuristicLoss(IDAState state) {
        int loss = 10 * boxNum;

        SubMapData tempSubMap = subMapWithoutBox.deepCopy();
        for (int[] box : state.boxesPos) tempSubMap.getObjectLayer()[box[1]][box[0]] = ObjectType.Box;
        
        // 如果有箱子陷入死角，直接置为大数
        if (DeadLockTest.lockTest(tempSubMap, state.boxesPos, true)) return 114514;

        // 计算所有箱子到所有目标点的曼哈顿距离
        for (int[] box : state.boxesPos) {
            for (int[] boxTarget : boxTargetList) {
                loss += manhattanDistance(box[0], box[1], boxTarget[0], boxTarget[1]);
            }
        }

        // 如果有箱子进入割点，启发式认为这个解法不够好
        // for (int[] box : state.boxesPos)  if (new CutVertexChecker(tempSubMap).isCutVertex(box[0], box[1])) loss += 1;

        // 如果进入一个较大块的区域，认为可能是一个比较好的解法
        int areaMinus = 0;
        for (int[] box : state.boxesPos) {
            if (box[1] > 0) areaMinus += PlayerCoreUtils.isWalkable(tempSubMap.getObjectLayer()[box[1] - 1][box[0]]) ? 1 : 0;
            if (box[1] < tempSubMap.height - 1) areaMinus += PlayerCoreUtils.isWalkable(tempSubMap.getObjectLayer()[box[1] + 1][box[0]]) ? 1 : 0;
            if (box[0] > 0) areaMinus += PlayerCoreUtils.isWalkable(tempSubMap.getObjectLayer()[box[1]][box[0] - 1]) ? 1 : 0;
            if (box[0] < tempSubMap.width - 1) areaMinus += PlayerCoreUtils.isWalkable(tempSubMap.getObjectLayer()[box[1]][box[0] + 1]) ? 1 : 0;
        }
        loss -= areaMinus * 6;

        // 玩家与箱子之间的最小曼哈顿距离
        for (int[] box : state.boxesPos) loss += manhattanDistance(box[0], box[1], state.playerX, state.playerY);

        return loss;
    }

    /** 判定是否到达目标 */
    private boolean isGoal(IDAState state) {
        for (int[] box : state.boxesPos) {

            boolean onTarget = false;
            for (int[] boxTarget : boxTargetList) {
                if (box[0] == boxTarget[0] && box[1] == boxTarget[1]) {
                     // 如果检测到当前箱子在位置上，翻转标志后跳出循环
                    onTarget = true;
                    break;
                }
            }

            if (!onTarget) return false; // 任何一个不在都会判定未达目标
        }

        return true;
    }

    /** 生成路径 */
    private List<IDAState> reconstructPath(IDAState state) {
        // 重建路径
        List<IDAState> path = new ArrayList<>();
        while (state != null) {
            path.add(state);
            state = state.parent;
        }
        Collections.reverse(path);
        return path;
    }

    /** 获得初始状态 */
    private IDAState findStartState() {
        // 找到玩家位置
        subMapIndex = playerCore.setMap(map); // 设置逻辑核心地图
        Pos playerPos = playerCore.findPlayerPosition(subMapIndex);
        subMap = map.allMaps.get(subMapIndex);

        // 获得没有箱子和人的空地图
        SubMapData tempSubMap = subMap.deepCopy();
        for (int y = 0; y < tempSubMap.height; y++) for (int x = 0; x < tempSubMap.width; x++)
            if (PlayerCoreUtils.isBox(tempSubMap.getObjectLayer()[y][x]) || PlayerCoreUtils.isPlayer(tempSubMap.getObjectLayer()[y][x])) {
                boxNum += 1;
                tempSubMap.getObjectLayer()[y][x] = ObjectType.Air;
            }
        subMapWithoutBox = tempSubMap;

        // 找到箱子位置
        Set<int[]> boxes = new HashSet<>();
        subMap = map.allMaps.get(subMapIndex);
        for (int y = 0; y < subMap.height; y++) {
            for (int x = 0; x < subMap.width; x++) {
                if (PlayerCoreUtils.isBox(subMap.getObjectLayer()[y][x])) boxes.add(new int[] {x, y});
            }
        }

        // 找到目标点位置
        Set<int[]> targets = new HashSet<>();
        subMap = map.allMaps.get(subMapIndex);
        for (int y = 0; y < subMap.height; y++) {
            for (int x = 0; x < subMap.width; x++) {
                if (subMap.getTargetLayer()[y][x] == ObjectType.BoxTarget) targets.add(new int[] {x, y});
            }
        }
        boxTargetList = targets;

        IDAState initState = new IDAState(playerPos.getX(), playerPos.getY(), boxes);
        return initState;
    }

    /** 获取所有合法后继状态 */
    private List<IDAState> getSuccessors(IDAState state) {
        // 奶奶的，早知道用 C++ 实现了，这里的转换效率不知道要低多少
        List<IDAState> successors = new ArrayList<>();

        SubMapData tempSubMap = subMapWithoutBox.deepCopy();
        for (int[] box : state.boxesPos) tempSubMap.getObjectLayer()[box[1]][box[0]] = ObjectType.Box;
        tempSubMap.getObjectLayer()[state.playerY][state.playerX] = ObjectType.Player;
        
        for (Direction direction : Direction.values()) {
            if (direction == Direction.None) continue;

            Pos newPos = new Pos(state.playerX, state.playerY).add(PlayerCoreUtils.getDeltaPos(direction));
            if (!playerCore.outOfBound(subMapIndex, newPos.getX(), newPos.getY())) {
                // 找到所有合法行动

                // 空
                if (PlayerCoreUtils.isWalkable(tempSubMap.getObjectLayer()[newPos.getY()][newPos.getX()])) {
                    successors.add(new IDAState(newPos.getX(), newPos.getY(), state.boxesPos));
                }

                // 有箱子
                if (PlayerCoreUtils.isBox(tempSubMap.getObjectLayer()[newPos.getY()][newPos.getX()])) {
                    // 如果能推动
                    Pos newBoxPos = newPos.add(PlayerCoreUtils.getDeltaPos(direction));
                    if (PlayerCoreUtils.isWalkable(tempSubMap.getObjectLayer()[newBoxPos.getY()][newBoxPos.getX()])) {
                        // 复制箱子位置集并更新对应箱子坐标
                        Set<int[]> newBoxPoses = new HashSet<>();
                        for (int[] bp : state.boxesPos) {
                            if (bp[0] == newPos.getX() && bp[1] == newPos.getY()) newBoxPoses.add(new int[] {newBoxPos.getX(), newBoxPos.getY()});
                            else newBoxPoses.add(bp);
                        }
                        successors.add(new IDAState(newPos.getX(), newPos.getY(), newBoxPoses));
                    }
                }
            }
        }

        // 返回所有合法状态
        return successors;
    }

    /** IDA* 深度限定搜索 */
    private int depthLimitedSearch(IDAState state, int g, int threshold, Set<IDAState> visited) {
        // 添加到已访问节点
        if (visited.contains(state)) return Integer.MAX_VALUE;
        visited.add(state);
        
        int f = g + heuristicLoss(state); // 计算当前 loss

        // 已经大于阈值，回溯
        if (f > threshold) {
            return f;
        }

        if (isGoal(state)) {
            goalState = state;
            return -1;
        }

        // 找到了目标
        if (isGoal(state)) {
            return -1; // 找到目标
        }

        // 找到拓展节点的最小阈值
        int minThreshold = Integer.MAX_VALUE;
        for (IDAState nextState : getSuccessors(state)) { // 获取所有后继合法节点

            // 重要：设置父节点引用
            nextState.parent = state;

            int tempThreshold = depthLimitedSearch(nextState, g + LAMBDA_DEEPIN, threshold, visited); // 进行搜索

            if (tempThreshold == -1) { // 找到了目标
                return -1;
            }

            minThreshold = Math.min(minThreshold, tempThreshold);
        }

        if (Math.random() < 0.00001) System.out.println(minThreshold);
        return minThreshold; // 返回最小阈值
    }

    /** IDA 算法核心实现 */
    private List<IDAState> IDAStarFind(IDAState startState) {
        int threshold = heuristicLoss(startState);
        while (true) {
            int tempThreshold = depthLimitedSearch(startState, 0, threshold, new HashSet<>());
            if (tempThreshold == -1) {
                return reconstructPath(goalState);
            } else if (tempThreshold == Integer.MAX_VALUE) {
                return null; // No solution
            }
            threshold = tempThreshold;
        }
    }

    /** 使用 IDA* 搜索解 */
    public List<IDAState> solve() {
        // 找到初始玩家和箱子位置
        IDAState startState = findStartState();
        // 调用 IDA* 算法
        List<IDAState> solution = IDAStarFind(startState);
        return solution;
    }
}

// 评价是来世记得用 C++