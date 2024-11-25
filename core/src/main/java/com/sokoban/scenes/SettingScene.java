package com.sokoban.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.sokoban.Main;
import com.sokoban.assets.ImageAssets;
import com.sokoban.core.Logger;
import com.sokoban.core.settings.GameSettings;
import com.sokoban.core.settings.GraphicsSettings;
import com.sokoban.core.settings.SoundSettings;
import com.sokoban.manager.BackgroundGrayParticleManager;
import com.sokoban.polygon.actioninterface.ValueUpdateCallback;
import com.sokoban.polygon.combine.CheckboxObject;
import com.sokoban.polygon.combine.CombinedNumberDisplayObject;
import com.sokoban.polygon.combine.HintMessageBox;
import com.sokoban.polygon.combine.SliderObject;
import com.sokoban.polygon.container.ImageButtonContainer;
import com.sokoban.utils.ActionUtils;

/**
 * 设置界面
 * @author Life_Checkpoint
 */
public class SettingScene extends SokobanScene {

    // Background 粒子
    private BackgroundGrayParticleManager bgParticle;

    // UI
    private ImageButtonContainer buttonContainer;
    private ImageButtonContainer buttonContainer2;
    private Image returnButton, saveButton;

    private CheckboxObject mipmapCheckbox;
    private CheckboxObject vsyncCheckbox;
    private Image msaaButton;
    private CombinedNumberDisplayObject msaaValueDisplayer;

    private SliderObject masterVolumeSlider;
    private SliderObject musicVolumeSlider;
    private SliderObject effectsVolumeSlider;

    // 当前设置
    GameSettings currentSettings;

    public SettingScene(Main gameMain) {
        super(gameMain);
    }

    public void init() {
        super.init();

        buttonContainer = new ImageButtonContainer(gameMain);
        buttonContainer2 = new ImageButtonContainer(gameMain, 0.004f);

        // 返回 保存
        returnButton = buttonContainer.create(ImageAssets.LeftArrowButton);
        returnButton.setPosition(0.3f, 8.3f);
        
        saveButton = buttonContainer.create(ImageAssets.SaveButton);
        saveButton.setPosition(-20f, -20f); // 暂时不可见
        saveButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                saveSettings();
            }
        });

        returnButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                gameMain.getScreenManager().returnPreviousScreen();
            }
        });

        currentSettings = gameMain.getSettingManager().gameSettings;

        // Mipmap 设置
        mipmapCheckbox = new CheckboxObject(gameMain, ImageAssets.Mipmap, currentSettings.graphics.mipmap, true, 0.16f);
        mipmapCheckbox.setPosition(2f, 7f);
        mipmapCheckbox.setCheckboxType(true);

        // 垂直同步设置
        vsyncCheckbox = new CheckboxObject(gameMain, ImageAssets.Vsync, currentSettings.graphics.vsync, true, 0.16f);
        vsyncCheckbox.setPosition(2f, 6.2f);
        vsyncCheckbox.setCheckboxType(true);

        // MSAA 设置
        msaaButton = buttonContainer2.create(ImageAssets.MSAA);
        msaaButton.setPosition(2f, 5.4f);
        msaaValueDisplayer = new CombinedNumberDisplayObject(gameMain, 2, 0, currentSettings.graphics.msaa);
        msaaValueDisplayer.setPosition(msaaButton.getX() + msaaButton.getWidth() + 0.2f, 5.4f);
        msaaValueDisplayer.setShowDecimalPoint(false);
        msaaValueDisplayer.setShowFirstZeros(false);

        msaaButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                switch (currentSettings.graphics.msaa) {
                    case 0:
                        currentSettings.graphics.msaa = 2;
                        break;
                    case 2:
                        currentSettings.graphics.msaa = 4;
                        break;
                    case 4:
                        currentSettings.graphics.msaa = 8;
                        break;
                    case 8:
                        currentSettings.graphics.msaa = 16;
                        break;
                    case 16:
                        currentSettings.graphics.msaa = 32;
                        break;
                    case 32:
                        currentSettings.graphics.msaa = 0;
                        break;    
                }

                msaaValueDisplayer.setValue((float) currentSettings.graphics.msaa);
            }
        });

        // 音量设置
        masterVolumeSlider = new SliderObject(gameMain, ImageAssets.MasterVolume, 
                                0f, 100f, currentSettings.sound.masterVolume * 100, 3, 0);

                                masterVolumeSlider.setPosition(9f, 7f);
        musicVolumeSlider = new SliderObject(gameMain, ImageAssets.MusicVolume, 
                                0f, 100f, currentSettings.sound.musicVolume * 100, 3, 0);
        musicVolumeSlider.setPosition(9f, 6.2f);
        effectsVolumeSlider = new SliderObject(gameMain, ImageAssets.EffectsVolume, 
                                0f, 100f, currentSettings.sound.effectsVolume * 100, 3, 0);
        effectsVolumeSlider.setPosition(9f, 5.4f);

        masterVolumeSlider.setActionWhenValueUpdate(new ValueUpdateCallback() {
            @Override
            public void onValueUpdate(float value) {
                gameMain.getMusicManager().setVolume(value * musicVolumeSlider.getSlider().getValue());
            }
        });
        musicVolumeSlider.setActionWhenValueUpdate(new ValueUpdateCallback() {
            @Override
            public void onValueUpdate(float value) {
                gameMain.getMusicManager().setVolume(value * masterVolumeSlider.getSlider().getValue());
            }
        });
        effectsVolumeSlider.setActionWhenValueUpdate(new ValueUpdateCallback() {
            @Override
            public void onValueUpdate(float value) {
                // TODO
            }
        });


        bgParticle = new BackgroundGrayParticleManager(gameMain);
        bgParticle.startCreateParticles();

        // 添加 UI
        addActorsToStage(returnButton, saveButton);

        addCombinedObjectToStage(mipmapCheckbox, vsyncCheckbox, msaaValueDisplayer);
        addActorsToStage(msaaButton);

        addCombinedObjectToStage(masterVolumeSlider, musicVolumeSlider, effectsVolumeSlider);

        // 设置淡入动画
        ActionUtils.FadeInEffect(returnButton);

        mipmapCheckbox.getAllActors().forEach(ActionUtils::FadeInEffect);
        vsyncCheckbox.getAllActors().forEach(ActionUtils::FadeInEffect);
        ActionUtils.FadeInEffect(msaaButton);
        msaaValueDisplayer.getAllActors().forEach(ActionUtils::FadeInEffect);

        masterVolumeSlider.getAllActors().forEach(ActionUtils::FadeInEffect);
        musicVolumeSlider.getAllActors().forEach(ActionUtils::FadeInEffect);
        effectsVolumeSlider.getAllActors().forEach(ActionUtils::FadeInEffect);
    }

    // 输入事件处理
    @Override
    public void input() {
        // 保存
        if (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT) && Gdx.input.isKeyJustPressed(Input.Keys.S) && isSettingUpdated()) {
            saveSettings();
        }

        // 退出
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            gameMain.getScreenManager().returnPreviousScreen();
        }
    }

    private boolean isSettingUpdated() {
        return !getCurrentSettings().equals(gameMain.getSettingManager().gameSettings);
    }

    /**
     * 获得当前设置
     * @return 当前设置
     */
    private GameSettings getCurrentSettings() {
        currentSettings = new GameSettings();
        GraphicsSettings graphicsSet = currentSettings.graphics;
        SoundSettings soundSet = currentSettings.sound;

        graphicsSet.mipmap = mipmapCheckbox.getChecked();
        graphicsSet.vsync = vsyncCheckbox.getChecked();
        graphicsSet.msaa = msaaValueDisplayer.getShowingValueInt();

        soundSet.masterVolume = masterVolumeSlider.getSlider().getValue();
        soundSet.musicVolume = musicVolumeSlider.getSlider().getValue();
        soundSet.effectsVolume = effectsVolumeSlider.getSlider().getValue();

        return currentSettings;
    }

    private void saveSettings() {
        Logger.info("SettingScene", "settings would be saved.");

        HintMessageBox saveSettingsHintBox = new HintMessageBox(gameMain, "Settings saved.");
        saveSettingsHintBox.setPosition(8f, 0.5f);
        saveSettingsHintBox.addActorsToStage(stage);

        gameMain.getSettingManager().gameSettings = getCurrentSettings();
        gameMain.getSettingManager().writeSettings();
    }

    // 重绘逻辑
    @Override
    public void draw(float delta) {
        // 检查是否有设置更新
        if (isSettingUpdated()) {
            saveButton.setPosition(0.8f, 7f);
        } else {
            saveButton.setPosition(-20f, -20f);
        }
        stage.draw();
    }

    @Override
    public void logic(float delta) {}

    @Override
    public void hide() {
        // 从预览恢复原先的音乐音量
        gameMain.getMusicManager().setVolume(gameMain.getSettingManager().gameSettings.sound.masterVolume * gameMain.getSettingManager().gameSettings.sound.musicVolume);
    }

    @Override
    public void dispose() {
        super.dispose();
    }
}