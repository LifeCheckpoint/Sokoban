package com.sokoban.scenes.mapchoose;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
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
import com.sokoban.scenes.GameScene;
import com.sokoban.scenes.SokobanScene;
import com.sokoban.scenes.manager.ActorMapper;
import com.sokoban.polygon.SpineObject;
import com.sokoban.polygon.TimerClock;
import com.sokoban.utils.ActionUtils;

/**
 * 关卡地图选择场景
 * @author Life_Checkpoint
 */
public abstract class MapChooseScene extends SokobanScene {
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

    /** 返回新创建的指定游戏地图选择场景 */
    public static MapChooseScene getMapChooseScene(Main gameMain, SokobanLevels levels) {
        return switch (levels) {
            case SokobanLevels.Origin -> new MapChooseOrigin(gameMain, levels);
            case SokobanLevels.Moving -> new MapChooseMoving(gameMain, levels);
            default -> null;
        };
    }

    @Override
    public void init() {
        super.init();
        // 提示当前 level
        HintMessageBox msgBox = new HintMessageBox(gameMain, level.toString());
        msgBox.setPosition(SCREEN_WIDTH_CENTER, 0.5f);

        // 调用子类对应初始化
        setupLevel(readMapData());

        // 组件 - 计时器 字典
        timer = new HashMap<>();

        addActorsToStage(playerSpine);
        addCombinedObjectToStage(msgBox);

        ActionUtils.FadeInEffect(playerSpine);        
    }

    public abstract void setupLevel(MapData mapData);

    /** 获得地图数据 */
    public MapData readMapData() {
        try {
            String mapPath = switch (level) {
                case SokobanLevels.Origin -> "level/origin.map";
                case SokobanLevels.Moving -> "level/moving.map";
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
    public abstract void stopAllActivities();

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

        overlapTrigger(); // 检测关卡箱触发
    }

    public abstract void overlapTrigger();

    /** 初始化画面玩家 */
    public void initPlayerObject() {
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
    public void addRacingButton(float x, float y) {
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

    /** 添加完成图标 */
    public Image addCompleteIcon(float x, float y) {
        Image completeIcon = new Image(gameMain.getAssetsPathManager().get(ImageAssets.CorrectIcon));
        completeIcon.setSize(0.3f, 0.3f);
        completeIcon.setPosition(x, y);
        addActorsToStage(completeIcon);
        
        return completeIcon;
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
