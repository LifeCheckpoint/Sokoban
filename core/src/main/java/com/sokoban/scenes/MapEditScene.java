package com.sokoban.scenes;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.sokoban.Main;
import com.sokoban.assets.ImageAssets;
import com.sokoban.core.Logger;
import com.sokoban.core.game.ObjectType;
import com.sokoban.core.map.MapData;
import com.sokoban.core.map.MapFileInfo;
import com.sokoban.core.map.MapFileParser;
import com.sokoban.core.map.MapFileReader;
import com.sokoban.core.map.SubMapData;
import com.sokoban.polygon.BoxObject.BoxType;
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
    private String currentFilePath = null; // 当前文件路径
    private float currentWorldScaling = 1.0f; // 当前世界缩放大小
    private boolean isDragging = false; // 是否正在拖动可变视口
    private Vector2 lastMousePosition = new Vector2(); // 鼠标拖动起始点
    private float lastMouseOriginalX, lastMouseOriginalY; // 鼠标拖动原始坐标

    // 按钮 菜单
    private Image layerUpButton, layerDownButton;
    private TopMenu topMenu;

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

                        topMenu.getAllActors().forEach(actor -> SAIManager.executeAction(actor, Actions.moveBy(0, -2.3f, 0.3f, Interpolation.exp10Out)));
                        pullDownTopMenu = true;
                    } else {
                        // 切换输入处理器
                        inputMultiplexer = new InputMultiplexer();
                        inputMultiplexer.addProcessor(UIStage);
                        inputMultiplexer.addProcessor(stage);
                        Gdx.input.setInputProcessor(inputMultiplexer);

                        topMenu.getAllActors().forEach(actor -> SAIManager.executeAction(actor, Actions.moveBy(0, 2.3f, 0.3f, Interpolation.exp10Out)));
                        pullDownTopMenu = false;
                    }

                }
            }
        });

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

        updateMapShowing(); // 更新当前界面地图显示

        // 角落标记
        for (int i = 0; i < INITIAL_MAP_WIDTH; i++) {
            for (int j = 0; j < INITIAL_MAP_HEIGHT; j++) {
                addActorsToStage(cornerDecoration[i][j]);
            }
        }

        // 为可变舞台增加事件监听
        stage.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                processLeftClick();
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
     * 新建文件
     */
    public void newFile() {
        // 询问是否退出
        QuestDialog questSave = new QuestDialog(gameMain, "Have you saved current file?\n\nIf not, click cancel and save.");
        questSave.setPosition(8f, 4.5f);
        questSave.addActorsToStage(UIStage);

        // 取消则隐藏
        questSave.getCancelButton().addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                questSave.getAllActors().forEach(actor -> actor.addAction(Actions.fadeOut(0.2f)));
                questSave.getCancelButton().clearListeners();
                questSave.getConfirmButton().clearListeners();
            }
        });

        // 确定则直接创建新编辑场景
        questSave.getConfirmButton().addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                gameMain.getScreenManager().setScreenWithoutSaving(new MapEditScene(gameMain));
            }
        });
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

        map = MapFileParser.parseMapData(new MapFileInfo(filePath, "", ""), mapString);
        if (map == null) {
            Logger.error("MapEditScene", "Parse map file failed.");
            ShowMsgBox("Read map file failed.");
            return;
        }

        currentFilePath = filePath;
        Logger.info("MapEditScene", "Reads map file successfully");

        // 直接对界面地图进行更新
        updateMapShowing();
    }

    /**
     * 退出编辑器
     */
    public void exitEditScene() {
        // 询问是否退出
        QuestDialog questSave = new QuestDialog(gameMain, "Do you want to exit?\n\nIf you haven't saved your file, click cancel.");
        questSave.setPosition(8f, 4.5f);
        questSave.addActorsToStage(UIStage);

        // 取消则隐藏
        questSave.getCancelButton().addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                questSave.getAllActors().forEach(actor -> actor.addAction(Actions.fadeOut(0.2f)));
                questSave.getCancelButton().clearListeners();
                questSave.getConfirmButton().clearListeners();
            }
        });

        // 确定则退出
        questSave.getConfirmButton().addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                gameMain.getScreenManager().returnPreviousScreen();
            }
        });
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

        // 对于当前子地图的每一层
        for (int layer = 0; layer < subMap.mapLayer.size(); layer++) {

            // 当前层
            gridWorld.addLayer();
            ObjectType[][] currentLayer = subMap.mapLayer.get(layer);
            
            for (int y = 0; y < subMap.height; y++) {
                for (int x = 0; x < subMap.width; x++) {
                    // 空气与未知类型不处理
                    if (currentLayer[y][x] == ObjectType.Air || currentLayer[y][x] == ObjectType.Unknown) continue;

                    // 数据类型转换为显示类型
                    BoxType objectBoxType = mapObjectTypeToActor(currentLayer[y][x]);
                    gridWorld.getTopLayer().addBox(objectBoxType, y, x);
                }
            }
        }

        // 将网格世界重新加入 stage
        addCombinedObjectToStage(gridWorld);
    }

    /**
     * 获得 ObjectType 地图数据的转换类型
     * @param obj ObjectType 物体数据
     * @return "Spine" "BoxType"
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
     * 处理鼠标指定位置物体
     */
    public void processLeftClick() {
        int coordinateX = getCoordinateX();
        int coordinateY = getCoordinateY();

        Logger.debug("MapEditScene", String.format("Left Click (%d, %d)", coordinateX, coordinateY));

        // 坐标合法检查
        if (coordinateX != -1 && coordinateY != -1) {
            // TODO 如果当前物体是 ... 层
            map.allMaps.get(currentSubMapIndex).getObjectLayer()[coordinateY][coordinateX] = currentObjectChoice;
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
