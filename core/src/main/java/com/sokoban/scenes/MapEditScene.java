package com.sokoban.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
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
import com.sokoban.polygon.combine.HintMessageBox;
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

    private final int INITIAL_MAP_WIDTH = 48;
    private final int INITIAL_MAP_HEIGHT = 27;
    private final float MAX_MOVING_PACE = 0.06f; // 地图移动最快巡航速度
    private final float MOUSE_RELATIVE_SQUARE_ALPHA = 0.02f; // 鼠标参照框透明度

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
        
        // 初始化地图
        map3DGirdWorld = new Stack3DGirdWorld(gameMain, INITIAL_MAP_WIDTH, INITIAL_MAP_HEIGHT, 1f);
        map3DGirdWorld.setPosition(8f, 4.5f);
        map3DGirdWorld.addStack2DLayer(); // 新建一层 2D 层

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
                if (saveFile(false)) {
                    HintMessageBox msgBox;
                    msgBox = new HintMessageBox(gameMain, "Save OK ;D");
                    msgBox.setPosition(8f, 0.5f);
                    msgBox.addActorsToStage(stage);
                }
            }
        });

        // 另存为
        topMenu.getSaveAsButton().addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                HintMessageBox msgBox;

                if (saveFile(true)) {
                    msgBox = new HintMessageBox(gameMain, "Save OK ;D");
                } else {
                    msgBox = new HintMessageBox(gameMain, "Fail or cancel to save map");
                }

                msgBox.setPosition(8f, 0.5f);
                msgBox.addActorsToStage(stage);
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
                        topMenu.getAllActors().forEach(actor -> SAIManager.executeAction(actor, Actions.moveBy(0, -2.3f, 0.3f, Interpolation.exp10Out)));
                        pullDownTopMenu = true;
                    } else {
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

        // 角落标记
        for (int i = 0; i < INITIAL_MAP_WIDTH; i++) {
            for (int j = 0; j < INITIAL_MAP_HEIGHT; j++) {
                // FIXME: Exception in thread "main" java.lang.NullPointerException: "this.stage" is null
                // addActorsToStage(cornerDecoration[i][j]);
                stage.addActor(cornerDecoration[i][j]);
            }
        }
    }

    /**
     * 新建文件
     */
    public void newFile() {
        // TODO 询问保存，保存逻辑
        // Save file?

        // 直接创建新编辑场景
        gameMain.getScreenManager().setScreenWithoutSaving(new MapEditScene(gameMain));
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

        // TODO 读取地图逻辑
        if (true) {
            currentFilePath = filePath;
            Logger.info("MapEditScene", "Reads map file successfully");
        } else {
            Logger.error("MapEditScene", "Loads map file failed. Check the map format or else");
        }

    }

    /**
     * 退出编辑器
     */
    public void exitEditScene() {
        // 如果当前打开了文件
        // TODO 询问保存，保存逻辑

        gameMain.getScreenManager().returnPreviousScreen();
    }

    /**
     * 保存当前地图编辑内容
     * @param saveAs 是否为另存
     * @return 是否保存成功
     */
    public boolean saveFile(boolean saveAs) {
        // if (!currentFileOpen) return false;
        if (currentFilePath == null) saveAs = true;
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
            
            // TODO 尝试写入地图文件
            if (true) {
                currentFilePath = savePath;
                Logger.info("MapEditScene", "Save map successfully");
                return true;
            } else {
                Logger.info("MapEditScene", "Save map failed");
                return false;
            }

        } else {
            // 直接向路径中写入
            Logger.info("MapEditScene", "This map will save to " + currentFilePath);

            // TODO 写入地图文件尝试
            if (true) {
                Logger.info("MapEditScene", "Save map successfully");
                return true;
            } else {
                Logger.info("MapEditScene", "Save map failed");
                return false;
            }
        }
    }

    @Override
    public void input() {
        // 退出
        if (Gdx.input.isKeyJustPressed(Keys.ESCAPE)) {
            exitEditScene();
        }

        // 视口右键移动判定
        processViewportMoving();

        // 鼠标移动参照框
        mouseRelativeSquareMoving();

        // 在下拉菜单缩回时，允许处理以下事件
        if (!pullDownTopMenu) {

        }
    }

    /**
     * 处理鼠标参照框移动
     */
    private void mouseRelativeSquareMoving() {
        float posX = Gdx.input.getX(), posY = Gdx.input.getY();
        Vector2 worldPosition = viewport.unproject(new Vector2(posX, posY));
        int coordinateX = MathUtilsEx.caculateMouseGridAxis(worldPosition.x, map3DGirdWorld.getX(), INITIAL_MAP_WIDTH, 1.0f);
        int coordinateY = MathUtilsEx.caculateMouseGridAxis(worldPosition.y, map3DGirdWorld.getY(), INITIAL_MAP_HEIGHT, 1.0f);

        // 坐标合法检查
        if (coordinateX != -1 && coordinateY != -1) {
            Vector2 coordVector = map3DGirdWorld.getTopLayer().getCellPosition(coordinateY, coordinateX);
            mouseRelativeSquare.setPosition(coordVector.x, coordVector.y);
        }

        if (pullDownTopMenu) mouseRelativeSquare.getColor().a = 0f;
        else mouseRelativeSquare.getColor().a = MOUSE_RELATIVE_SQUARE_ALPHA;

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
        UIStage.draw();
        stage.draw();
    }

    @Override
    public void logic(float delta) {}

    @Override
    public void hide() {}

}