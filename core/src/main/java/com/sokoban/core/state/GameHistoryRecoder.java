package com.sokoban.core.state;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.sokoban.core.game.GameParams;
import com.sokoban.core.json.JsonManager;
import com.sokoban.core.map.MoveListParser;

/**
 * 游戏历史记录
 * <br><br>
 * @author Life_Checkpoint
 * 记录了每一帧游戏
 */
public class GameHistoryRecoder {
    private GameParams gameParams;
    private List<GameStateFrame> stateFrame;

    public GameHistoryRecoder() {
        gameParams = new GameParams();
        stateFrame = new ArrayList<>();
    }

    public GameHistoryRecoder(GameParams gameParams) {
        this.gameParams = gameParams;
        this.stateFrame = new ArrayList<>();
    }

    /**
     * 获得总帧数
     * @return 总帧数
     */
    public int getTotalFrameNum() {
        return stateFrame.size();
    }

    /**
     * 添加新帧
     * @param frame 新游戏帧
     */
    public void addNewFrame(GameStateFrame frame) {
        stateFrame.add(frame);
    }

    /**
     * 返回指定 step 对应的游戏帧
     * @param step 游戏步
     * @return 对应游戏帧，失败返回 null
     */
    public GameStateFrame getStepFrame(int step) {
        GameStateFrame targetFrame = new GameStateFrame();
        targetFrame.stepCount = step;

        // 需要保证有序
        int index = Collections.binarySearch(stateFrame, targetFrame, Comparator.comparingInt((frame) -> frame.stepCount));
        return index >= 0 ? stateFrame.get(index) : null;
    }

    /**
     * 进行一次回退，保留当前时间
     * @return 回退是否成功
     */
    public boolean undo() {
        if (stateFrame.size() <= 1) return false;

        // 删除最新记录
        stateFrame.removeLast();
        GameStateFrame UndoFrame = stateFrame.getLast().deepCopy(); // 深复制前一步
        
        /*
         * 在 UndoFrame 中，
         * action -> 回到前一步，不需要变更，保持一致性
         * frameTime -> 记录时间更新
         * mapData -> 回到前一步，不需要变更
         * moves -> 回到前一步，要进行反演变换
         * stepCount -> 撤去步的新增步数已被删除，所以需要 += 2
         */

        UndoFrame.frameTime = LocalDateTime.now();
        UndoFrame.moves = MoveListParser.inverseMoves(UndoFrame.moves);
        UndoFrame.stepCount += 2;

        stateFrame.add(UndoFrame);
        return true;
    }

    /**
     * 获得当前所有帧的总相差时间
     * @return 相差时间, ms
     */
    public long getTotalTime() {
        if (stateFrame.isEmpty() || stateFrame.size() == 1) return 0;
        LocalDateTime startTime = stateFrame.getFirst().frameTime;
        LocalDateTime endTime = stateFrame.getLast().frameTime;
        return Duration.between(startTime, endTime).toMillis();
    }

    public GameStateFrame getLast() {
        return stateFrame.getLast();
    }

    public GameParams getGameParams() {
        return gameParams;
    }

    public void setGameParams(GameParams gameParams) {
        this.gameParams = gameParams;
    }

    public List<GameStateFrame> getStateFrame() {
        return stateFrame;
    }

    public void setStateFrame(List<GameStateFrame> stateFrame) {
        this.stateFrame = stateFrame;
    }

    @Override
    public String toString() {
        return new JsonManager().getJsonString(this); // 返回序列化得到的数据
    }
}
