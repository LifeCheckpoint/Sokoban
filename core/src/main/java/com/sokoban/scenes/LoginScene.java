package com.sokoban.scenes;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.sokoban.Main;
import com.sokoban.manager.APManager.ImageAssets;
import com.sokoban.manager.BackgroundGrayParticleManager;
import com.sokoban.polygon.InputTextField;
import com.sokoban.polygon.combine.CheckboxObject;
import com.sokoban.polygon.container.ImageButtonContainer;
import com.sokoban.utils.ActionUtils;

/**
 * 用户登录界面
 * <br><br>
 * 其实这不太符合单机游戏的最佳实践，只是因为在 Java 项目需要这么开发罢了
 * @author Life_Checkpoint
 */
public class LoginScene extends SokobanScene {
    private BackgroundGrayParticleManager bgParticle;

    private InputTextField UserNameField;
    private InputTextField PasswordField;
    private CheckboxObject rememberPasswordCheckbox;
    private Image cancelButton;
    private Image loginButton;
    private Image guestModeButton;

    public LoginScene(Main gameMain) {
        super(gameMain);
    }

    @Override
    public void init() {
        super.init();

        bgParticle = new BackgroundGrayParticleManager(gameMain);
        bgParticle.startCreateParticles();

        // 按钮
        ImageButtonContainer buttonContainer = new ImageButtonContainer(gameMain);
        cancelButton = buttonContainer.create(ImageAssets.CancelButton);
        cancelButton.setPosition(6.5f - cancelButton.getWidth() / 2, 2f);
        loginButton = buttonContainer.create(ImageAssets.CancelButton);
        loginButton.setPosition(9.5f - loginButton.getWidth() / 2, 2f);
        guestModeButton = buttonContainer.create(ImageAssets.GuestModeButton);
        guestModeButton.setPosition(8f - guestModeButton.getWidth() / 2, 1.3f);

        rememberPasswordCheckbox = new CheckboxObject(gameMain, ImageAssets.RememberPasswordButton, false, true);
        rememberPasswordCheckbox.setPosition(8f - rememberPasswordCheckbox.getWidth(), 3f);

        // 取消按钮监听
        cancelButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // TODO 保存逻辑
                returnToPreviousScreen();
            }
        });

        // 输入框
        UserNameField = new InputTextField(gameMain, 20);
        UserNameField.setPosition(8f - UserNameField.getWidth() / 2, 5.5f);
        PasswordField = new InputTextField(gameMain, 25);
        PasswordField.setPosition(8f - PasswordField.getWidth() / 2, 4.25f);

        // 添加组件
        addActorsToStage(cancelButton, loginButton, guestModeButton);
        addCombinedObjectToStage(rememberPasswordCheckbox);
        addActorsToStage(UserNameField, PasswordField);

        // 淡入效果
        ActionUtils.FadeInEffect(cancelButton);
        ActionUtils.FadeInEffect(loginButton);
        ActionUtils.FadeInEffect(guestModeButton);
        rememberPasswordCheckbox.getAllActors().forEach(ActionUtils::FadeInEffect);
        ActionUtils.FadeInEffect(UserNameField);
        ActionUtils.FadeInEffect(PasswordField);
    }

    // 返回上一屏
    public void returnToPreviousScreen() {
        gameMain.getScreenManager().returnPreviousScreen();
    }

    @Override
    public void draw(float delta) {
        stage.draw();
    }

    @Override
    public void logic(float delta) {}

    @Override
    public void input() {

    }

    // 资源释放
    @Override
    public void dispose() {
        super.dispose();
    }
}
