package com.sokoban.scenes;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Timer;
import com.sokoban.Main;
import com.sokoban.assets.AssetsPathManager;
import com.sokoban.assets.ImageAssets;
import com.sokoban.assets.MusicAssets;
import com.sokoban.assets.ShaderAssets;
import com.sokoban.core.Logger;
import com.sokoban.core.user.UserInfo;
import com.sokoban.polygon.TextureSquare;
import com.sokoban.polygon.combine.CheckboxObject;
import com.sokoban.polygon.combine.HintMessageBox;
import com.sokoban.polygon.container.ButtonCheckboxContainers;
import com.sokoban.polygon.manager.BackgroundGrayParticleManager;
import com.sokoban.polygon.manager.MouseMovingTraceManager;
import com.sokoban.polygon.manager.MusicManager;
import com.sokoban.polygon.manager.SingleActionInstanceManager;
import com.sokoban.polygon.manager.MusicManager.MusicAudio;
import com.sokoban.utils.ActionUtils;

/**
 * 游戏开始欢迎界面
 * @author Life_Checkpoint
 */
public class GameWelcomeScene extends SokobanScene {

    // 画面相机跟踪
    private MouseMovingTraceManager moveTrace;

    // Background
    private TextureSquare[][] backgroundGrid;
    private final int backgroundCol = 6;
    private final int backgroundRow = 10;
    private final float backgroundSquareSize = 0.8f;
    private final float backgroundSquareScale = 2f;
    private final float backgroundMoveDuration = 0.3f;
    private final float backgroundMoveInverval = 0.2f;
    private final float backgroundAlpha = 0.15f;
    private final float backgroundBlueAmount = 0.5f;

    // Background 粒子
    private BackgroundGrayParticleManager bgParticle;

    // Shader
    private ShaderProgram blurShader;
    private FrameBuffer[] blurBuffers; 

    // 按钮
    ButtonCheckboxContainers buttonContainer;
    private Texture[] backgroundTextures;

    private CheckboxObject startGameButton;
    private CheckboxObject aboutButton;
    private CheckboxObject exitButton;
    private CheckboxObject settingsButton;
    private CheckboxObject logInButton;
    private CheckboxObject logOutButton;

    // 动画单一实例管理
    SingleActionInstanceManager SAIManager = new SingleActionInstanceManager(gameMain);

    public GameWelcomeScene(Main gameMain) {
        super(gameMain);
    }

    @Override
    public void init() {
        super.init();

        AssetsPathManager apManager = gameMain.getAssetsPathManager();
        gameMain.setLoginUser(new UserInfo());

        // 背景音乐处理
        MusicManager musicManager = gameMain.getMusicManager();
        musicManager.loadMusic(MusicAudio.Background1, MusicAssets.Light);
        musicManager.loadMusic(MusicAudio.Background2, MusicAssets.Rain);
        musicManager.play(musicManager.getRandomAudioEnum(), false);

        // 鼠标跟踪
        moveTrace = new MouseMovingTraceManager(viewport);
        initShaders();
        
        // 初始化按钮
        buttonContainer = new ButtonCheckboxContainers();

        startGameButton = buttonContainer.create(gameMain, ImageAssets.StartGameButton, false, true, 0.1f);
        startGameButton.setPosition(1f, 3.2f);
        startGameButton.setCheckboxType(false);
        
        logInButton = buttonContainer.create(gameMain, ImageAssets.LoginButton, false, true, 0.1f);
        logInButton.setPosition(1f, 2.2f);
        logInButton.setCheckboxType(false);

        logOutButton = buttonContainer.create(gameMain, ImageAssets.LogOutButton, false, true, 0.1f);
        logOutButton.setPosition(1f, 2.2f);
        logOutButton.setCheckboxType(false);

        aboutButton = buttonContainer.create(gameMain, ImageAssets.AboutButton, false, true, 0.1f);
        aboutButton.setPosition(1f, 1.4f);
        aboutButton.setCheckboxType(false);

        exitButton = buttonContainer.create(gameMain, ImageAssets.ExitButton, false, true, 0.1f);
        exitButton.setPosition(3f, 0.6f);
        exitButton.setCheckboxType(false);

        settingsButton = buttonContainer.create(gameMain, ImageAssets.SettingsButton, false, true, 0.1f);
        settingsButton.setPosition(1f, 0.6f);
        settingsButton.setCheckboxType(false);

        // 开始按钮监听
        startGameButton.getCheckboxText().addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                startGame();
            }
        });

        // 关于按钮监听
        aboutButton.getCheckboxText().addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                gameMain.getScreenManager().setScreen(new AboutScene(gameMain));
            }
        });

        // 退出按钮监听
        exitButton.getCheckboxText().addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Logger.info("GameWelcomeScene", "Game exit");
                gameMain.exit();
            }
        });

        // 设置按钮监听
        settingsButton.getCheckboxText().addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                gameMain.getScreenManager().setScreen(new SettingScene(gameMain));
            }
        });

        // 登录按钮监听
        logInButton.getCheckboxText().addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                gameMain.getScreenManager().setScreen(new LoginScene(gameMain, GameWelcomeScene.this));
            }
        });

        // 登出按钮监听
        logOutButton.getCheckboxText().addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // 切换回访客账户
                setCurrentUser(new UserInfo());
            }
        });

        // 背景纹理组
        backgroundTextures = new Texture[]{
            apManager.get(ImageAssets.Box),
            apManager.get(ImageAssets.BoxActive),
            apManager.get(ImageAssets.BoxTarget)
        };

        // 背景初始化
        backgroundGrid = new TextureSquare[backgroundRow][backgroundCol];
        for (int row = 0; row < backgroundRow; row++) {
            for (int col = 0; col < backgroundCol; col++) {
                // 随机选择纹理
                TextureSquare square = new TextureSquare(backgroundTextures[MathUtils.random(0, backgroundTextures.length - 1)]);
                square.setPosition((row - 1) * backgroundSquareScale, (col - 1) * backgroundSquareScale);
                square.setSize(backgroundSquareSize, backgroundSquareSize);
                // square.setAlpha(backgroundAlpha);
                ActionUtils.FadeInEffect(square, backgroundAlpha, MathUtils.random(0f, 0.5f));
                stage.addActor(square); // 添加到stage中
                backgroundGrid[row][col] = square;
            }
        }

        SokobanScene thisScene = this;
        // 背景 Timer 控制网格矩形移动，交换随机相邻的两个矩形
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                if (gameMain.getScreenManager().getCurrentScreen().equals(thisScene)) swapRandomAdjacentSquares();
            }
        }, 1, backgroundMoveInverval);

        // 背景粒子
        bgParticle = new BackgroundGrayParticleManager(gameMain);
        bgParticle.startCreateParticles();

        // 设置淡入动画
        startGameButton.getAllActors().forEach(ActionUtils::FadeInEffect);
        aboutButton.getAllActors().forEach(ActionUtils::FadeInEffect);
        exitButton.getAllActors().forEach(ActionUtils::FadeInEffect);
        settingsButton.getAllActors().forEach(ActionUtils::FadeInEffect);
        logInButton.getAllActors().forEach(ActionUtils::FadeInEffect);

        // 添加按钮
        // logIn 与 logOut 将互相切换
        addCombinedObjectToStage(startGameButton, aboutButton, exitButton, settingsButton, logInButton);
    }

    /** 
     * 随机交换相邻的两个矩形
     */
    public void swapRandomAdjacentSquares() {
        final int[][] deltaPos = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}};
        int row = MathUtils.random(0, backgroundRow - 1);
        int col = MathUtils.random(0, backgroundCol - 1);
        int row2, col2;

        // 计算可用方向
        List<Integer> possibleDirections = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            row2 = row + deltaPos[i][0];
            col2 = col + deltaPos[i][1];
            // 边界判定
            if (0 <= row2 && row2 < backgroundRow && 0 <= col2 && col2 < backgroundCol) possibleDirections.add(i);
        }
        if (possibleDirections.isEmpty()) return;

        // 随机选择有效方向
        int randomDirectionIndex = possibleDirections.get(MathUtils.random(0, possibleDirections.size() - 1));
        row2 = row + deltaPos[randomDirectionIndex][0];
        col2 = col + deltaPos[randomDirectionIndex][1];

        TextureSquare square1 = backgroundGrid[row][col];
        TextureSquare square2 = backgroundGrid[row2][col2];

        if (SAIManager.isInAction(square1) || SAIManager.isInAction(square2)) return;

        float x1 = square1.getX();
        float y1 = square1.getY();
        float x2 = square2.getX();
        float y2 = square2.getY();

        // 先交换实际数组位置
        backgroundGrid[row][col] = square2;
        backgroundGrid[row2][col2] = square1;

        // 缓动，动画管理防止冲突
        SAIManager.executeAction(square1, Actions.moveTo(x2, y2, backgroundMoveDuration, Interpolation.sine));
        SAIManager.executeAction(square2, Actions.moveTo(x1, y1, backgroundMoveDuration, Interpolation.sine));
    }

    /**
     * 设置主用户信息的回调，用于主界面 UI 切换
     * @param userInfo 用户信息
     */
    public void setCurrentUser(UserInfo userInfo) {
        HintMessageBox msgBox;
        
        // 空用户
        if (userInfo == null) {
            Logger.error("GameWelcomeScene", "Null User Info is not valid!");
            return;
        }

        if (userInfo.isGuest()) {
            // 访客用户
            Logger.info("GameWelcomeScene", "Switch user: Guest");
            logOutButton.getAllActors().forEach(Actor::remove);
            addCombinedObjectToStage(logInButton);
            msgBox = new HintMessageBox(gameMain, "Log out : )");

        } else {
            // 普通用户
            Logger.info("GameWelcomeScene", "Switch user: " + userInfo.getUserID());
            logInButton.getAllActors().forEach(Actor::remove);
            addCombinedObjectToStage(logOutButton);
            msgBox = new HintMessageBox(gameMain, "Welcome, " + userInfo.getUserID() + " !");
        }

        gameMain.setLoginUser(userInfo);

        msgBox.setPosition(8f, 0.2f);
        addCombinedObjectToStage(msgBox);
    }

    // 重绘逻辑
    @Override
    public void draw(float delta) {
        // 画面跟踪
        moveTrace.setPositionWithUpdate();

        // 渲染模糊背景
        renderBlurredBackground();

        stage.draw();
    }

    // 着色器初始化
    // FIXME 无效
    private void initShaders() {
        // 初始化模糊效果
        blurBuffers = new FrameBuffer[2];
        for (int i = 0; i < 2; i++) {
            blurBuffers[i] = new FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);
        }
        
        // 编译着色器
        ShaderProgram.pedantic = false;
        blurShader = gameMain.getAssetsPathManager().get(ShaderAssets.Blur);
        if (!blurShader.isCompiled()) {
            Logger.error("Shader", "Compilation failed:\n" + blurShader.getLog());
            return;
        }
    }

    // 渲染模糊背景
    // FIXME 无效
    private void renderBlurredBackground() {
        Matrix4 projectionMatrix = stage.getBatch().getProjectionMatrix().cpy();
        
        // Step 1: Render background to first buffer
        blurBuffers[0].begin();
        Gdx.gl.glClearColor(0, 0, 0, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        stage.getBatch().setProjectionMatrix(viewport.getCamera().combined);
        stage.getBatch().begin();
        // Only render background grid
        for (int row = 0; row < backgroundRow; row++) {
            for (int col = 0; col < backgroundCol; col++) {
                backgroundGrid[row][col].draw(stage.getBatch(), 1);
            }
        }
        stage.getBatch().end();
        blurBuffers[0].end();
        
        // Step 2: Apply blur
        blurBuffers[1].begin();
        Gdx.gl.glClearColor(0, 0, 0, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        stage.getBatch().setShader(blurShader);
        stage.getBatch().begin();
        
        // Set shader uniforms
        blurShader.setUniformf("blurAmount", backgroundBlueAmount);
        blurShader.setUniformi("u_texture", 0); // texture unit 0
        
        // Draw with Y-flip
        stage.getBatch().draw(blurBuffers[0].getColorBufferTexture(),
            0, Gdx.graphics.getHeight(),
            Gdx.graphics.getWidth(), -Gdx.graphics.getHeight());
        
        stage.getBatch().end();
        blurBuffers[1].end();
        
        // Step 3: Render final result to screen
        stage.getBatch().begin();
        // Draw the blurred result
        stage.getBatch().draw(blurBuffers[1].getColorBufferTexture(),
            0, Gdx.graphics.getHeight(),
            Gdx.graphics.getWidth(), -Gdx.graphics.getHeight());
        stage.getBatch().end();
        
        // Reset shader and projection matrix
        stage.getBatch().setShader(null);
        stage.getBatch().setProjectionMatrix(projectionMatrix);
    }

    // 输入事件处理
    @Override
    public void input() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            startGame();
        }
    }

    @Override
    public void logic(float delta) {}

    // 资源释放
    @Override
    public void dispose() {
        // 释放纹理及 glsl
        if (backgroundTextures != null) for (Texture texture : backgroundTextures) if (texture != null) texture.dispose();
        if (blurShader != null) blurShader.dispose();
        if (blurBuffers != null) for (FrameBuffer buffer : blurBuffers) if (buffer != null) buffer.dispose();
        super.dispose();
    }

    // 游戏正式开始
    public void startGame() {
        gameMain.getScreenManager().setScreen(new LevelChooseScene(gameMain));
    }
}

