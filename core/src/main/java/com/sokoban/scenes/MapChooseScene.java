package com.sokoban.scenes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.sokoban.assets.ImageAssets;
import com.sokoban.core.game.GameParams;
import com.sokoban.core.game.Logger;
import com.sokoban.core.logic.Direction;
import com.sokoban.core.logic.ObjectType;
import com.sokoban.core.map.MapData;
import com.sokoban.core.map.MapFileInfo;
import com.sokoban.core.map.MapFileParser;
import com.sokoban.core.map.SubMapData;
import com.sokoban.core.map.gamedefault.SokobanLevels;
import com.sokoban.core.map.gamedefault.SokobanMaps;
import com.sokoban.Main;
import com.sokoban.polygon.actioninterface.ClockEndCallback;
import com.sokoban.polygon.BoxObject;
import com.sokoban.polygon.BoxObject.BoxType;
import com.sokoban.polygon.combine.CheckboxObject;
import com.sokoban.polygon.combine.HintMessageBox;
import com.sokoban.polygon.combine.Stack2DGirdWorld;
import com.sokoban.polygon.combine.Stack3DGirdWorld;
import com.sokoban.polygon.container.ImageButtonContainer;
import com.sokoban.polygon.manager.AccelerationMovingManager;
import com.sokoban.polygon.manager.MouseMovingTraceManager;
import com.sokoban.polygon.manager.OverlappingManager;
import com.sokoban.polygon.manager.OverlappingManager.OverlapStatue;
import com.sokoban.scenes.manager.ActorMapper;
import com.sokoban.polygon.SpineObject;
import com.sokoban.polygon.TimerClock;
import com.sokoban.utils.ActionUtils;

public class MapChooseScene extends SokobanScene {
    protected AccelerationMovingManager accelerationManager;
    protected Direction preDirection = Direction.None;
    protected boolean isPlayerInMove;
    protected Image returnButton;
    protected SokobanLevels level;
    protected MouseMovingTraceManager moveTrace;
    protected SpineObject playerSpine;
    protected OverlappingManager playerOverlapManager;
    protected Image racingModeButton;
    protected CheckboxObject racingModeCheckbox;
    protected GameParams gameParams = new GameParams();
    protected Map<Actor, TimerClock> timer;
    protected List<BoxObject> greenBoxObjectOrigin = new ArrayList<>();

    protected final float SCREEN_WIDTH_CENTER = 8f, SCREEN_HEIGHT_CENTER = 4.5f;
    protected final int INITIAL_MAP_WIDTH = 48, INITIAL_MAP_HEIGHT = 27;
    protected final float DEFAULT_CELL_SIZE = 1f;

    // 选择界面地图网格
    protected Stack3DGirdWorld gridMap;

    public MapChooseScene(Main gameMain, SokobanLevels levelEnum) {
        super(gameMain);
        this.level = levelEnum;
    }

    @Override
    public void init() {
        super.init();
        // 提示当前 level
        HintMessageBox msgBox = new HintMessageBox(gameMain, level.toString());
        msgBox.setPosition(SCREEN_WIDTH_CENTER, 0.5f);

        // 根据场景不同调用对应初始化
        switch (level) {
            case Origin:
                setupLevelOrigin(readMapData());
                break;
            default:
                break;
        }

        // 组件 - 计时器 字典
        timer = new HashMap<>();

        addActorsToStage(playerSpine);
        addCombinedObjectToStage(msgBox);

        ActionUtils.FadeInEffect(playerSpine);        
    }

    /** 获得地图数据 */
    public MapData readMapData() {
        try {
            String mapPath = switch (level) {
                case SokobanLevels.Origin -> "level/origin.map";
                default -> "";
            };
            return MapFileParser.parseMapData(new MapFileInfo(), Gdx.files.internal(mapPath).readString());
        } catch (Exception e) {
            Logger.error("MapChooseScene", "Can't parse level choser map");
            gameMain.getScreenManager().returnPreviousScreen();
            return null;
        }
    }

    /** 离开前清理所有动画并渐隐所有物体 */
    public void stopAllActivities() {
        stage.addAction(Actions.run(() -> {
            playerOverlapManager.disableAllCollision();

            // 共有物体
            returnButton.addAction(Actions.fadeOut(0.3f, Interpolation.sine));
            playerSpine.addAction(Actions.fadeOut(0.3f, Interpolation.sine));
            gridMap.getAllActors().forEach(actor -> actor.addAction(Actions.fadeOut(0.3f, Interpolation.sine)));

            // 非共有物体，需要判断存在性
            if (racingModeCheckbox != null) racingModeCheckbox.getAllActors().forEach(actor -> actor.addAction(Actions.fadeOut(0.3f, Interpolation.sine)));
            if (timer != null) for (TimerClock tClock : timer.values()) tClock.remove(); // 清除计时器字典所有部件
        }));
    }

    // FIXME 快速退出会出现闪动
    /** 返回按钮触发返回上一场景 */
    public void returnToPreviousScreen() {
        stopAllActivities(); // 结束所有动画
        stage.addAction(Actions.sequence(
            // 等待指定时间
            Actions.delay(0.8f),
            // 返回上一界面
            Actions.run(() -> gameMain.getScreenManager().returnPreviousScreen())
        ));
    }

    /** 进入游玩界面 */
    public void enterGameScene(SokobanMaps map) {
        stopAllActivities(); // 结束所有动画

        Logger.info("MapChooseScene", "Enter Map -> " + map.toString());
        stage.addAction(Actions.sequence(
            // 等待指定时间
            Actions.delay(0.8f),
            // 进入游戏界面
            Actions.run(() -> gameMain.getScreenManager().setScreenWithoutSaving(new GameScene(gameMain, new MapFileInfo("", level, map), gameParams)))
        ));
    }

    // 处理连续键盘移动，需要累积处理
    private void updateMovingInput() {
        // 加速度管理器管理移动，反向按键混合将静止
        boolean keyUp = Gdx.input.isKeyPressed(Keys.W) || Gdx.input.isKeyPressed(Keys.UP);
        boolean keyLeft = Gdx.input.isKeyPressed(Keys.A) || Gdx.input.isKeyPressed(Keys.LEFT);
        boolean keyDown = Gdx.input.isKeyPressed(Keys.S) || Gdx.input.isKeyPressed(Keys.DOWN);
        boolean keyRight = Gdx.input.isKeyPressed(Keys.D) || Gdx.input.isKeyPressed(Keys.RIGHT);

        boolean inMove = false;

        // System.out.printf("%s %s %s %s\n", keyUp, keyDown, keyLeft, keyRight);
        
        if (keyUp && !keyDown) {
            updatePlayerAnimation(Direction.Up);
            inMove = true;
        }
        
        if (keyDown && !keyUp) {
            updatePlayerAnimation(Direction.Down);
            inMove = true;
        }
        
        if (keyLeft && !keyRight) {
            updatePlayerAnimation(Direction.Left);
            inMove = true;
        }
        
        if (keyRight && !keyLeft) {
            updatePlayerAnimation(Direction.Right);
            inMove = true;
        }

        if (!inMove) updatePlayerAnimation(Direction.None);

        // 完成速度更新后进行实际位移
        accelerationManager.updateActorMove();
    }

    // 处理单次键盘输入
    @Override
    public void input() {
        // 退出
        if (Gdx.input.isKeyJustPressed(Keys.ESCAPE)) {
            returnToPreviousScreen();
        }
    }

    /** 玩家动画更新 */
    private void updatePlayerAnimation(Direction direction) {
        // 相同移动，仅第一次触发动画
        if (!isPlayerInMove && direction != Direction.None) {
            playerSpine.setAnimation(0, direction.getDirection(), false);
            isPlayerInMove = true;
        }
        // 不相同移动，更改触发动画
        else if (preDirection != direction && direction != Direction.None) {
            playerSpine.setAnimation(0, direction.getDirection(), false);
            isPlayerInMove = true;
        }

        // 处理移动
        accelerationManager.updateVelocity(direction);
        // 解锁动画
        if (direction == Direction.None) isPlayerInMove = false;
        // 更新上一次移动
        preDirection = direction;
    }

    // 全局碰撞检测
    private void overlapsCheck() {
        playerOverlapManager.overlapsCheck();

        // Origin Level
        // 触碰返回按钮触发
        if (returnButton != null && playerOverlapManager.getActorOverlapState(returnButton) == OverlapStatue.FirstOverlap) {
            // 首次碰撞，启动时钟
            timer.put(returnButton, 
                new TimerClock(gameMain, returnButton, 1.5f, new ClockEndCallback() {
                    @Override
                    public void clockEnd() {returnToPreviousScreen();}
                }, false)
            );
            timer.get(returnButton).moveBy(-0.2f, 0);
            addActorsToStage(timer.get(returnButton));
        }

        // 从返回按钮退出，停止启动时钟
        if (timer != null && playerOverlapManager.getActorOverlapState(returnButton) == OverlapStatue.FirstLeave) {
            if (timer.containsKey(returnButton)) {
                timer.get(returnButton).cancel();
                timer.remove(returnButton);
            }
        }

        if (level == SokobanLevels.Origin) overlapTriggerOrigin(); // 检测第一个地图关卡箱触发
    }

    /** 检查第一个地图是否有箱子碰撞 */
    public void overlapTriggerOrigin() {
        // 检测绿色箱子是否有碰撞发生
        if (level == SokobanLevels.Origin) {
            for (int boxIndex = 0; boxIndex < greenBoxObjectOrigin.size(); boxIndex++) {
                BoxObject boxObj = greenBoxObjectOrigin.get(boxIndex);

                // 碰撞普通绿色箱子
                if (playerOverlapManager.getActorOverlapState(boxObj) == OverlapStatue.FirstOverlap) {
                    // 箱子变为激活状态
                    boxObj.resetBoxType(BoxType.GreenChestActive);

                    // 获得对应关卡
                    // 加载的时候从下到上，所以需要反序
                    SokobanMaps enterMap = switch(boxIndex) {
                        case 4 -> SokobanMaps.Origin_1;
                        case 3 -> SokobanMaps.Origin_2;
                        case 2 -> SokobanMaps.Origin_3;
                        case 1 -> SokobanMaps.Origin_4;
                        case 0 -> SokobanMaps.Origin_5;
                        default -> SokobanMaps.None;
                    };

                    // 触发计时器
                    timer.put(boxObj, 
                        new TimerClock(gameMain, boxObj, 1.5f, new ClockEndCallback() {
                            @Override
                            public void clockEnd() {enterGameScene(enterMap);} // 进入游戏场景
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

    /**
     * 关卡 origin 初始化
     * @param levelChoosingMap 关卡选择地图
     */
    private void setupLevelOrigin(MapData levelChoosingMap) {
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
                    playerOverlapManager.addSecondaryObject(boxObj, String.valueOf(greenBoxObjectOrigin.size()));
                    greenBoxObjectOrigin.add(boxObj);
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

    /** 初始化画面玩家 */
    private void initPlayerObject() {
        // 找到玩家并设为当前画面中心
        for (Actor boxActor : gridMap.getAllActors()) {
            if (boxActor instanceof BoxObject) {
                BoxObject boxObj = (BoxObject) boxActor;
                if (boxObj.getBoxType() == BoxType.Player) {
                    Logger.debug("MapChooseScene", "Find Player Object");
                    boxObj.stayAnimationAtFirst("down");
                    boxObj.setSize(1f, 1f);

                    // 视角跟随
                    moveTrace = new MouseMovingTraceManager(viewport, SCREEN_WIDTH_CENTER + boxObj.getWidth() / 2, SCREEN_HEIGHT_CENTER + boxObj.getHeight() / 2);
                    playerSpine = boxObj;

                    // 加速度管理器
                    accelerationManager = new AccelerationMovingManager(playerSpine, 0.001f, 0.04f, 0.96f);
                    accelerationManager.setReactPositionXY(boxObj.getX() - SCREEN_WIDTH_CENTER + boxObj.getWidth() / 2, boxObj.getY() - SCREEN_HEIGHT_CENTER + boxObj.getHeight() / 2);
                }
            }
        }
    }

    /** 添加返回按钮 */
    public void addReturnButton(float x, float y) {
        ImageButtonContainer controlButtonContainer = new ImageButtonContainer(gameMain);
        returnButton = controlButtonContainer.create(ImageAssets.LeftArrowButton);
        returnButton.setPosition(x, y);

        returnButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                returnToPreviousScreen();
            }
        });

        addActorsToStage(returnButton);
        ActionUtils.FadeInEffect(returnButton);
    }

    /** 添加竞速复选框 */
    private void addRacingButton(float x, float y) {
        racingModeButton = new ImageButtonContainer(gameMain).create(ImageAssets.RacingModeButton);
        racingModeCheckbox = new CheckboxObject(gameMain, racingModeButton, false, true);
        racingModeCheckbox.setCheckboxType(true);
        racingModeCheckbox.setPosition(x, y);
        racingModeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                gameParams.racing = racingModeCheckbox.getChecked();
                // 提示
                HintMessageBox msgBox = new HintMessageBox(gameMain, "Racing Mode " + (gameParams.racing ? "On" : "Off"));
                msgBox.setPosition(racingModeButton.getX() + racingModeCheckbox.getWidth() / 2, racingModeCheckbox.getY() - 1f);
                addCombinedObjectToStage(msgBox);
            }
        });

        racingModeCheckbox.addActorsToStage(stage);
    }

    /** 初始化地图显示 */
    public void initMapToGrid(MapData map) {
        // 针对每个子地图创建 Stack2DLayer
        for (int subMapIndex = 0; subMapIndex < map.allMaps.size(); subMapIndex++) gridMap.addStack2DLayer();
        
        // 根据当前子地图创建子地图网格世界
        Stack2DGirdWorld currentSubMapGridWorld = gridMap.getStack2DLayer(0); // 选择关卡的地图只会有一层
        SubMapData subMapData = map.allMaps.get(0); // 选择关卡的地图只会有一层

        for (int layer = 0; layer < subMapData.mapLayer.size(); layer++) currentSubMapGridWorld.addLayer(); // 添加子地图每一层

        // 对于当前子地图的每一层
        for (int layer = 0; layer < subMapData.mapLayer.size(); layer++) {
            // 当前层
            ObjectType[][] currentLayer = subMapData.mapLayer.get(layer);
            
            for (int y = 0; y < subMapData.height; y++) {
                for (int x = 0; x < subMapData.width; x++) {
                    // 空气与未知类型不处理
                    if (currentLayer[y][x] == ObjectType.Air || currentLayer[y][x] == ObjectType.Unknown) continue;

                    // 数据类型转换为显示类型
                    BoxType objectBoxType = ActorMapper.mapObjectTypeToActor(currentLayer[y][x]);
                    currentSubMapGridWorld.getLayer(layer).addBox(objectBoxType, y, x);
                }
            }
        }

        // 将网格世界加入 stage
        addCombinedObjectToStage(gridMap);
    }

    // 重绘逻辑
    @Override
    public void draw(float delta) {
        // 更新鼠标跟踪、主角视角
        moveTrace.setPositionWithUpdate(playerSpine);

        // 更新移动检测
        updateMovingInput();
        
        // 更新碰撞检测器
        overlapsCheck();
        
        stage.draw();
    }

    // 需要连续处理的输入逻辑
    @Override
    public void logic(float delta) {}

    // 资源释放
    @Override
    public void dispose() {
        super.dispose();
    }
    
}
