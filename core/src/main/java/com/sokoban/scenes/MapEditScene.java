package com.sokoban.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.sokoban.Main;
import com.sokoban.assets.ImageAssets;
import com.sokoban.core.Logger;
import com.sokoban.polygon.combine.HintMessageBox;
import com.sokoban.polygon.combine.TopMenu;
import com.sokoban.polygon.container.ImageButtonContainer;
import com.sokoban.polygon.manager.BackgroundGrayParticleManager;
import com.sokoban.polygon.manager.SingleActionInstanceManager;
import com.sokoban.utils.ActionUtils;
import com.sokoban.utils.WindowsFileChooser;

/**
 * 地图编辑界面
 * @author Life_Checkpoint
 */
public class MapEditScene extends SokobanScene {
    // 辅助功能
    private BackgroundGrayParticleManager bgParticle;
    private SingleActionInstanceManager SAIManager;

    // 状态控制
    private boolean pullDownTopMenu = false;
    private String currentFilePath = null;

    // 按钮
    private Image layerUpButton, layerDownButton;

    // 菜单
    private TopMenu topMenu;

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

        topMenu = new TopMenu(gameMain, 0.2f);
        topMenu.setPosition(8f, 8.7f); // 将下拉菜单放在顶部，留出下拉按钮

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
                HintMessageBox msgBox;

                if (saveFile(false)) {
                    msgBox = new HintMessageBox(gameMain, "Save OK ;D");
                } else {
                    msgBox = new HintMessageBox(gameMain, "Fail or cancel to save map");
                }

                msgBox.setPosition(8f, 0.5f);
                msgBox.addActorsToStage(stage);
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
                        topMenu.getAllActors().forEach(actor -> SAIManager.executeAction(actor, Actions.moveBy(0, -2.5f, 0.3f, Interpolation.exp10Out)));
                        pullDownTopMenu = true;
                    } else {
                        topMenu.getAllActors().forEach(actor -> SAIManager.executeAction(actor, Actions.moveBy(0, 2.5f, 0.3f, Interpolation.exp10Out)));
                        pullDownTopMenu = false;
                    }

                }
            }
        });

        bgParticle = new BackgroundGrayParticleManager(gameMain);
        bgParticle.startCreateParticles();

        ActionUtils.FadeInEffect(layerUpButton);
        ActionUtils.FadeInEffect(layerDownButton);
        topMenu.getAllActors().forEach(ActionUtils::FadeInEffect);

        // 添加 UI
        addActorsToStage(layerDownButton, layerUpButton);
        addCombinedObjectToStage(topMenu);
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
    }

    @Override
    public void draw(float delta) {
        
        stage.draw();
    }

    @Override
    public void logic(float delta) {}

    @Override
    public void hide() {}

    // 资源释放
    @Override
    public void dispose() {
        super.dispose();
    }
}