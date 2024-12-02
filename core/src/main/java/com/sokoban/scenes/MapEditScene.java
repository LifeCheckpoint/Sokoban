package com.sokoban.scenes;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.sokoban.Main;
import com.sokoban.assets.ImageAssets;
import com.sokoban.assets.SpineAssets;
import com.sokoban.core.Logger;
import com.sokoban.core.game.ObjectType;
import com.sokoban.core.map.MapData;
import com.sokoban.core.map.MapFileInfo;
import com.sokoban.core.map.MapFileParser;
import com.sokoban.core.map.MapFileReader;
import com.sokoban.core.map.SubMapData;
import com.sokoban.polygon.SpineObject;
import com.sokoban.polygon.BoxObject.BoxType;
import com.sokoban.polygon.combine.BottomObjectChooser;
import com.sokoban.polygon.combine.HintMessageBox;
import com.sokoban.polygon.combine.QuestDialog;
import com.sokoban.polygon.combine.Stack2DGirdWorld;
import com.sokoban.polygon.combine.Stack3DGirdWorld;
import com.sokoban.polygon.combine.TopMenu;
import com.sokoban.polygon.container.ImageButtonContainer;
import com.sokoban.polygon.container.ImageLabelContainer;
import com.sokoban.polygon.manager.BackgroundGrayParticleManager;
import com.sokoban.polygon.manager.SingleActionInstanceManager;
import com.sokoban.utils.ActionUtils;
import com.sokoban.utils.MathUtilsEx;
import com.sokoban.utils.WindowsFileChooser;

/**
 * 地图编辑界面
 * @author Life_Checkpoint
 */
public class MapEditScene extends SokobanFitScene {
    // 辅助功能
    private BackgroundGrayParticleManager bgParticle;
    private SingleActionInstanceManager SAIManager;

    // 状态控制
    private boolean pullDownTopMenu = false; // 下拉菜单是否被拉下
    private boolean questWindowShowing = false; // 是否有弹出窗口
    private String currentFilePath = null; // 当前文件路径
    private float currentWorldScaling = 1.0f; // 当前世界缩放大小
    private boolean isDragging = false; // 是否正在拖动可变视口
    private Vector2 lastMousePosition = new Vector2(); // 鼠标拖动起始点
    private float lastMouseOriginalX, lastMouseOriginalY; // 鼠标拖动原始坐标
    private int draggedMouseButton; // 当前拖动鼠标类型

    // 按钮 菜单
    private Image layerUpButton, layerDownButton;
    private TopMenu topMenu;
    private BottomObjectChooser bottomChooser;

    // 地图
    private Stack3DGirdWorld map3DGirdWorld;
    private Image[][] cornerDecoration; // 角落的方格标记
    private MapData map; // 完整地图数据
    private int currentSubMapIndex = 0; // 当前子地图索引

    private final int INITIAL_MAP_WIDTH = 48;
    private final int INITIAL_MAP_HEIGHT = 27;
    private final float MAX_MOVING_PACE = 0.06f; // 地图移动最快巡航速度
    private final float MOUSE_RELATIVE_SQUARE_ALPHA = 0.02f; // 鼠标参照框透明度

    // 物体选择框
    private ObjectType currentObjectChoice = ObjectType.Wall; // 当前选择的物体

    // 其它部件
    private Image mouseRelativeSquare;

    public MapEditScene(Main gameMain) {
        super(gameMain);
    }

    public void init() {
        super.init();

        SAIManager = new SingleActionInstanceManager(gameMain);

        // 调整上下层按钮
        ImageButtonContainer selectorButtonContainer = new ImageButtonContainer(gameMain);
        layerUpButton = selectorButtonContainer.create(ImageAssets.UpSquareButton);
        layerUpButton.setSize(0.6f, 0.6f);
        layerUpButton.setPosition(0.8f, 4f);
        layerDownButton = selectorButtonContainer.create(ImageAssets.DownSquareButton);
        layerDownButton.setSize(0.6f, 0.6f);
        layerDownButton.setPosition(0.8f, 3f);

        // 顶部下拉菜单
        topMenu = new TopMenu(gameMain, 0.2f);
        topMenu.setPosition(8f, 8.6f);

        initBottomChooser();

        // 鼠标参照框
        mouseRelativeSquare = new Image(gameMain.getAssetsPathManager().get(ImageAssets.WhitePixel));
        mouseRelativeSquare.setSize(1f, 1f);
        mouseRelativeSquare.getColor().a = MOUSE_RELATIVE_SQUARE_ALPHA;
        
        // 初始化地图显示
        map3DGirdWorld = new Stack3DGirdWorld(gameMain, INITIAL_MAP_WIDTH, INITIAL_MAP_HEIGHT, 1f);
        map3DGirdWorld.setPosition(8f, 4.5f);
        map3DGirdWorld.addStack2DLayer(); // 新建一层 2D 层

        // 初始化地图数据
        map = new MapData();
        map.mapFileInfo = new MapFileInfo();
        map.addtionalInfo = "";
        map.allMaps = new ArrayList<>();
        map.allMaps.add(new SubMapData(INITIAL_MAP_HEIGHT, INITIAL_MAP_WIDTH)); // 添加默认层

        initCornerDecoration();

        // 向上移动按钮
        layerUpButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (currentSubMapIndex == 0) return;
                currentSubMapIndex -= 1;
                updateMapShowing();
            }
        });

        // 向下移动按钮
        layerDownButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (currentSubMapIndex == map.allMaps.size() - 1) return;
                currentSubMapIndex += 1;
                updateMapShowing();
            }
        });

        // 地图滚轮、鼠标响应缩放
        stage.addListener(new InputListener() {
            @Override
            public boolean scrolled(InputEvent event, float x, float y, float amountX, float amountY) {
                // 滚轮滚动，amountY 发生变动，更新非固定视口大小与相机
                currentWorldScaling += amountY * 0.08f;
                if (currentWorldScaling < 0.2f) currentWorldScaling = 0.2f;
                if (currentWorldScaling > 2f) currentWorldScaling = 2f;

                // Logger.debug("MapEditScene", String.format("Scroll amountY = %.2f, world scaling = %.2f", amountY, currentWorldScaling));

                viewport.setWorldSize(16f * currentWorldScaling, 9f * currentWorldScaling);
                viewport.apply();
                return true;
            }
        });

        initFileOperatorButtons();

        bgParticle = new BackgroundGrayParticleManager(gameMain, -INITIAL_MAP_WIDTH / 2, -INITIAL_MAP_HEIGHT / 2, INITIAL_MAP_WIDTH / 2, INITIAL_MAP_HEIGHT / 2);
        bgParticle.startCreateParticles();

        ActionUtils.FadeInEffect(layerUpButton);
        ActionUtils.FadeInEffect(layerDownButton);
        topMenu.getAllActors().forEach(ActionUtils::FadeInEffect);

        // 添加 UI 到固定 Stage
        addCombinedObjectToUIStage(topMenu);
        addActorsToUIStage(layerDownButton, layerUpButton);
        
        // 添加 Actor 到非固定 Stage
        stage.addActor(mouseRelativeSquare);
        // 角落标记
        for (int i = 0; i < INITIAL_MAP_WIDTH; i++) {
            for (int j = 0; j < INITIAL_MAP_HEIGHT; j++) {
                addActorsToStage(cornerDecoration[i][j]);
            }
        }

        updateMapShowing(); // 更新当前界面地图显示

        // 为可变舞台增加事件监听
        stage.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                draggedMouseButton = button;
                if (draggedMouseButton == Buttons.MIDDLE) processMiddleClick();
                if (draggedMouseButton == Buttons.LEFT) processLeftClick();
                return true; // 返回 true 捕获后续事件
            }
            
            @Override
            public void touchDragged(InputEvent event, float x, float y, int pointer) {
                if (draggedMouseButton == Buttons.MIDDLE) processMiddleClick();
                if (draggedMouseButton == Buttons.LEFT) processLeftClick();
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                draggedMouseButton = -1; // 重置记录的按钮
            }
        });
    }

    /** 初始化底部菜单 */
    public void initBottomChooser() {
        bottomChooser = new BottomObjectChooser(gameMain);

        // 设置触发事件为设置当前操作对象 -> ObjectType
        bottomChooser.setOnClickEvent(objectTag -> {
            currentObjectChoice = (ObjectType) objectTag;
        });

        bottomChooser.addObject(0, ObjectType.Box, getBoxSpine(SpineAssets.BoxGreenBox), "Green Box");
        bottomChooser.addObject(0, ObjectType.Wall, getBoxSpine(SpineAssets.BoxBlueBox), "Wall");
        bottomChooser.addObject(0, ObjectType.Player, getBoxSpine(SpineAssets.Player1), "Player");
        bottomChooser.addObject(1, ObjectType.BoxTarget, getBoxSpine(SpineAssets.BoxBoxTarget), "Box Target");
        bottomChooser.addObject(1, ObjectType.PlayerTarget, getBoxSpine(SpineAssets.BoxPlayerTarget), "Player Target");
        bottomChooser.addObject(2, ObjectType.GroundDarkGray, getBoxSpine(SpineAssets.BoxDarkGrayBack), "Gray Background");

        bottomChooser.setPosition(8f, 1.6f);

        bottomChooser.getPrevTab().addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                bottomChooser.getAllActors().forEach(Actor::remove);
                bottomChooser.subTabIndex();
                addCombinedObjectToUIStage(bottomChooser);
            }
        });

        bottomChooser.getNextTab().addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                bottomChooser.getAllActors().forEach(Actor::remove);
                bottomChooser.addTabIndex();
                addCombinedObjectToUIStage(bottomChooser);
            }
        });

        bottomChooser.addActorsToStage(UIStage);
    }

    /** 获得 spineAssetes 对应 SpineObject (initBottomChoser) */
    private SpineObject getBoxSpine(SpineAssets spineAssets) {
        SpineObject spine = new SpineObject(gameMain, spineAssets);
        spine.setSize(1.1f, 1.1f);
        return spine;
    }

    /** 初始化角落标记装饰 */
    public void initCornerDecoration() {
        cornerDecoration = new Image[INITIAL_MAP_WIDTH][INITIAL_MAP_HEIGHT];
        ImageLabelContainer cornerContainer = new ImageLabelContainer(gameMain, 0.001f);
        Vector2 corner_position;
        for (int i = 0; i < INITIAL_MAP_WIDTH; i++) {
            for (int j = 0; j < INITIAL_MAP_HEIGHT; j++) {
                corner_position = map3DGirdWorld.getTopLayer().getCellPosition(j, i);
                // 将角落标记装饰加入
                cornerDecoration[i][j] = cornerContainer.create(ImageAssets.LightSquare);
                cornerDecoration[i][j].getColor().a = 0.25f;
                cornerDecoration[i][j].setPosition(corner_position.x - cornerDecoration[i][j].getWidth() / 2, corner_position.y - cornerDecoration[i][j].getHeight() / 2);
                // Logger.info(String.format("%.2f, %.2f", corner_position.x, corner_position.y));
            }
        }
    }

    /** 初始化文件操作按钮回调 */
    public void initFileOperatorButtons() {

        // 返回
        topMenu.getExitButton().addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                exitEditScene();
            }
        });

        // 新建
        topMenu.getNewButton().addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                newFile();
            }
        });

        // 打开
        topMenu.getOpenButton().addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                openFile();
            }
        });

        // 保存
        topMenu.getSaveButton().addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                saveFile(false);
            }
        });

        // 另存为
        topMenu.getSaveAsButton().addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                saveFile(true);
            }
        });

        // 唤起下拉菜单
        topMenu.getPullButton().addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // 获取一个 menu 部件并检查是否静止
                if (!SAIManager.isInAction(topMenu.getAllActors().getFirst())) {

                    // 为每个部件增加动画实例
                    // 如果未处在下拉状态
                    if (!pullDownTopMenu) {
                        // 切换输入处理器
                        Gdx.input.setInputProcessor(UIStage);

                        // 部件下拉动画
                        topMenu.getAllActors().forEach(actor -> SAIManager.executeAction(actor, Actions.moveBy(0, -2.3f, 0.3f, Interpolation.exp10Out)));
                        topMenu.getPullButton().setRotation(180f);
                        topMenu.getPullButton().setPosition(
                            topMenu.getPullButton().getX() + topMenu.getPullButton().getWidth(), 
                            topMenu.getPullButton().getY() + topMenu.getPullButton().getHeight()
                        );
                        
                        pullDownTopMenu = true;

                    } else {
                        // 切换输入处理器
                        inputMultiplexer = new InputMultiplexer();
                        inputMultiplexer.addProcessor(UIStage);
                        inputMultiplexer.addProcessor(stage);
                        Gdx.input.setInputProcessor(inputMultiplexer);

                        // 部件回收动画
                        topMenu.getAllActors().forEach(actor -> SAIManager.executeAction(actor, Actions.moveBy(0, 2.3f, 0.3f, Interpolation.exp10Out)));
                        topMenu.getPullButton().setRotation(0f);
                        topMenu.getPullButton().setPosition(
                            topMenu.getPullButton().getX() - topMenu.getPullButton().getWidth(), 
                            topMenu.getPullButton().getY() - topMenu.getPullButton().getHeight()
                        );
                        
                        pullDownTopMenu = false;
                    }

                }
            }
        });
    }

    /**
     * 显示消息框
     * @param msg 消息内容
     */
    public void ShowMsgBox(String msg) {
        HintMessageBox msgBox;
        msgBox = new HintMessageBox(gameMain, msg);
        msgBox.setPosition(8f, 0.5f);
        msgBox.addActorsToStage(UIStage);
    }

    /**
     * 显示询问框
     * @param hintText 询问文本
     * @param cancel 取消按钮点击回调
     * @param confirm 确认按钮点击回调
     */
    public void ShowQuestBox(String hintText, ClickListener cancel, ClickListener confirm) {
        // 如果当前弹出窗口，直接返回
        if (questWindowShowing) return;

        questWindowShowing = true;
        // 询问是否退出
        QuestDialog questSave = new QuestDialog(gameMain, "Have you saved current file?\n\nIf not, click cancel and save.");
        questSave.setPosition(8f, 4.5f);
        questSave.addActorsToStage(UIStage);

        ClickListener closeQuestBox = new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                questWindowShowing = false;
                // 添加淡出与销毁线程
                questSave.getAllActors().forEach(actor -> actor.addAction(Actions.sequence(
                    Actions.fadeOut(0.2f),
                    Actions.run(() -> questSave.getAllActors().forEach(Actor::remove))
                )));
                questSave.getCancelButton().clearListeners();
                questSave.getConfirmButton().clearListeners();
            }
        };

        // 取消确定均隐藏
        questSave.getCancelButton().addListener(closeQuestBox);
        questSave.getConfirmButton().addListener(closeQuestBox);

        // 添加指定回调
        if (cancel != null) questSave.getCancelButton().addListener(cancel);
        if (confirm != null) questSave.getConfirmButton().addListener(confirm);
    }

    /**
     * 新建文件
     */
    public void newFile() {
        // 询问是否退出新建
        ShowQuestBox(
            "Have you saved current file?\n\nIf not, click cancel and save.", 
            null, 
            new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    gameMain.getScreenManager().setScreenWithoutSaving(new MapEditScene(gameMain));
                }
            }
        );
    }

    /**
     * 打开文件
     */
    public void openFile() {
        String filePath = WindowsFileChooser.selectFile("Map Files (*.map, *.cmap)|*.map;*.cmap|All Files (*.*)|*.*");

        if (filePath == null || filePath.equals("")) {
            Logger.info("MapEditScene", "Select canceled");
            return;
        }
        
        Logger.info("MapEditScene", "Select file path " + filePath);

        // 使用 MapFileReader 读取地图
        String mapString = new MapFileReader().readMapByPath(filePath);
        if (mapString == null) {
            Logger.error("MapEditScene", "Read map file failed.");
            ShowMsgBox("Read map file failed.");
        }

        MapData newMap = MapFileParser.parseMapData(new MapFileInfo(filePath, "", ""), mapString);
        if (newMap == null) {
            Logger.error("MapEditScene", "Parse map file failed.");
            ShowMsgBox("Read map file failed.");
            return;
        }

        ShowQuestBox(
            "Have you saved current file?\n\nIf not, click cancel and save.", 
            null, 
            new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    map = newMap;
                    currentFilePath = filePath;
                    Logger.info("MapEditScene", "Reads map file successfully");

                    // 直接对界面地图进行更新
                    updateMapShowing();
                }
            }
        );
    }

    /**
     * 退出编辑器
     */
    public void exitEditScene() {
        // 询问是否退出
        ShowQuestBox(
            "Do you want to exit?\n\nIf you haven't saved your file, click cancel.", 
            null, 
            new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    gameMain.getScreenManager().returnPreviousScreen();
                }
            }
        );
    }

    /**
     * 保存当前地图编辑内容
     * @param saveAs 是否为另存
     * @return 是否保存成功
     */
    public boolean saveFile(boolean saveAs) {
        if (currentFilePath == null) saveAs = true;

        // 如果是另存为，则打开对话框选择新路径
        if (saveAs) {
            // 打开保存对话框
            String savePath = WindowsFileChooser.saveFile("Map Files (*.map)|*.map");
            
            // 是否确定保存
            if (savePath == null) {
                Logger.info("MapEditScene", "Save canceled");
                return false;
            }

            // 检查拓展名是否存在
            String[] fileNameParts = savePath.split("\\.");
            if (!fileNameParts[fileNameParts.length - 1].toLowerCase().equals("map")) savePath += ".map";

            Logger.info("MapEditScene", "This map will save to " + savePath);
            currentFilePath = savePath;
        }

        // 序列化地图数据
        String mapDataString = MapFileParser.serializeMapData(map);

        if (mapDataString == null) {
            Logger.info("MapEditScene", "Serialize map data failed");
            ShowMsgBox("Fail to save map");
            return false;
        }

        Logger.info("MapEditScene", "This map will save to " + currentFilePath);
        
        // 写入地图文件
        if (new MapFileReader().createMapWithContent(currentFilePath, mapDataString)) {
            Logger.info("MapEditScene", "Save map successfully");
            ShowMsgBox("Save OK ;D");
            return true;
        } else {
            Logger.info("MapEditScene", "Save map failed");
            return false;
        }
    }

    /**
     * 直接更新编辑器界面地图显示
     */
    public void updateMapShowing() {
        SubMapData subMap = map.allMaps.get(currentSubMapIndex);

        // 重置当前网格世界
        map3DGirdWorld.getStack2DLayer(currentSubMapIndex).getAllActors().forEach(actor -> actor.remove());
        map3DGirdWorld.stack3DGridWorld.set(currentSubMapIndex, new Stack2DGirdWorld(gameMain, subMap.width, subMap.height, 1.0f));

        // 获取新网格世界
        Stack2DGirdWorld gridWorld = map3DGirdWorld.getStack2DLayer(currentSubMapIndex);
        for (int layer = 0; layer < subMap.mapLayer.size(); layer++) gridWorld.addLayer();

        // 对于当前子地图的每一层
        for (int layer = 0; layer < subMap.mapLayer.size(); layer++) {
            // 当前层
            ObjectType[][] currentLayer = subMap.mapLayer.get(layer);
            
            for (int y = 0; y < subMap.height; y++) {
                for (int x = 0; x < subMap.width; x++) {
                    // 空气与未知类型不处理
                    if (currentLayer[y][x] == ObjectType.Air || currentLayer[y][x] == ObjectType.Unknown) continue;

                    // 数据类型转换为显示类型
                    BoxType objectBoxType = mapObjectTypeToActor(currentLayer[y][x]);
                    gridWorld.getLayer(layer).addBox(objectBoxType, y, x);
                }
            }
        }

        // 将网格世界重新加入 stage
        addCombinedObjectToStage(gridWorld);
    }

    /**
     * 获得 ObjectType 地图数据的转换类型
     * @param obj ObjectType 物体数据
     * @return BoxType
     */
    public BoxType mapObjectTypeToActor(ObjectType obj) {
        return switch (obj) {
            case ObjectType.Wall -> BoxType.BlueChest;
            case ObjectType.Player -> BoxType.Player;
            case ObjectType.Box -> BoxType.GreenChest;
            case ObjectType.BoxTarget -> BoxType.BoxTarget;
            case ObjectType.PlayerTarget -> BoxType.PlayerTarget;
            case ObjectType.GroundDarkGray -> BoxType.DarkGrayBack;
            default -> null;
        };
    }

    /**
     * 获得 ObjectType 地图数据的物件类型
     * @param obj ObjectType 物体数据
     * @return 对应物体层
     */
    public int mapObjectTypeToLayerIndex(ObjectType obj) {
        return switch (obj) {
            case ObjectType.Wall, ObjectType.Player, ObjectType.Box -> SubMapData.LAYER_OBJECT;
            case ObjectType.BoxTarget, ObjectType.PlayerTarget -> SubMapData.LAYER_TARGET;
            case ObjectType.GroundDarkGray -> SubMapData.LAYER_DECORATION;
            default -> 0;
        };
    }

    /** 输入事件处理 */
    @Override
    public void input() {
        boolean controlPressed = Gdx.input.isKeyPressed(Keys.CONTROL_LEFT) || Gdx.input.isKeyPressed(Keys.CONTROL_RIGHT);
        boolean shiftPressed = Gdx.input.isKeyPressed(Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Keys.SHIFT_RIGHT);

        // 退出
        if (Gdx.input.isKeyJustPressed(Keys.ESCAPE)) {
            exitEditScene();
            return;
        }

        // 保存
        if (controlPressed && Gdx.input.isKeyJustPressed(Keys.S)) {
            saveFile(false);
            return;
        }

        // 另存为
        if (controlPressed && shiftPressed && Gdx.input.isKeyJustPressed(Keys.S)) {
            saveFile(true);
            return;
        }

        // 打开
        if (controlPressed && Gdx.input.isKeyJustPressed(Keys.O)) {
            openFile();
            return;
        }

        // 新建
        if (controlPressed && Gdx.input.isKeyJustPressed(Keys.N)) {
            newFile();
            return;
        }

        // 视口右键移动判定
        processViewportMoving();

        // 鼠标移动参照框
        mouseRelativeSquareMoving();
    }

    /*
     * 获得鼠标当前指向的 GridWorld X 坐标，超界返回 -1
     */
    public int getCoordinateX() {
        float posX = Gdx.input.getX(), posY = Gdx.input.getY();
        Vector2 worldPosition = viewport.unproject(new Vector2(posX, posY));
        return MathUtilsEx.caculateMouseGridAxis(worldPosition.x, map3DGirdWorld.getX(), INITIAL_MAP_WIDTH, 1.0f);
    }

    /*
     * 获得鼠标当前指向的 GridWorld Y 坐标，超界返回 -1
     */
    public int getCoordinateY() {
        float posX = Gdx.input.getX(), posY = Gdx.input.getY();
        Vector2 worldPosition = viewport.unproject(new Vector2(posX, posY));
        return MathUtilsEx.caculateMouseGridAxis(worldPosition.y, map3DGirdWorld.getY(), INITIAL_MAP_HEIGHT, 1.0f);
    }

    /**
     * 处理鼠标参照框移动
     */
    private void mouseRelativeSquareMoving() {
        int coordinateX = getCoordinateX();
        int coordinateY = getCoordinateY();

        // 坐标合法检查
        if (coordinateX != -1 && coordinateY != -1) {
            Vector2 coordVector = map3DGirdWorld.getTopLayer().getCellPosition(coordinateY, coordinateX);
            mouseRelativeSquare.setPosition(coordVector.x, coordVector.y);
        }

        if (pullDownTopMenu) mouseRelativeSquare.getColor().a = 0f;
        else mouseRelativeSquare.getColor().a = MOUSE_RELATIVE_SQUARE_ALPHA;
    }

    /**
     * 左键处理鼠标指定位置物体
     */
    public void processLeftClick() {
        int coordinateX = getCoordinateX();
        int coordinateY = getCoordinateY();

        Logger.debug("MapEditScene", String.format("Left Click (%d, %d)", coordinateX, coordinateY));

        // 坐标合法检查
        if (coordinateX != -1 && coordinateY != -1) {
            // 当前地图
            SubMapData currentSubMap = map.allMaps.get(currentSubMapIndex);

            // 判断当前层物体存在性
            boolean thingExists = currentSubMap.mapLayer.get(mapObjectTypeToLayerIndex(currentObjectChoice))[coordinateY][coordinateX] != ObjectType.Air;
            if (!thingExists) {
                // 向对应层添加对应物体
                map.allMaps.get(currentSubMapIndex).mapLayer.get(mapObjectTypeToLayerIndex(currentObjectChoice))[coordinateY][coordinateX] = currentObjectChoice;
            } else {
                // 也许有一些处理
            }
        }

        // 更新画面
        updateMapShowing();
    }

    /**
     * 中键处理鼠标指定位置物体
     */
    public void processMiddleClick() {
        int coordinateX = getCoordinateX();
        int coordinateY = getCoordinateY();

        Logger.debug("MapEditScene", String.format("Middle Click (%d, %d)", coordinateX, coordinateY));

        // 坐标合法检查
        if (coordinateX != -1 && coordinateY != -1) {
            // 将所有层都清除为空气
            for (ObjectType[][] layer : map.allMaps.get(currentSubMapIndex).mapLayer) layer[coordinateY][coordinateX] = ObjectType.Air;
        }

        // 更新画面
        updateMapShowing();
    }
     
    /**
     * 处理鼠标右键引发的视口移动
     */
    private void processViewportMoving() {
        // 处理鼠标右键拖动，变换可变视口
        if (Gdx.input.isButtonPressed(Input.Buttons.RIGHT)) {
            // 如果是第一次按下右键
            if (!isDragging) {

                // 相对于未改变的视口，鼠标初始位置的世界坐标
                lastMouseOriginalX = Gdx.input.getX();
                lastMouseOriginalY = Gdx.input.getY();
                lastMousePosition.set(viewport.unproject(new Vector2(lastMouseOriginalX, lastMouseOriginalY)));
                // 设置拖动标志
                isDragging = true;

            // 正在拖动
            } else {

                // 获取当前鼠标位置以及相对位移，这里用原始视口进行坐标变换
                Vector2 currentMousePosition = viewport.unproject(new Vector2(Gdx.input.getX(), Gdx.input.getY()));
                // 相对于变化的视口，鼠标初始位置的世界坐标要更新
                lastMousePosition.set(viewport.unproject(new Vector2(lastMouseOriginalX, lastMouseOriginalY)));

                // 相对位移
                Vector2 deltaPosition = currentMousePosition.cpy().sub(lastMousePosition).scl(MAX_MOVING_PACE);
                if (deltaPosition.len() > 0.1f) deltaPosition.setLength(0.1f);
                
                // 视口位移向量 = 初始视口位置 + 鼠标位移
                Vector2 newViewPosition = new Vector2(viewport.getCamera().position.x, viewport.getCamera().position.y).sub(deltaPosition);
                
                // 边界
                newViewPosition.x = Math.clamp(newViewPosition.x, -INITIAL_MAP_WIDTH * 1.0f / 2, INITIAL_MAP_WIDTH * 1.0f / 2);
                newViewPosition.y = Math.clamp(newViewPosition.y, -INITIAL_MAP_HEIGHT * 1.0f / 2, INITIAL_MAP_HEIGHT * 1.0f / 2);

                viewport.getCamera().position.set(newViewPosition, 0);
                viewport.getCamera().update();
            }

        // 如果没有拖放
        } else {
            isDragging = false;
        }
    }

    @Override
    public void draw(float delta) {
        // 固定 Stage 与可变 Stage 绘制
        stage.draw();
        UIStage.draw();
    }

    @Override
    public void logic(float delta) {}

    @Override
    public void hide() {}

}
