package com.sokoban.scenes;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.sokoban.assets.ImageAssets;
import com.sokoban.core.game.GameParams;
import com.sokoban.core.game.Logger;
import com.sokoban.core.map.MapFileInfo;
import com.sokoban.core.map.gamedefault.SokobanLevels;
import com.sokoban.core.map.gamedefault.SokobanMaps;
import com.sokoban.core.user.UserInfo;
import com.sokoban.core.user.UserManager;
import com.sokoban.Main;
import com.sokoban.polygon.combine.CheckboxObject;
import com.sokoban.polygon.combine.HintMessageBox;
import com.sokoban.polygon.container.ImageButtonContainer;
import com.sokoban.polygon.InputTextField;
import com.sokoban.polygon.manager.BackgroundGrayParticleManager;
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
    private GameWelcomeScene gameWelcomeScene;

    private InputTextField userNameField;
    private InputTextField passwordField;
    private CheckboxObject rememberPasswordCheckbox;
    private Image cancelButton;
    private Image loginButton;
    private Image registerButton;

    public LoginScene(Main gameMain, GameWelcomeScene gameWelcomeScene) {
        super(gameMain);
        this.gameWelcomeScene = gameWelcomeScene;
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

        // 用户名输入框更新逻辑
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
                // 只有在确认用户配置 rememberPassword 为 true 后才能隐藏
                // rememberPasswordCheckbox.getCheckbox().setChecked(user.isRememberPassword());
                rememberPasswordCheckbox.getCheckbox().setEnabled(false);
                if (user.isRememberPassword()) {
                    passwordField.setText("");
                    passwordField.remove();
                } else {
                    addActorsToStage(passwordField);
                }

            } else {
                rememberPasswordCheckbox.getCheckbox().setEnabled(true);
                loginButton.remove();
                addActorsToStage(registerButton);

                // 密码框未显示，重新添加
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
                returnToPreviousScreen();
            }
        });

        // 注册按钮监听
        registerButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                UserInfo newUser = userManager.readUserInfo(userNameField.getText());
                
                // 用户已经存在
                if (newUser != null) {
                    Logger.error("LoginScene", String.format("User %s is exist or sth goes wrong", newUser.getUserID()));
                    HintMessageBox msgBox = new HintMessageBox(gameMain, "This user is exist or sth goes wromg...");
                    msgBox.setPosition(8f, 0.2f);
                    addCombinedObjectToStage(msgBox);
                    return;
                }

                // 创建用户信息，密码计算哈希
                newUser = new UserInfo(
                    userNameField.getText(), 
                    userManager.calculatePasswordHash(passwordField.getText()), 
                    rememberPasswordCheckbox.getChecked()
                );
                newUser.setGuest(false);
                
                // 密码为空且不记住密码
                if (passwordField.getText().equals("") && !newUser.isRememberPassword()) {
                    Logger.warning("LoginScene", "Password can't be null");
                    HintMessageBox msgBox = new HintMessageBox(gameMain, "Please input password... ^_^");
                    msgBox.setPosition(8f, 0.2f);
                    addCombinedObjectToStage(msgBox);
                    return;
                }

                // 测试用户信息有效性
                if(!userManager.isValidUserInfo(newUser)) {
                    Logger.warning("LoginScene", "User info is not valid strings");
                    HintMessageBox msgBox = new HintMessageBox(gameMain, "Oops... A strange info, please modify it");
                    msgBox.setPosition(8f, 0.2f);
                    addCombinedObjectToStage(msgBox);
                    return;
                }

                // 写入用户文件，检查是否成功
                if (!userManager.createUserInfo(newUser)) {
                    Logger.error("LoginScene", "Create user info failed");
                    HintMessageBox msgBox = new HintMessageBox(gameMain, "Well, codes always make strange errors...");
                    msgBox.setPosition(8f, 0.2f);
                    addCombinedObjectToStage(msgBox);
                    return;
                }

                // 完成写入，进入教程
                Logger.info("LoginScene", String.format("User %s created successfully", newUser.getUserID()));
                gameWelcomeScene.setCurrentUser(newUser);
                gameMain.getScreenManager().setScreenWithoutSaving(new GameScene(
                    gameMain, new MapFileInfo("", SokobanLevels.Tutorial, SokobanMaps.Turotial_Tutorial), 
                    new GameParams()
                ));
            }
        });

        // 登录按钮监听
        loginButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                UserInfo comparedUser = userManager.readUserInfo(userNameField.getText());
                
                // 用户不存在
                if (comparedUser == null) {
                    Logger.error("LoginScene", String.format("User %s isn't exist or sth goes wrong", userNameField.getText().toLowerCase()));
                    HintMessageBox msgBox = new HintMessageBox(gameMain, "This user is not exist or sth goes error...");
                    msgBox.setPosition(8f, 0.2f);
                    addCombinedObjectToStage(msgBox);
                    return;
                }

                // 密码为空且不记住密码
                if (passwordField.getText().equals("") && !comparedUser.isRememberPassword()) {
                    Logger.warning("LoginScene", "Password test failed");
                    HintMessageBox msgBox = new HintMessageBox(gameMain, "Please input password... ^_^");
                    msgBox.setPosition(8f, 0.2f);
                    addCombinedObjectToStage(msgBox);
                    return;
                }

                // 测试密码正确性
                if (!userManager.testPassword(comparedUser, passwordField.getText())) {
                    Logger.warning("LoginScene", "Password test failed");
                    HintMessageBox msgBox = new HintMessageBox(gameMain, "Password Error? O.o");
                    msgBox.setPosition(8f, 0.2f);
                    addCombinedObjectToStage(msgBox);
                    return;
                }

                // 完成读取，返回主界面
                Logger.info("LoginScene", String.format("User %s login successfully", comparedUser.getUserID()));
                gameWelcomeScene.setCurrentUser(comparedUser);
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
