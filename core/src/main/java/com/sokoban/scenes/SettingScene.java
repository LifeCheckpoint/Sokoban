package com.sokoban.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.sokoban.Main;
import com.sokoban.core.settings.GraphicsSettings;
import com.sokoban.core.settings.SoundSettings;
import com.sokoban.manager.APManager;
import com.sokoban.manager.BackgroundGrayParticleManager;
import com.sokoban.polygon.actioninterface.ValueUpdateCallback;
import com.sokoban.polygon.combine.CheckboxObject;
import com.sokoban.polygon.combine.SliderObject;
import com.sokoban.polygon.container.ImageButtonContainer;

/**
 * 设置界面
 * @author Life_Checkpoint
 */
public class SettingScene extends SokoyoScene {

    // Background 粒子
    private BackgroundGrayParticleManager bgParticle;

    // UI
    private ImageButtonContainer buttonContainer;
    private Image returnButton;

    private CheckboxObject mipmapCheckbox;
    private CheckboxObject vsyncCheckbox;

    private SliderObject masterVolumeSlider;
    private SliderObject musicVolumeSlider;
    private SliderObject effectsVolumeSlider;

    public SettingScene(Main gameMain) {
        super(gameMain);
    }

    public void init() {
        super.init();

        buttonContainer = new ImageButtonContainer(gameMain);

        // 返回按钮
        returnButton = buttonContainer.create(APManager.ImageAssets.LeftArrowButton);
        returnButton.setPosition(0.3f, 8.3f);
        returnButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("Return!");
                gameMain.getScreenManager().returnPreviousScreen();
            }
        });

        // Mipmap 设置
        mipmapCheckbox = new CheckboxObject(gameMain, APManager.ImageAssets.Mipmap, gameMain.getSettingManager().gameSettings.graphics.mipmap, true, 0.16f);
        mipmapCheckbox.setPosition(2f, 7f);
        mipmapCheckbox.setCheckboxType(true);

        // 垂直同步设置
        vsyncCheckbox = new CheckboxObject(gameMain, APManager.ImageAssets.Vsync, gameMain.getSettingManager().gameSettings.graphics.vsync, true, 0.16f);
        vsyncCheckbox.setPosition(2f, 6f);
        vsyncCheckbox.setCheckboxType(true);

        // 音量设置
        masterVolumeSlider = new SliderObject(gameMain, APManager.ImageAssets.MasterVolume, 
                                0f, 100f, gameMain.getSettingManager().gameSettings.sound.masterVolume * 100, 3, 1);
        masterVolumeSlider.setPosition(2f, 5f);
        musicVolumeSlider = new SliderObject(gameMain, APManager.ImageAssets.MusicVolume, 
                                0f, 100f, gameMain.getSettingManager().gameSettings.sound.musicVolume * 100, 3, 1);
        musicVolumeSlider.setPosition(2f, 4.2f);
        effectsVolumeSlider = new SliderObject(gameMain, APManager.ImageAssets.EffectsVolume, 
                                0f, 100f, gameMain.getSettingManager().gameSettings.sound.effectsVolume * 100, 3, 1);
        effectsVolumeSlider.setPosition(2f, 3.4f);

        // 滑块条响应
        masterVolumeSlider.setActionWhenValueUpdate(new ValueUpdateCallback() {
            @Override
            public void onValueUpdate(float value) {
                gameMain.getMusicManager().setVolume(value * musicVolumeSlider.getSlider().getValue());
            }
        });
        musicVolumeSlider.setActionWhenValueUpdate(new ValueUpdateCallback() {
            @Override
            public void onValueUpdate(float value) {
                gameMain.getMusicManager().setVolume(value);
            }
        });
        effectsVolumeSlider.setActionWhenValueUpdate(new ValueUpdateCallback() {
            @Override
            public void onValueUpdate(float value) {
                gameMain.getMusicManager().setVolume(value * masterVolumeSlider.getSlider().getValue());
            }
        });


        bgParticle = new BackgroundGrayParticleManager(gameMain);
        bgParticle.startCreateParticles();

        // 添加 UI
        stage.addActor(returnButton);
        stage.addActor(mipmapCheckbox.getCheckbox());
        stage.addActor(mipmapCheckbox.getCheckboxText());
        stage.addActor(vsyncCheckbox.getCheckbox());
        stage.addActor(vsyncCheckbox.getCheckboxText());
        masterVolumeSlider.addActorsToStage(stage);
        musicVolumeSlider.addActorsToStage(stage);
        effectsVolumeSlider.addActorsToStage(stage);
    }

    // 输入事件处理
    private void input() {
        if (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT) && Gdx.input.isKeyJustPressed(Input.Keys.S)) {
            GraphicsSettings graphicsSet = gameMain.getSettingManager().gameSettings.graphics;
            SoundSettings soundSet = gameMain.getSettingManager().gameSettings.sound;

            graphicsSet.mipmap = mipmapCheckbox.getCheckbox().getChecked();
            graphicsSet.vsync = vsyncCheckbox.getCheckbox().getChecked();

            soundSet.masterVolume = masterVolumeSlider.getSlider().getValue();
            soundSet.musicVolume = musicVolumeSlider.getSlider().getValue();
            soundSet.effectsVolume = effectsVolumeSlider.getSlider().getValue();

            gameMain.getSettingManager().writeSettings();
            System.out.println("Save");
        }
    }

    // 重绘逻辑
    private void draw() {
        // stage 更新
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    // 主渲染帧
    @Override
    public void render(float delta) {
        input();
        draw();
    }

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        super.dispose();
    }
}