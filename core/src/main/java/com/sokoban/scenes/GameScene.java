package com.sokoban.scenes;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Timer;
import com.sokoban.assets.ImageAssets;
import com.sokoban.assets.SpineAssets;
import com.sokoban.core.game.GameParams;
import com.sokoban.core.game.Logger;
import com.sokoban.core.json.JsonManager;
import com.sokoban.core.logic.Direction;
import com.sokoban.core.logic.ObjectType;
import com.sokoban.core.logic.PlayerCore;
import com.sokoban.core.logic.PlayerCoreUtils;
import com.sokoban.core.logic.Pos;
import com.sokoban.core.map.MapData;
import com.sokoban.core.map.MapFileInfo;
import com.sokoban.core.map.MapFileParser;
import com.sokoban.core.map.MapFileReader;
import com.sokoban.core.map.MoveListParser;
import com.sokoban.core.map.SubMapData;
import com.sokoban.core.map.gamedefault.SokobanLevels;
import com.sokoban.core.state.GameHistoryRecoder;
import com.sokoban.core.state.GameStateFrame;
import com.sokoban.core.user.UserManager;
import com.sokoban.core.user.SaveArchiveInfo.MapStatue;
import com.sokoban.core.user.SaveArchiveInfo.StepRecordInfo;
import com.sokoban.core.user.SaveArchiveInfo.TimeRecordInfo;
import com.sokoban.Main;
import com.sokoban.polygon.BoxObject;
import com.sokoban.polygon.SpineObject;
import com.sokoban.polygon.TimerClock;
import com.sokoban.polygon.BoxObject.BoxType;
import com.sokoban.polygon.action.ViewportRescaleAction;
import com.sokoban.polygon.combine.CheckboxObject;
import com.sokoban.polygon.combine.CombinedNumberDisplayObject;
import com.sokoban.polygon.combine.GameEscapeFrame;
import com.sokoban.polygon.combine.Stack2DGirdWorld;
import com.sokoban.polygon.combine.Stack3DGirdWorld;
import com.sokoban.polygon.container.ButtonCheckboxContainers;
import com.sokoban.polygon.manager.BackgroundGrayParticleManager;
import com.sokoban.polygon.manager.MouseMovingTraceManager;
import com.sokoban.polygon.manager.SingleActionInstanceManager;
import com.sokoban.scenes.manager.ActorMapper;
import com.sokoban.scenes.mapchoose.MapChooseScene;
import com.sokoban.utils.ActionUtils;

/**
 * 主游戏类，负责游戏的显示交互
 * @author Life_Checkpoint
 */
public class GameScene extends SokobanFitScene {
    // 状态控制
    private MapFileInfo mapFileInfo;
    private boolean isInEscapeMenu;
    private GameParams gameParams;
    private LocalDateTime startTime;

    // 功能增强
    private BackgroundGrayParticleManager bgParticle;
    private MouseMovingTraceManager moveTrace;
    private SingleActionInstanceManager SAIManager;
    private Actor escapeMenuActorStateHelper;

    // Escape Menu
    private ButtonCheckboxContainers buttonContainer;
    private GameEscapeFrame escapeFrame;
    private CheckboxObject continueGameButton;
    private CheckboxObject replayButton;
    private CheckboxObject settingsButton;
    private CheckboxObject exitGameButton;

    // 游戏主内容
    Stack3DGirdWorld gridWorld; // 网格世界
    PlayerCore playerCore; // 游戏逻辑核心
    int currentSubmap; // 当前子地图
    CombinedNumberDisplayObject racingStep; // 竞速步记录器
    CombinedNumberDisplayObject racingTime; // 竞速时间记录器
    TimerClock racingClock; // 竞速计时器

    GameHistoryRecoder historyStates; // 历史记录器

    private final float DEFAULT_CELL_SIZE = 1f;
    private final float VIEWPORT_RESCALE_RATIO = 1.6f;
    private final float ESCAPE_MENU_ANIMATION_DURATION = 0.2f;
    private final float ESCAPE_MENU_BUTTON_SCALING = 0.008f;
    private final float ESCAPE_MENU_BUTTON_ALIGN_X = 11f;
    private final float ESCAPE_MENU_BUTTON_ALIGN_Y = 4f;
    private final float ESCAPE_MENU_BUTTON_ALIGN_Y_DELTA = 1f;
    private final int INITIAL_MAP_WIDTH = 48;
    private final int INITIAL_MAP_HEIGHT = 27;

    public GameScene(Main gameMain, MapFileInfo mapfileInfo, GameParams gameParams) {
        super(gameMain);
        this.mapFileInfo = mapfileInfo;
        this.gameParams = gameParams;

        Logger.info("GameScene", String.format("Enter game -> " + new JsonManager().getJsonString(mapfileInfo)));
        Logger.debug("GameScene", "Game Params -> " + new JsonManager().getJsonString(gameParams));
    }

    // 初始化游戏屏幕
    @Override
    public void init() {
        super.init();

        escapeMenuActorStateHelper = new Actor();
        bgParticle = new BackgroundGrayParticleManager(gameMain);
        bgParticle.startCreateParticles();
        isInEscapeMenu = false;
        moveTrace = new MouseMovingTraceManager(viewport);
        SAIManager = new SingleActionInstanceManager(gameMain);

        // 初始化逻辑内核
        currentSubmap = initPlayerCore();
        if (currentSubmap == -2) return; // 异常情况

        // 初始化网格世界
        gridWorld = new Stack3DGirdWorld(gameMain, INITIAL_MAP_WIDTH, INITIAL_MAP_HEIGHT, DEFAULT_CELL_SIZE);
        gridWorld.setPosition(8f, 4.5f);

        // 网格世界读取
        initMapToGrid();

        // 初始化退出菜单
        initEscapeMenu();

        // 初始化竞速对象
        initRacingObjects();

        // 初始化历史记录
        initHistoryState();

        // 更新地图状态
        // 如果用户非空非 guest，存档非空
        if (gameMain.getLoginUser() != null && !gameMain.getLoginUser().isGuest() && gameMain.getSaveArchive() != null) {
            if (gameMain.getSaveArchive().getMapStatue(mapFileInfo.map) != MapStatue.Success) // 已经成功，不再更新记录
                gameMain.getSaveArchive().updateMapStaute(mapFileInfo.map, MapStatue.Playing); // 设置档案对应地图状态
            new UserManager().updateUserInfo(gameMain.getLoginUser());
        }

        stage.addActor(escapeMenuActorStateHelper);

        // 添加主 Stage 的输入监听
        addStageListener();
    }

    /** 
     * 初始化逻辑内核
     * @return 玩家所在子地图索引，-1 表示未找到，-2 表示异常
     */
    public int initPlayerCore() {
        playerCore = new PlayerCore();
        String levelName = mapFileInfo.level.toString(), mapName = mapFileInfo.map.getMapName();

        String currentMapString = new MapFileReader().readMapByLevelAndName(levelName, mapName);
        if (currentMapString == null) {
            Logger.error("GameScene", String.format("Can't load level - %s, map - %s", levelName, mapName));
            gameMain.getScreenManager().setScreenWithoutSaving(MapChooseScene.getMapChooseScene(gameMain, mapFileInfo.level)); // 直接返回地图选择界面
            return -2;
        }

        MapData currentMapData = MapFileParser.parseMapData(new MapFileInfo("", mapFileInfo.level, mapFileInfo.map), currentMapString);
        if (currentMapData == null) {
            Logger.error("GameScene", String.format("Can't load level - %s, map - %s", levelName, mapName));
            gameMain.getScreenManager().setScreenWithoutSaving(MapChooseScene.getMapChooseScene(gameMain, mapFileInfo.level)); // 直接返回地图选择界面
            return -2;
        }

        return playerCore.setMap(currentMapData);
    }

    /** 初始化地图显示 */
    public void initMapToGrid() {
        // 针对每个子地图创建 Stack2DLayer
        for (int subMapIndex = 0; subMapIndex < playerCore.getMap().allMaps.size(); subMapIndex++) gridWorld.addStack2DLayer();
        
        // 根据当前子地图创建子地图网格世界
        Stack2DGirdWorld currentSubMapGridWorld = gridWorld.getStack2DLayer(currentSubmap);
        SubMapData subMapData = playerCore.getSubmap(currentSubmap);

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
        addCombinedObjectToStage(gridWorld);

        // 设置淡入动画
        currentSubMapGridWorld.getAllActors().forEach(ActionUtils::FadeInEffect);
    }

    /** 初始化所有动画 */
    public void initSpineObjectAnimation(BoxType objectType, SpineObject spineObject) {
        
        // 玩家动画
        if (objectType == BoxType.Player) {
            spineObject.setAnimation(0, "down", false);
        }

        // 箱子目标点动画
        if (objectType == BoxType.BoxTarget) {
            spineObject.setAnimation(1, "deactive", false);
            spineObject.setAnimationTotalTime(1, 0.5f);
        }
    }

    /** 初始化竞速小组件 */
    public void initRacingObjects() {
        racingStep = new CombinedNumberDisplayObject(gameMain, 6, 0);
        racingStep.setPosition(0f, 8f);
        racingStep.setShowDecimalPoint(false);
        racingStep.setShowFirstZeros(false);

        racingTime = new CombinedNumberDisplayObject(gameMain, 6, 2);
        racingTime.setPosition(0f, 7f);
        racingTime.setShowDecimalPoint(true);
        racingTime.setShowFirstZeros(false);

        // 只有在开启竞速的时候才会显示
        if (gameParams.racing) {
            addCombinedObjectToUIStage(racingStep);
            addCombinedObjectToUIStage(racingTime);

            startTime = LocalDateTime.now();

            // 添加竞速定时更新事件
            Timer.schedule(new Timer.Task() {
                @Override
                public void run() {
                    stage.addAction(Actions.sequence(
                        Actions.delay(MathUtils.random(-0.2f, 0.2f)),
                        Actions.run(() -> racingTime.setValue(((float) Duration.between(startTime, LocalDateTime.now()).toMillis()) / 1000f))
                    ));
                }
            }, 0.05f, 0.5f);
        }
    }

    /**
     * 动画更新画面表现
     * @param moves 位移列表
     */
    public void updateShowing(List<String> moves) {
        for (int i = 0; i < moves.size(); i++) Logger.debug("GameScene", String.format("Move instruction #%d: %s", i, moves.get(i)));

        // 解析信息
        List<MoveListParser.MoveInfo> moveInfo = MoveListParser.parseMove(moves);
        // 分组处理
        Map<Integer, Map<Integer, List<MoveListParser.MoveInfo>>> groupedMoves = MoveListParser.groupMoves(moveInfo);

        for (int subMapIndex : groupedMoves.keySet()) { // 每个子地图
            for (int layerIndex : groupedMoves.get(subMapIndex).keySet()) { // 每层
                // 进行转移
                Actor[][] thisLayerGird = gridWorld.getStack2DLayer(subMapIndex).getLayer(layerIndex).gridSpineObjects; // 原数组
                Actor[][] newLayerGird = new Actor[thisLayerGird.length][thisLayerGird[0].length]; // 新地图层数组
                Set<Pos> completedMoveTo = new HashSet<>(); // 保存已经成功到达目标移动的数据
                Set<Pos> completedMoveFrom = new HashSet<>(); // 保存已经成功离开原位移动的数据

                // 对于每个移动，将原数组的相应数据的引用复制到新数组上
                for (MoveListParser.MoveInfo move : groupedMoves.get(subMapIndex).get(layerIndex)) {

                    // 进行动画层面移动
                    doAnimatedMove(thisLayerGird[move.origin.getY()][move.origin.getX()], move.to);
                    doAnimation(thisLayerGird[move.origin.getY()][move.origin.getX()], subMapIndex, layerIndex, move.origin, move.to);

                    // 复制新物体
                    newLayerGird[move.to.getY()][move.to.getX()] = thisLayerGird[move.origin.getY()][move.origin.getX()];
                    
                    // 将已经成功转移的物体加入 completedMoveFrom 与 To
                    if (!(completedMoveFrom.add(move.origin) && completedMoveTo.add(move.to))) { // 如果集合中出现重复元素，报错并拒绝移动
                        Logger.error("GameScene", "Find conflict in move instructions");
                        return;
                    }
                }

                /*
                通过数组引用的复制解决依赖矛盾
                对于一个物体，如果它未被标记，需要复制
                如果同时被标记为转移出 / 转移入的目标点，无需复制
                如果被单独标记为转移入，无需复制
                如果被单独标记为转移出，需要复制

                -> 简化为检查是否标记转移出即可
                */
                for (int y = 0; y < thisLayerGird.length; y++) {
                    for (int x = 0; x < thisLayerGird[0].length; x++) {
                        Pos testPos = new Pos(x, y);
                        if (!completedMoveTo.contains(testPos)) newLayerGird[y][x] = thisLayerGird[y][x];
                    }
                }

                // 完成移动，原数组重新被新数组引用覆盖
                for (int y = 0; y < thisLayerGird.length; y++) {
                    for (int x = 0; x < thisLayerGird[0].length; x++) {
                        thisLayerGird[y][x] = newLayerGird[y][x];
                    }
                }

            }
        }
    }

    /**
     * 对 actor 进行动画层面移动
     * @param actor 要移动的物件
     * @param to 移动到的网格位置
     */
    public void doAnimatedMove(Actor actor, Pos to) {
        // 计算最终坐标，转换为居中坐标
        Vector2 finalPosition = gridWorld.getStack2DLayer(currentSubmap).getCellPosition(to.getY(), to.getX()); // row = y, column = x

        // 添加单例动画执行器
        SAIManager.executeAction(
            actor,
            Actions.moveTo(finalPosition.x, finalPosition.y, 0.2f, Interpolation.pow4Out),
            () -> { // reset 重置回调事件
                actor.clearActions();
                actor.setPosition(finalPosition.x, finalPosition.y);
            }
        );
    }

    /**
     * 对 actor 进行动画更新
     * <br><br>
     * 注意在 Core 中获取物件应该使用 to 坐标，因为内核已经进行更新，而在 grid 中应该使用 from 坐标获取物件
     * @param actor
     * @param x
     * @param y
     */
    public void doAnimation(Actor actor, int subMapIndex, int layerIndex, Pos from, Pos to) {
        SubMapData subMap = playerCore.getMap().allMaps.get(subMapIndex);
        ObjectType object = subMap.mapLayer.get(layerIndex)[to.getY()][to.getX()];

        Logger.debug("Execute Animation: " + object.toString());

        // 玩家
        if (object == ObjectType.Player) {
            // 玩家动画更新
            ((SpineObject) actor).setAnimation(0, PlayerCoreUtils.getDeltaDirection(to.sub(from)).getDirection(), false);
        }

        // 箱子
        if (object == ObjectType.Box) {
            // 到达目标点
            if (subMap.mapLayer.get(SubMapData.LAYER_TARGET)[to.getY()][to.getX()] == ObjectType.BoxTarget) {
                ((BoxObject) actor).reset(gameMain, SpineAssets.BoxGreenBoxLight);
            } else {
                ((BoxObject) actor).reset(gameMain, SpineAssets.BoxGreenBox);
            }
        }
    }

    /** 初始化退出菜单 */
    public void initEscapeMenu() {
        escapeFrame = new GameEscapeFrame(gameMain, 11f, 6.5f, 8f, 4.5f);
        
        buttonContainer = new ButtonCheckboxContainers();

        continueGameButton = buttonContainer.create(gameMain, ImageAssets.ContinueGame, false, true, 0.1f, ESCAPE_MENU_BUTTON_SCALING);
        continueGameButton.setPosition(ESCAPE_MENU_BUTTON_ALIGN_X, ESCAPE_MENU_BUTTON_ALIGN_Y + ESCAPE_MENU_BUTTON_ALIGN_Y_DELTA * 3);
        continueGameButton.setCheckboxType(false);

        replayButton = buttonContainer.create(gameMain, ImageAssets.PlayAgainButton, false, true, 0.1f, ESCAPE_MENU_BUTTON_SCALING);
        replayButton.setPosition(ESCAPE_MENU_BUTTON_ALIGN_X, ESCAPE_MENU_BUTTON_ALIGN_Y + ESCAPE_MENU_BUTTON_ALIGN_Y_DELTA * 2);
        replayButton.setCheckboxType(false);

        settingsButton = buttonContainer.create(gameMain, ImageAssets.SettingsButton, false, true, 0.1f, ESCAPE_MENU_BUTTON_SCALING);
        settingsButton.setPosition(ESCAPE_MENU_BUTTON_ALIGN_X, ESCAPE_MENU_BUTTON_ALIGN_Y + ESCAPE_MENU_BUTTON_ALIGN_Y_DELTA * 1);
        settingsButton.setCheckboxType(false);

        exitGameButton = buttonContainer.create(gameMain, ImageAssets.ExitButton, false, true, 0.1f, ESCAPE_MENU_BUTTON_SCALING);
        exitGameButton.setPosition(ESCAPE_MENU_BUTTON_ALIGN_X, ESCAPE_MENU_BUTTON_ALIGN_Y + ESCAPE_MENU_BUTTON_ALIGN_Y_DELTA * 0);
        exitGameButton.setCheckboxType(false);

        // 继续按钮监听
        continueGameButton.getCheckboxText().addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                closeEscapeMenu();
            }
        });

        // 重玩按钮监听
        replayButton.getCheckboxText().addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                replay();
            }
        });

        // 退出按钮监听
        exitGameButton.getCheckboxText().addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                returnToMapChooseScene();
            }
        });

        // 设置按钮监听
        settingsButton.getCheckboxText().addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                gameMain.getScreenManager().setScreen(new SettingScene(gameMain));
            }
        });
    }

    /** 添加 Stage 输入监听 */
    public void addStageListener() {
        stage.addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                Direction moveDirection = Direction.None;

                // 如果在退出菜单则不检测操控行为
                if (isInEscapeMenu) return false;

                if (keycode == Keys.W || keycode == Keys.UP) moveDirection = Direction.Up;
                if (keycode == Keys.S || keycode == Keys.DOWN) moveDirection = Direction.Down;
                if (keycode == Keys.A || keycode == Keys.LEFT) moveDirection = Direction.Left;
                if (keycode == Keys.D || keycode == Keys.RIGHT) moveDirection = Direction.Right;
    
                // 如果进行四种移动之一
                if (moveDirection != Direction.None) {
                    // 进行逻辑移动
                    Logger.debug("GameScene", "Move direction " + moveDirection);
                    MapData originalMap = playerCore.getMap().deepCopy();
                    playerCore.move(currentSubmap, moveDirection);
  
                    // 如果地图未发生变化，不进行任何更新
                    if (originalMap.equals(playerCore.getMap())) return true;

                    // 更新历史记录
                    GameStateFrame stateFrame = new GameStateFrame();
                    stateFrame.mapData = playerCore.getMap().deepCopy();
                    stateFrame.action = moveDirection;
                    stateFrame.stepCount = historyStates.getTotalFrameNum(); // 不包括初始帧
                    stateFrame.moves = playerCore.getMoveList();

                    stateFrame.undo = false;

                    Logger.debug("GameScene", "Current game frame = " + stateFrame, 500);
                    historyStates.addNewFrame(stateFrame);

                    // 更新画面表现
                    updateShowing(playerCore.getMoveList());

                    // 更新计步器
                    racingStep.setValue(historyStates.getLast().stepCount);

                    // 检查是否胜利
                    if (playerCore.isGameWin()) endGame(true);

                    return true;
                }
    
                // 撤销
                if (Gdx.input.isKeyJustPressed(Keys.Z)) {

                    // 如果不是初始状态
                    if (historyStates.getTotalFrameNum() > 1) {
                        // 允许撤销，但是步数和时间都会继续增长
                        GameStateFrame undoFrame = historyStates.undo();

                        if (undoFrame != null) {
                            playerCore.setMap(undoFrame.mapData);
                            Logger.debug("GameScene", "Undo, Current game frame = " + undoFrame);
                            Logger.warning(undoFrame.moves.toString());

                            // 更新画面表现
                            updateShowing(undoFrame.moves);
                        }

                        // 更新计步器
                        racingStep.setValue(historyStates.getLast().stepCount);
                    }
                    
                    return true;
                }
    
                // 重置
                if (Gdx.input.isKeyJustPressed(Keys.R)) {
                    replay();
                    return true;
                }

                return false;
            }
        });
    }

    /** 初始化历史记录 */
    public void initHistoryState() {
        historyStates = new GameHistoryRecoder(gameParams);

        // 添加初始记录
        GameStateFrame stateFrame = new GameStateFrame();
        stateFrame.mapData = playerCore.getMap().deepCopy();
        stateFrame.action = Direction.None;
        stateFrame.stepCount = 0;
        Logger.debug("GameScene", "Init, Current game frame = " + stateFrame.toString(), 500);

        historyStates.addNewFrame(stateFrame);
    }

    /** 胜利 / 失败结束游戏 */
    public void endGame(boolean succcess) {

        // 胜利结束
        if (succcess) {
            String successString = null;

            // 如果用户非空非 guest，存档非空
            if (gameMain.getLoginUser() != null && !gameMain.getLoginUser().isGuest() && gameMain.getSaveArchive() != null)  {
                
                // 检查 racing 是否破纪录
                if (gameParams.racing) {
                    StepRecordInfo stepRecord = gameMain.getSaveArchive().updateStepRecords(mapFileInfo.map, historyStates.getLast().stepCount);
                    long duration = Duration.between(startTime, historyStates.getLast().frameTime).toMillis();
                    TimeRecordInfo timeRecord = gameMain.getSaveArchive().updateTimeRecords(mapFileInfo.map, duration);

                    if (stepRecord.success) {
                        successString = (successString == null) ? "" : successString;
                        successString += String.format("Step: %d -> %d", stepRecord.lastBestRecord, stepRecord.currentRecord);
                    }

                    if (timeRecord.success) {
                        successString = (successString == null) ? "" : successString + ", ";
                        successString += String.format("Time: %d -> %d (ms)", timeRecord.lastBestRecord, timeRecord.currentRecord);
                    }

                    Logger.info("GameScene", "Success string:" + successString);
                }

                // 保存存档
                gameMain.getSaveArchive().updateMapStaute(mapFileInfo.map, MapStatue.Success); // 设置档案对应地图状态
                if (!new UserManager().updateUserInfo(gameMain.getLoginUser())) {
                    Logger.error("GameScene", "Can't save user file");
                } else {
                    Logger.info("GameScene", "Save user file OK");
                }
            }

            // 等待一会后淡出
            Image whiting = new Image(gameMain.getAssetsPathManager().get(ImageAssets.WhitePixel));
            whiting.setSize(48f, 48f);
            whiting.getColor().a = 0f;
            addActorsToUIStage(whiting);
            whiting.addAction(Actions.fadeIn(0.95f, Interpolation.sineIn)); 

            final String successStringValue = successString;
            final boolean defaultEnableRacingValue = gameParams.racing;
            stage.addAction(Actions.sequence(
                Actions.delay(1f),
                Actions.run(() -> returnToMapChooseScene(successStringValue, defaultEnableRacingValue))
            ));

        } else {
            // TODO 失败逻辑
        }
    }

    /** 绘制屏幕场景 */
    public void draw(float delta) {
        // 更新鼠标跟踪
        moveTrace.setPositionWithUpdate();
        stage.draw();
        UIStage.draw();
    }

    /** 显示退出菜单 */
    public void showEscapeMenu() {
        if (!SAIManager.isInAction(escapeMenuActorStateHelper)) {
            isInEscapeMenu = true;

            // 缩放添加后渐显
            SAIManager.executeAction(escapeMenuActorStateHelper, Actions.sequence(
                Actions.run(() -> {
                    addCombinedObjectToUIStage(escapeFrame);
                    addCombinedObjectToUIStage(continueGameButton, settingsButton, exitGameButton, replayButton);
                }),
                Actions.parallel(
                    new ViewportRescaleAction(viewport, VIEWPORT_RESCALE_RATIO, ESCAPE_MENU_ANIMATION_DURATION),
                    Actions.run(() -> {
                        escapeFrame.getAllActors().forEach(actor -> actor.addAction(Actions.fadeIn(ESCAPE_MENU_ANIMATION_DURATION)));
                        continueGameButton.getAllActors().forEach(actor -> actor.addAction(Actions.fadeIn(ESCAPE_MENU_ANIMATION_DURATION)));
                        replayButton.getAllActors().forEach(actor -> actor.addAction(Actions.fadeIn(ESCAPE_MENU_ANIMATION_DURATION)));
                        settingsButton.getAllActors().forEach(actor -> actor.addAction(Actions.fadeIn(ESCAPE_MENU_ANIMATION_DURATION)));
                        exitGameButton.getAllActors().forEach(actor -> actor.addAction(Actions.fadeIn(ESCAPE_MENU_ANIMATION_DURATION)));
                    })
                )
            ));
        }
    }

    /** 隐藏退出菜单 */
    public void closeEscapeMenu() {
        if (!SAIManager.isInAction(escapeMenuActorStateHelper)) isInEscapeMenu = false;

        // 缩放渐隐后清除
        SAIManager.executeAction(escapeMenuActorStateHelper, Actions.sequence(
            Actions.parallel(
                new ViewportRescaleAction(viewport, 1 / VIEWPORT_RESCALE_RATIO, ESCAPE_MENU_ANIMATION_DURATION),
                Actions.run(() -> {
                    escapeFrame.getAllActors().forEach(actor -> actor.addAction(Actions.fadeOut(ESCAPE_MENU_ANIMATION_DURATION)));
                    continueGameButton.getAllActors().forEach(actor -> actor.addAction(Actions.fadeOut(ESCAPE_MENU_ANIMATION_DURATION)));
                    replayButton.getAllActors().forEach(actor -> actor.addAction(Actions.fadeOut(ESCAPE_MENU_ANIMATION_DURATION)));
                    settingsButton.getAllActors().forEach(actor -> actor.addAction(Actions.fadeOut(ESCAPE_MENU_ANIMATION_DURATION)));
                    exitGameButton.getAllActors().forEach(actor -> actor.addAction(Actions.fadeOut(ESCAPE_MENU_ANIMATION_DURATION)));
                })
            ),
            Actions.run(() -> {
                escapeFrame.getAllActors().forEach(Actor::remove);
                continueGameButton.getAllActors().forEach(Actor::remove);
                replayButton.getAllActors().forEach(Actor::remove);
                settingsButton.getAllActors().forEach(Actor::remove);
                exitGameButton.getAllActors().forEach(Actor::remove);
            })
        ));
        
    }

    /** 再玩一次 */
    public void replay() {
        // 直接重启屏幕
        gameMain.getScreenManager().setScreenWithoutSaving(new GameScene(gameMain, mapFileInfo, gameParams));
    }

    @Override
    public void logic(float delta) {}

    @Override
    public void input() {
        // 检测退出行为
        if (Gdx.input.isKeyJustPressed(Keys.ESCAPE)) {
            if (!isInEscapeMenu) showEscapeMenu();
            else closeEscapeMenu();
        }

        // 强制退出
        if (Gdx.input.isKeyPressed(Keys.CONTROL_LEFT) && Gdx.input.isKeyJustPressed(Keys.W)) {
            returnToMapChooseScene();
        }
    }

    /** 返回地图选择场景 */
    public void returnToMapChooseScene() {
        returnToMapChooseScene(null, false);
    }

    /** 返回地图选择场景 */
    public void returnToMapChooseScene(String msg, boolean defaultEnableRacing) {
        SokobanScene returnScreen;

        if (mapFileInfo.level == SokobanLevels.Tutorial) returnScreen = new LevelChooseScene(gameMain);
        else if (mapFileInfo.level != SokobanLevels.None) {
            returnScreen = MapChooseScene.getMapChooseScene(gameMain, mapFileInfo.level);
            ((MapChooseScene) returnScreen).setWinMessage(msg);
            ((MapChooseScene) returnScreen).setDefaultEnableRacing(defaultEnableRacing);
        }
        else returnScreen = new LevelChooseScene(gameMain);

        stage.addAction(Actions.sequence(
            // 离开前处理
            Actions.run(() -> {}),
            // 等待指定时间
            Actions.delay(0.2f),
            // 返回上一界面
            Actions.run(() -> gameMain.getScreenManager().setScreenWithoutSaving(returnScreen))
        ));
    }

    /** 资源释放 */
    @Override
    public void dispose() {
        super.dispose();
    }
}
