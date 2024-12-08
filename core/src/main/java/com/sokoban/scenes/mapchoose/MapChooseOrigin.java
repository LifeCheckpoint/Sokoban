package com.sokoban.scenes.mapchoose;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.sokoban.Main;
import com.sokoban.core.map.MapData;
import com.sokoban.core.map.gamedefault.SokobanLevels;
import com.sokoban.core.map.gamedefault.SokobanMaps;
import com.sokoban.core.user.SaveArchiveInfo.MapStatue;
import com.sokoban.polygon.BoxObject;
import com.sokoban.polygon.BoxObject.BoxType;
import com.sokoban.polygon.TimerClock;
import com.sokoban.polygon.actioninterface.ClockEndCallback;
import com.sokoban.polygon.combine.Stack3DGirdWorld;
import com.sokoban.polygon.manager.OverlappingManager;
import com.sokoban.polygon.manager.OverlappingManager.OverlapStatue;

public class MapChooseOrigin extends MapChooseScene {
    public MapChooseOrigin(Main gameMain, SokobanLevels levelEnum) {
        super(gameMain, levelEnum);
        this.level = levelEnum;
    }

    /**
     * 关卡 origin 初始化
     * @param levelChoosingMap 关卡选择地图
     */
    public void setupLevel(MapData levelChoosingMap) {
        gridMap = new Stack3DGirdWorld(gameMain, INITIAL_MAP_WIDTH, INITIAL_MAP_HEIGHT, DEFAULT_CELL_SIZE);
        gridMap.setPosition(8f, 4.5f);
        gridMap.addStack2DLayer(); // 新建一层 2D 层
        initMapToGrid(levelChoosingMap); // 初始化地图内容

        addRacingButton(13f, 4f);
        addReturnButton(9f, 4f);
        
        initPlayerObject();
        
        // 碰撞管理器
        playerOverlapManager = new OverlappingManager(gameMain, playerSpine);
        playerOverlapManager.addSecondaryObject(returnButton, "return");
        
        List<Actor> components = new ArrayList<>();
        // 碰撞检测
        for (Actor boxActor : gridMap.getAllActors()) {
            if (boxActor instanceof BoxObject) {
                BoxObject boxObj = (BoxObject) boxActor;

                if (boxObj.getBoxType() == BoxType.Player) continue;

                // 为所有 GreenBox 添加碰撞检测
                if (boxObj.getBoxType() == BoxType.GreenChest) {
                    int boxIndex = greenBoxObjectOrigin.size();
                    playerOverlapManager.addSecondaryObject(boxObj, String.valueOf(boxIndex));
                    greenBoxObjectOrigin.add(boxObj);

                    // 如果通过当前关卡，添加已完成图标
                    if (gameMain.getSaveArchive() != null && gameMain.getSaveArchive().getMapStatue(index2Map(boxIndex)) == MapStatue.Success) {
                        addCompleteIcon(boxObj.getX() + 0.2f, boxObj.getY() + 0.2f);
                    }
                }

                // 为所有 Wall 添加边界
                if (boxObj.getBoxType() == BoxType.DarkGrayWall) {
                    String BoxBlockTag = String.format("box[%.2f][%.2f]", boxObj.getX(), boxObj.getY()); // 不需要对墙进行变更
                    float blockX = boxObj.getX() - 8f, blockY = boxObj.getY() - 4.5f;
                    accelerationManager.addBound(BoxBlockTag, blockX, blockX + boxObj.getSize(), blockY, blockY + boxObj.getSize());
                }

                // 为所有 BoxTarget 添加动画
                if (boxObj.getBoxType() == BoxType.BoxTarget) {
                    boxObj.setAnimation(0, "rotate", true);
                    boxObj.setAnimationTotalTime(0, MathUtils.random(8f, 12f));
                }

                // 添加到待加入列表
                components.add(boxObj);
            }
        }

        // 显示淡入
        components.forEach(actor -> actor.addAction(Actions.fadeIn(0.2f, Interpolation.pow3Out)));
        playerSpine.remove();
        addActorsToStage(playerSpine);
    }

    /** 检查 Origin 是否有箱子碰撞 */
    public void overlapTrigger() {
        // 检测绿色箱子是否有碰撞发生
        if (level == SokobanLevels.Origin) {
            for (int boxIndex = 0; boxIndex < greenBoxObjectOrigin.size(); boxIndex++) {
                BoxObject boxObj = greenBoxObjectOrigin.get(boxIndex);

                // 碰撞普通绿色箱子
                if (playerOverlapManager.getActorOverlapState(boxObj) == OverlapStatue.FirstOverlap) {
                    // 箱子变为激活状态
                    boxObj.resetBoxType(BoxType.GreenChestActive);

                    // 触发计时器
                    final int enterBoxIndex = boxIndex;
                    timer.put(boxObj, 
                        new TimerClock(gameMain, boxObj, 1.5f, new ClockEndCallback() {
                            @Override
                            public void clockEnd() {enterGameScene(index2Map(enterBoxIndex));} // 进入游戏场景
                        }, false)
                    );
                    timer.get(boxObj).moveBy(-0.3f, 0);
                    addActorsToStage(timer.get(boxObj));
                }

                // 离开绿色激活箱子
                if (playerOverlapManager.getActorOverlapState(boxObj) == OverlapStatue.FirstLeave) {
                    // 箱子变为普通状态
                    boxObj.resetBoxType(BoxType.GreenChest);

                    // 取消计时器
                    if (timer.containsKey(boxObj)) {
                        timer.get(boxObj).cancel();
                        timer.remove(boxObj);
                    }
                }
            }
        }
    }

    /** 获得索引对应关卡 */
    public SokobanMaps index2Map(int boxIndex) {
        // 加载的时候从下到上，所以需要反序
        return switch(boxIndex) {
            case 4 -> SokobanMaps.Origin_1;
            case 3 -> SokobanMaps.Origin_2;
            case 2 -> SokobanMaps.Origin_3;
            case 1 -> SokobanMaps.Origin_4;
            case 0 -> SokobanMaps.Origin_5;
            default -> SokobanMaps.None;
        };
    }

    /** 离开前清理所有动画并渐隐所有物体 */
    public void stopAllActivities() {
        stage.addAction(Actions.run(() -> {
            playerOverlapManager.disableAllCollision();

            returnButton.addAction(Actions.fadeOut(0.3f, Interpolation.sine));
            playerSpine.addAction(Actions.fadeOut(0.3f, Interpolation.sine));
            gridMap.getAllActors().forEach(actor -> actor.addAction(Actions.fadeOut(0.3f, Interpolation.sine)));
            racingModeCheckbox.getAllActors().forEach(actor -> actor.addAction(Actions.fadeOut(0.3f, Interpolation.sine)));
            if (timer != null) for (TimerClock tClock : timer.values()) tClock.remove(); // 清除计时器字典所有部件
        }));
    }
}
