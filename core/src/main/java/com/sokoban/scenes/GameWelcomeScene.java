package com.sokoban.scenes;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.sokoban.Main;
import com.sokoban.polygon.GridSquare;

public class GameWelcomeScene extends ApplicationAdapter implements Screen {
    private Main gameMain;
    private Random random;
    private FitViewport viewport;
    private Stage stage;
    private final int backGroundColorRGBA = 0x101010ff;

    // 鼠标位移相关 Vector
    private Vector2 mousePos;
    private Vector2 screenCenter;
    private Vector2 mouse2CenterOffsetScaled;
    private final float maxScreenOffset = 1f;
    private final float screenMoveScaling = 0.03f;

    // Texture素材
    private Texture startGameButtonTextTexture;
    private Texture[] backgroundTextures;

    // Background
    private GridSquare[][] backgroundGrid;
    private final int backgroundCol = 6;
    private final int backgroundRow = 10;
    private final float backgroundSquareSize = 1f;
    private final float backgroundSquareScale = 2f;
    private final float backgroundMoveDuration = 0.3f;
    private final float backgroundMoveInverval = 0.2f;
    private final float backgroundAlpha = 0.15f;
    private final float BLUR_AMOUNT = 0.5f;

    // Shader
    private ShaderProgram blurShader;
    private FrameBuffer[] blurBuffers; 

    // Region UI
    private TextureRegion startGameButtonRegion;
    private ImageButton startGameButton;

    public GameWelcomeScene(Main gameMain) {
        this.gameMain = gameMain;
    }

    public Main getGameMain() {
        return gameMain;
    }

    @Override
    public void show() {
        random = new Random();
        viewport = new FitViewport(16, 9);
        mousePos = new Vector2();
        mouse2CenterOffsetScaled = new Vector2();
        screenCenter = new Vector2(viewport.getWorldWidth() / 2, viewport.getWorldHeight() / 2);

        // 初始化模糊效果
        blurBuffers = new FrameBuffer[2];
        for (int i = 0; i < 2; i++) {
            blurBuffers[i] = new FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);
        }
        
        // 编译着色器
        ShaderProgram.pedantic = false;
        blurShader = new ShaderProgram(
            Gdx.files.internal("shaders/blurVertex.glsl"),
            Gdx.files.internal("shaders/blurFragment.glsl")
        );
        
        if (!blurShader.isCompiled()) {
            Gdx.app.error("Shader", "Compilation failed:\n" + blurShader.getLog());
            return;
        }
        
        // UI Stage
        stage = new Stage(viewport);
        Gdx.input.setInputProcessor(stage);

        // 游戏开始按钮
        startGameButtonTextTexture = new Texture("img/start_game.png");
        startGameButtonRegion = new TextureRegion(startGameButtonTextTexture);
        startGameButton = new ImageButton(new TextureRegionDrawable(startGameButtonRegion));
        startGameButton.setSize(2.5f, 3.75f);
        startGameButton.setPosition(2f, 2f);
        startGameButton.setTransform(true);

        // 开始按钮监听
        startGameButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("Start Game!");
            }
        });
        startGameButton.addListener(new InputListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                startGameButton.addAction(Actions.scaleTo(1.2f, 1.2f, 0.2f, Interpolation.sine)); // 增大按钮
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                startGameButton.addAction(Actions.scaleTo(1f, 1f, 0.2f, Interpolation.sine)); // 恢复原始大小
            }
        });

        // 背景纹理组
        backgroundTextures = new Texture[]{
            new Texture("img/box.png"),
            new Texture("img/box_active.png"),
            new Texture("img/target.png")
        };

        // 背景初始化
        backgroundGrid = new GridSquare[backgroundRow][backgroundCol];
        for (int row = 0; row < backgroundRow; row++) {
            for (int col = 0; col < backgroundCol; col++) {
                // 随机选择纹理
                Texture texture = backgroundTextures[random.nextInt(backgroundTextures.length)];
                GridSquare square = new GridSquare(texture);
                square.setPosition((row - 1) * backgroundSquareScale, (col - 1) * backgroundSquareScale);
                square.setSize(backgroundSquareSize, backgroundSquareSize);
                square.setAlpha(backgroundAlpha);
                stage.addActor(square); // 添加到stage中
                backgroundGrid[row][col] = square;
            }
        }

        // 背景 Timer 控制网格矩形移动，交换随机相邻的两个矩形
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                swapRandomAdjacentSquares();
            }
        }, 1, backgroundMoveInverval);

        // 添加 UI
        stage.addActor(startGameButton);
    }

    // 随机交换相邻的两个矩形
    public void swapRandomAdjacentSquares() {
        final int[][] deltaPos = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}};
        int row = random.nextInt(backgroundRow);
        int col = random.nextInt(backgroundCol);
        int row2 = -1, col2 = -1;

        // 随机选取可用方向
        List<Integer> possibleDirections = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            row2 = row + deltaPos[i][0];
            col2 = col + deltaPos[i][1];
            // 边界判定
            if (0 <= row2 && row2 < backgroundRow && 0 <= col2 && col2 < backgroundCol) {
                possibleDirections.add(i);
            }
        }

        if (possibleDirections.isEmpty()) {
            return;
        }

        // 随机选择有效方向
        int randomDirectionIndex = possibleDirections.get(random.nextInt(possibleDirections.size()));
        row2 = row + deltaPos[randomDirectionIndex][0];
        col2 = col + deltaPos[randomDirectionIndex][1];

        final int row2F = row2, col2F = col2; // lambda 使用局部变量需要 final 修饰

        GridSquare square1 = backgroundGrid[row][col];
        GridSquare square2 = backgroundGrid[row2][col2];

        float x1 = square1.getX();
        float y1 = square1.getY();
        float x2 = square2.getX();
        float y2 = square2.getY();

        // 缓动
        square1.addAction(Actions.sequence(
            Actions.moveTo(x2, y2, 0.5f, Interpolation.sine),
            Actions.run(() -> {
                // 交换矩形数据位置
                backgroundGrid[row][col] = square2;
                backgroundGrid[row2F][col2F] = square1;
            })
        ));

        square2.addAction(Actions.moveTo(x1, y1, backgroundMoveDuration, Interpolation.sine));
}

    // 重绘逻辑
    private void draw() {
        ScreenUtils.clear(new Color(backGroundColorRGBA));

        // 计算鼠标位置世界坐标以及偏移矢量
        mousePos.set(Gdx.input.getX(), Gdx.input.getY());
        viewport.unproject(mousePos);
        mouse2CenterOffsetScaled = mousePos.cpy().sub(screenCenter).scl(screenMoveScaling);

        // 防止移出
        if (mouse2CenterOffsetScaled.len() > maxScreenOffset) mouse2CenterOffsetScaled.setLength(maxScreenOffset);
        
        // 更新相机位置
        viewport.getCamera().position.set(mouse2CenterOffsetScaled.add(screenCenter), 0);
        viewport.getCamera().update();

        // stage 更新
        stage.act(Gdx.graphics.getDeltaTime());

        // 渲染模糊背景
        renderBlurredBackground();
        
        // 渲染UI元素
        stage.getBatch().begin();
        startGameButton.draw(stage.getBatch(), 1);
        stage.getBatch().end();
        
        stage.draw();
    }

    // BUG TO FIX
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
        blurShader.setUniformf("blurAmount", BLUR_AMOUNT);
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
    private void input() {
        if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            System.out.println("Space");
        }
    }

    // 主渲染帧
    @Override
    public void render(float delta) {
        input();
        // logic();
        draw();
    }

    @Override
    public void hide() {
        // 隐藏即释放
        dispose();
    }

    // 资源释放
    @Override
    public void dispose() {
        if (stage != null) stage.dispose();
        if (startGameButtonTextTexture != null) startGameButtonTextTexture.dispose();
        if (backgroundTextures != null) for (Texture texture : backgroundTextures) if (texture != null) texture.dispose();
        if (blurShader != null) blurShader.dispose();
        if (blurBuffers != null) for (FrameBuffer buffer : blurBuffers) if (buffer != null) buffer.dispose();
    }

    // 游戏开始
    public void startGame() {
        System.out.println("Game start.");
    }
}

