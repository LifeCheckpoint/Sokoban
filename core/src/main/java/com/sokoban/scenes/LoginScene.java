package com.sokoban.scenes;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.sokoban.core.Logger;
import com.sokoban.Main;
import com.sokoban.core.user.UserInfo;
import com.sokoban.core.user.UserManager;
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
    private UserManager userManager;

    private InputTextField userNameField;
    private InputTextField passwordField;
    private CheckboxObject rememberPasswordCheckbox;
    private Image cancelButton;
    private Image loginButton;
    private Image registerButton;

    public LoginScene(Main gameMain) {
        super(gameMain);
    }

    @Override
    public void init() {
        super.init();

        userManager = new UserManager();
        bgParticle = new BackgroundGrayParticleManager(gameMain);
        bgParticle.startCreateParticles();

        // 输入框
        userNameField = new InputTextField(gameMain, 20);
        userNameField.setPosition(8f - userNameField.getWidth() / 2, 5.5f);
        passwordField = new InputTextField(gameMain, 25);
        passwordField.setPosition(8f - passwordField.getWidth() / 2, 4.25f);

        userNameField.setCallback(text -> {
            UserInfo user;

            // 尝试读取用户
            try {
                user = userManager.readUserInfo(text);
            } catch (Exception e) {
                Logger.error("LoginScene", e.getMessage());
                return;
            }
            
            // 用户被找到
            if (user != null) {
                Logger.info("LoginScene", "Find user: " + user.getUserID());
                registerButton.remove();
                addActorsToStage(loginButton);

                // 记住密码相关处理
                rememberPasswordCheckbox.getCheckbox().setChecked(user.isRememberPassword());
                if (user.isRememberPassword()) {
                    passwordField.setText("");
                    passwordField.remove();
                }

            } else {
                loginButton.remove();
                addActorsToStage(registerButton);

                // 密码框未显示
                if (!stage.getActors().contains(passwordField, true)) {
                    addActorsToStage(passwordField);
                }
            }
        });

        // 按钮
        ImageButtonContainer buttonContainer = new ImageButtonContainer(gameMain);
        cancelButton = buttonContainer.create(ImageAssets.CancelButton);
        cancelButton.setPosition(6.5f - cancelButton.getWidth() / 2, 2f);
        buttonContainer.setScaling(0.007f);
        loginButton = buttonContainer.create(ImageAssets.LoginButton);
        loginButton.setPosition(9.5f - loginButton.getWidth() / 2, 1.95f);
        registerButton = buttonContainer.create(ImageAssets.RegisterButton);
        registerButton.setPosition(loginButton.getX(), 1.95f);

        rememberPasswordCheckbox = new CheckboxObject(gameMain, ImageAssets.RememberPasswordButton, false, true);
        rememberPasswordCheckbox.setPosition(passwordField.getX(), 3f);
        rememberPasswordCheckbox.setCheckboxType(true);

        // 取消按钮监听
        cancelButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // TODO 保存逻辑
                returnToPreviousScreen();
            }
        });

        // 添加组件
        addActorsToStage(cancelButton, registerButton); // 输入正确用户名后显示 login 按钮
        addCombinedObjectToStage(rememberPasswordCheckbox);
        addActorsToStage(userNameField, passwordField);

        // 淡入效果
        ActionUtils.FadeInEffect(cancelButton);
        ActionUtils.FadeInEffect(registerButton);
        rememberPasswordCheckbox.getAllActors().forEach(ActionUtils::FadeInEffect);
        ActionUtils.FadeInEffect(userNameField);
        ActionUtils.FadeInEffect(passwordField);
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
