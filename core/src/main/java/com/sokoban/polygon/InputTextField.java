package com.sokoban.polygon;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.sokoban.Main;
import com.sokoban.manager.APManager.ImageAssets;
import com.sokoban.polygon.combine.ImageFontStringObject;

/**
 * 文本框输入组件
 * @author Claude
 * @author Life_Checkpoint
 */
public class InputTextField extends Actor {
    private Main gameMain;

    private boolean cursorVisible = true;
    private Color backgroundColor = Color.WHITE;
    private float cursorBlinkTime = 0.5f;
    private float cursorTimer = 0f;
    private ImageFontStringObject textImageObj;
    private int cursorPosition = 0;
    private int maxInputLength;
    private String text = "";
    
    private final float PADDING_HEIGHT = 0.05f;
    private final float DEFAULT_FIELD_HEIGHT = 0.4f;

    public InputTextField(Main gameMain, int maxInputLength) {
        this.gameMain = gameMain;
        this.textImageObj = new ImageFontStringObject(gameMain, "", 0.02f);
        this.maxInputLength = maxInputLength;

        addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                // 获得焦点
                InputTextField.this.getStage().setKeyboardFocus(InputTextField.this);
                cursorPosition = text.length();
                return true;
            }
        });

        setSize(6f, DEFAULT_FIELD_HEIGHT + PADDING_HEIGHT * 2);
        setPosition(getX(), getY());
    }

    @Override
    public void setPosition(float x, float y) {
        super.setPosition(x, y);
        textImageObj.setPosition(x + PADDING_HEIGHT, y + PADDING_HEIGHT);
    }

    /**
     * 向文本中插入文本并移动光标
     * @param insertText 光标位置
     */
    private void insertText(String insertText) {
        text = text.substring(0, cursorPosition) + insertText + text.substring(cursorPosition);
        cursorPosition += insertText.length();
    }

    /**
     * 绘制文本框
     */
    @Override
    public void act(float delta) {
        super.act(delta);

        // 光标闪动处理
        cursorTimer += delta;
        if (cursorTimer >= cursorBlinkTime) {
            cursorVisible = !cursorVisible;
            cursorTimer = 0f;
        }

        // 获得焦点
        if (getStage() != null && getStage().getKeyboardFocus() == this) {

            // 判断 shift 和 control 是否按下
            boolean shiftPressed = Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT);
            boolean controlPressed = Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT) || Gdx.input.isKeyPressed(Input.Keys.CONTROL_RIGHT);
            boolean altPresses = Gdx.input.isKeyPressed(Input.Keys.ALT_LEFT) || Gdx.input.isKeyPressed(Input.Keys.ALT_RIGHT);

            // 按下任意键，且长度不超过最大值
            if (Gdx.input.isKeyJustPressed(Input.Keys.ANY_KEY) && text.length() < maxInputLength) {
                // Ctrl + V （不允许 Shift 和 Alt 按下）
                if (controlPressed && !shiftPressed && !altPresses && Gdx.input.isKeyJustPressed(Input.Keys.V)) {
                    // 处理粘贴
                    String clipboardText = Gdx.app.getClipboard().getContents();
                    if (clipboardText != null) {
                        // 只允许输入字母数字下划线
                        if (!clipboardText.matches("^[a-zA-Z0-9_]+$")) return;
                        // 拼合光标前后与输入内容
                        insertText(clipboardText);
                    }

                // 如果任意控制键未被按下
                } else if (!controlPressed && !altPresses) {
                    // 处理字母输入 (A-Z)
                    for (int i = Input.Keys.A; i <= Input.Keys.Z; i++) {
                        if (Gdx.input.isKeyJustPressed(i)) {
                            char c = (char) ('a' + (i - Input.Keys.A));
                            // 如果按下Shift键，转换为大写
                            if (shiftPressed) c = Character.toUpperCase(c);

                            insertText(String.valueOf(c));
                            return;
                        }
                    }
                    
                    // 处理数字输入 (0-9)
                    for (int i = Input.Keys.NUM_0; i <= Input.Keys.NUM_9; i++) {
                        if (Gdx.input.isKeyJustPressed(i)) {
                            char c = (char) (i - Input.Keys.NUM_0 + '0');
                            insertText(String.valueOf(c));
                            return;
                        }
                    }
                    
                    // 处理小键盘数字输入
                    for (int i = Input.Keys.NUMPAD_0; i <= Input.Keys.NUMPAD_9; i++) {
                        if (Gdx.input.isKeyJustPressed(i)) {
                            char c = (char) (i - Input.Keys.NUMPAD_0 + '0');
                            insertText(String.valueOf(c));
                            return;
                        }
                    }
                    
                    // 处理下划线输入
                    if (Gdx.input.isKeyJustPressed(Input.Keys.MINUS) && shiftPressed) {
                        insertText("_");
                        return;
                    }

                    // 处理空格输入
                    if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
                        insertText(" ");
                        return;
                    }
                }
            }

            // Backspace 删除
            if (Gdx.input.isKeyJustPressed(Input.Keys.BACKSPACE) && text.length() > 0 && cursorPosition > 0) {
                text = text.substring(0, cursorPosition - 1) + text.substring(cursorPosition);
                cursorPosition--;
            }
            
            // Delete 键删除
            if (Gdx.input.isKeyJustPressed(Input.Keys.FORWARD_DEL) && cursorPosition < text.length()) {
                text = text.substring(0, cursorPosition) + text.substring(cursorPosition + 1);
            }

            // 光标左移
            if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT) && cursorPosition > 0) {
                cursorPosition--;
            }

            // 光标右移
            if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT) && cursorPosition < text.length()) {
                cursorPosition++;
            }
        }
    }

    // 绘制输入文本框组件
    @Override
    public void draw(Batch batch, float parentAlpha) {
        Texture whitePixel = gameMain.getAssetsPathManager().get(ImageAssets.WhitePixel);
        Texture grayPixel = gameMain.getAssetsPathManager().get(ImageAssets.GrayPixel);
        // 绘制背景
        batch.setColor(backgroundColor);
        batch.draw(grayPixel, getX(), getY(), getWidth(), getHeight());

        // 绘制文本
        textImageObj.reset(text);
        textImageObj.setPosition(getX() + PADDING_HEIGHT, getY() + PADDING_HEIGHT);
        textImageObj.addActorsToStage(getStage());

        // 绘制光标
        if (getStage() != null && getStage().getKeyboardFocus() == this && cursorVisible) {
            batch.draw(whitePixel, getX() + textImageObj.getIntegrateWidth(cursorPosition), getY(), 0.05f, DEFAULT_FIELD_HEIGHT);
        }
    }

    public String getText() {
        return text;
    }
    public void setText(String text) {
        this.text = text;
        cursorPosition = text.length();
    }
    public float getCursorBlinkTime() {
        return cursorBlinkTime;
    }
    public void setCursorBlinkTime(float cursorBlinkTime) {
        this.cursorBlinkTime = cursorBlinkTime;
    }
    public float getPADDING_HEIGHT() {
        return PADDING_HEIGHT;
    }
}
