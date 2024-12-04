package com.sokoban.core.logic;

import java.util.ArrayList;
import java.util.List;

import com.sokoban.core.game.Logger;
import com.sokoban.core.map.MapData;
import com.sokoban.core.map.SubMapData;

/**
 * 推箱子逻辑核心类
 * @author StiCK-bot
 * @author Life_Checkpoint
 */
public class PlayerCore {
    private Pos playerPos; // 玩家坐标
    private List<String> moveList; // 这一轮有哪些物块坐标发生了移动
    private MapData map; // 游戏地图

    /*
     * 需要注意的是数组维度的访问顺序
     * x, y - 代表横、纵坐标，访问是先 y 后 x
     * width, height - 代表宽、高，访问是先 height 后 width
     */

    /**
     * 逻辑核心构造
     */
    public PlayerCore() {
        moveList = new ArrayList<>();
        map = new MapData();
        playerPos = null;
    }
    
    /** 获得索引对应子地图 */
    public SubMapData getSubmap(int index) {
        if (index < 0 || index >= map.allMaps.size()) {
            Logger.error("PlayerCore", String.format("Can't get sub map. Index expected [0, %d], get %d", map.allMaps.size(), index));
            return null;
        }
        return map.allMaps.get(index);
    }
    
    /** 获得子地图对应位置物体 */
    public ObjectType getObject(int subMapIndex, int x, int y) {
        if (outOfBound(subMapIndex, x, y)) {
            Logger.error("PlayerCore", String.format("Read at a out-of-bound position (%d, %d)", x, y));
            return ObjectType.Unknown;
        }
        return getSubmap(subMapIndex).getObjectLayer()[y][x];
    }

    /**
     * 在指定子地图中寻找当前地图中玩家位置并更新
     * @return 玩家位置，未找到返回 null
     */
    public Pos findPlayerPosition(int subMapIndex) {
        SubMapData subMap = getSubmap(subMapIndex);
        Pos playerPos = null;

        // 检查每一个位置
        for (int y = 0; y < subMap.height; y++) {
            for (int x = 0; x < subMap.width; x++) {

                // 当前位置为玩家位置
                if (getObject(subMapIndex, x, y) == ObjectType.Player) {
                    if (playerPos == null) {
                        // 首次找到玩家
                        playerPos = new Pos(x, y);
                    } else {
                        // 多次找到玩家，以首次找到为准
                        Logger.warning("PlayerCore", "Find more than one player. Please Check your map");
                    }
                }
            }
        }

        return playerPos;
    }

    /** 获得子地图对应位置目标 */
    public ObjectType getTarget(int subMapIndex, int x, int y) {
        if (outOfBound(subMapIndex, x, y)) {
            Logger.error("PlayerCore", String.format("Read at a out-of-bound position (%d, %d)", x, y));
            return ObjectType.Unknown;
        }
        return getSubmap(subMapIndex).getTargetLayer()[y][x];
    }
    
    /** 设置子地图对应位置物体 */
    public void setObject(int subMapIndex, int x, int y, ObjectType object) {
        if (outOfBound(subMapIndex, x, y)) {
            Logger.error("PlayerCore", String.format("Write at a out-of-bound position (%d, %d)", x, y));
            return;
        }
        getSubmap(subMapIndex).getObjectLayer()[y][x] = object;
    }
    
    /** 判断坐标是否超出子地图边界 */
    public boolean outOfBound(int subMapIndex, int x, int y) {
        return x < 0 || y < 0 || getSubmap(subMapIndex).width <= x || getSubmap(subMapIndex).height <= y;
    }
    
    /**
     * 判断指定方向上的物块是否能连续推动
     * @param subMapIndex 子地图索引
     * @param position 当前物块初始位置
     * @param direction 试图推动的方向
     * @return 是否能连续推动
     */
    public boolean canPush(int subMapIndex, Pos position, Direction direction) {
        // 获得新坐标
        Pos nextPos = position.add(PlayerCoreUtils.getDeltaPos(direction));
        
        // 获得当前物体和下一物体
        ObjectType thisObj = getObject(subMapIndex, position.x, position.y);
        ObjectType nextObj = getObject(subMapIndex, nextPos.x, nextPos.y);
        
        // 如果当前物体不是箱子，不可以推
        if (!PlayerCoreUtils.isBox(thisObj)) return false;
        
        // 如果下一个物体是墙或者超边界，不可以推
        if (PlayerCoreUtils.isWall(nextObj) || outOfBound(subMapIndex, nextPos.x, nextPos.y)) return false;
        
        // 如果下一个物体是箱子，则需要通过递归连锁判断
        if (PlayerCoreUtils.isBox(nextObj)) return canPush(subMapIndex, nextPos, direction);
        
        // 如果下一个物体是空的，可以推
        if (PlayerCoreUtils.isWalkable(nextObj)) return true;
        
        // 如果是其它情况，发出警告（判断可能漏掉了一些物块类型）
        Logger.warning("PlayerCore", "method canPush can't identify object type " + nextObj);
        return false;
    }
    
    /**
     * 对所在物块进行一次指定方向的推动（包括玩家）
     * <br><br>
     * 如果是新一轮移动，应该重置 moveList
     * @param subMapIndex 子地图索引
     * @param position 当前物块位置
     * @param direction 推动方向
     */
    public void doPush(int subMapIndex, Pos position, Direction direction) {
        Logger.debug("PlayerCore", String.format(
            "doPush -> subMapIndex = %d, position = (%d, %d), direction = %s", 
            subMapIndex, position.x, position.y, direction
        ));

        // 获得新坐标
        Pos nextPos = position.add(PlayerCoreUtils.getDeltaPos(direction));
        
        // 获得当前物体和下一物体
        ObjectType thisObj = getObject(subMapIndex, position.x, position.y);
        ObjectType nextObj = getObject(subMapIndex, nextPos.x, nextPos.y);
        
        // 如果当前物块是玩家
        if (PlayerCoreUtils.isPlayer(thisObj)) {
            Logger.debug("PlayerCore", "Player moving");

            // 下一物块为空气，直接进入
            if (PlayerCoreUtils.isWalkable(nextObj)) {
                setObject(subMapIndex, position.x, position.y, ObjectType.Air); // 当前坐标换为空气
                setObject(subMapIndex, nextPos.x, nextPos.y, ObjectType.Player); // 下一坐标换为玩家
                playerPos = nextPos;

                moveList.add(PlayerCoreUtils.toObjectMoveString(subMapIndex, position.x, position.y, nextPos.x, nextPos.y)); // 将玩家移动的信息加入到位移列表
                return;
            }

            // 下一物块为箱子，且连续推动的检验通过，需要进行多个物块的移动
            if (PlayerCoreUtils.isBox(nextObj) && canPush(subMapIndex, nextPos, direction)) {

                // 要进行多个物块的连续推动，但是物块之间的状态不能互相影响，所以需要复制一张新地图，更改完成后才覆盖旧地图
                // 复制的时候要调用 SubMapData 的 cpy 方法，否则会得到对同一个对象的引用
                SubMapData newMap = getSubmap(subMapIndex).cpy();
                
                // 为了防止栈内存溢出，使用循环实现
                Pos currentPos = new Pos(position.x, position.y); // 当前处理物块的坐标
                Pos currentPosNext = currentPos.add(PlayerCoreUtils.getDeltaPos(direction)); // 当前处理物块的目标坐标

                // 如果当前物块在旧地图中不是空气（未到达结束条件）
                while (!PlayerCoreUtils.isWalkable(getObject(subMapIndex, currentPos.x, currentPos.y))) {

                    // 将旧地图对应物块移动到新地图，注意坐标顺序
                    newMap.getObjectLayer()[currentPosNext.y][currentPosNext.x] = getObject(subMapIndex, currentPos.x, currentPos.y);

                    // 将物体移动的信息加入到位移列表
                    moveList.add(PlayerCoreUtils.toObjectMoveString(subMapIndex, currentPos.x, currentPos.y, currentPosNext.x, currentPosNext.y));

                    // 更新坐标
                    currentPos = new Pos(currentPosNext.x, currentPosNext.y);
                    currentPosNext = currentPos.add(PlayerCoreUtils.getDeltaPos(direction));
                }

                // 最后去除最开始的玩家位置
                newMap.getObjectLayer()[position.y][position.x] = ObjectType.Air;
                playerPos = nextPos;

                // 新地图覆盖旧地图
                map.allMaps.set(subMapIndex, newMap);
                return;
            }
        }

        // 如果当前物块是箱子（箱子自主移动，暂时无需开发）
    }

    /**
     * 检查游戏是否达到胜利条件
     * @return 是否胜利
     */
    public boolean isGameWin() {
        // 如果所有的子地图目标点都被覆盖了对应物体，即可判定胜利
        for (int subMapIndex = 0; subMapIndex < map.allMaps.size(); subMapIndex++) {
            SubMapData subMap = getSubmap(subMapIndex);

            for (int y = 0; y < subMap.height; y++) {
                for (int x = 0; x < subMap.width; x++) {

                    // 检查箱子目标点
                    if (getTarget(subMapIndex, x, y) == ObjectType.BoxTarget)
                        if (!PlayerCoreUtils.isBox(getObject(subMapIndex, x, y))) return false;

                    // 检查玩家目标点
                    if (getTarget(subMapIndex, x, y) == ObjectType.PlayerTarget)
                        if (!PlayerCoreUtils.isPlayer(getObject(subMapIndex, x, y))) return false;
                }
            }
        }

        // 所有检查通过后返回成功
        Logger.debug("PlayerCore", "Checking succeed passed.");
        return true;
    }

    /**
     * 操控玩家进行指定方向移动
     * @param subMapIndex 进行移动的子地图
     * @param direction 移动方向
     */
    public void move(int subMapIndex, Direction direction) {
        moveList.clear(); // 重置 moveList
        doPush(subMapIndex, playerPos, direction); // 对玩家进行移动
    }

    public MapData getMap() {
        return map;
    }

    /**
     * 设置地图
     * @param map 标准地图数据
     * @return 玩家所在子地图索引，找不到则返回 -1
     */
    public int setMap(MapData map) {
        if (map == null) {
            Logger.error("PlayerCore", "Load map failed because map is null");
            return -1;
        }

        this.map = map;

        // 尝试搜索玩家位置，并返回玩家所在的子地图
        for (int sumMapIndex = 0; sumMapIndex < map.allMaps.size(); sumMapIndex++) {
            Pos findingPlayerPos = findPlayerPosition(0);
            if (findingPlayerPos != null) {
                playerPos = findingPlayerPos;
                return sumMapIndex;
            }
        }

        // 未找到
        Logger.warning("PlayerCore", "Can't find any player in map.");
        return -1;
    }

    public Pos getPlayerPos() {
        return playerPos;
    }

    public List<String> getMoveList() {
        return moveList;
    }
}