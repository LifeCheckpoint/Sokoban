package com.sokoban.polygon;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.sokoban.Main;

public class InputTextField extends Actor {
    private Main gameMain;

    private BitmapFont font;
    private String text = "";
    private Color backgroundColor = Color.WHITE;
    private Color textColor = Color.BLACK;
    private Color cursorColor = Color.BLACK;
    private float cursorBlinkTime = 0.5f;
    private boolean cursorVisible = true;
    private float cursorTimer = 0f;
    private int cursorPosition = 0;
    private GlyphLayout layout = new GlyphLayout();
    private Rectangle textBounds = new Rectangle();
    private float padding = 5f;

    public InputTextField(BitmapFont font, Main gameMain) {
        this.gameMain = gameMain;

        this.font = font;
        setSize(200, font.getCapHeight() + padding * 2);

        addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                InputTextField.this.getStage().setKeyboardFocus(InputTextField.this);
                cursorPosition = text.length();  // 直接使用 text，而不是 layout
                return true;
            }
        });
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        cursorTimer += delta;
        if (cursorTimer >= cursorBlinkTime) {
            cursorVisible = !cursorVisible;
            cursorTimer = 0f;
        }

        if (getStage() != null && getStage().getKeyboardFocus() == this) {
            boolean shiftPressed = Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT);
            boolean controlPressed = Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT) || Gdx.input.isKeyPressed(Input.Keys.CONTROL_RIGHT);

            if (Gdx.input.isKeyJustPressed(Input.Keys.ANY_KEY)) {
                if (controlPressed && !shiftPressed && Gdx.input.isKeyJustPressed(Input.Keys.V)) {
                    // 处理粘贴
                    String clipboardText = Gdx.app.getClipboard().getContents();
                    if (clipboardText != null) {
                        text = text.substring(0, cursorPosition) + clipboardText + text.substring(cursorPosition);
                        cursorPosition += clipboardText.length();
                    }
                } else if (!controlPressed && !shiftPressed) {
                    // 处理字符输入
                    Gdx.input.getTextInput(new Input.TextInputListener() {
                        @Override
                        public void input(String inputText) {
                            // 当用户输入时，将输入的文本更新到文本框
                            text = text.substring(0, cursorPosition) + inputText + text.substring(cursorPosition);
                            cursorPosition += inputText.length();  // 更新光标位置
                        }
                
                        @Override
                        public void canceled() {
                            // 如果用户取消输入
                            System.out.println("Input canceled");
                        }
                    }, "Input", getText(), "");  // 弹出输入框，默认文本是当前文本
                }
            }

            if (Gdx.input.isKeyJustPressed(Input.Keys.BACKSPACE) && text.length() > 0 && cursorPosition > 0) {
                // 处理退格删除
                text = text.substring(0, cursorPosition - 1) + text.substring(cursorPosition);
                cursorPosition--;
            }

            if (Gdx.input.isKeyJustPressed(Input.Keys.FORWARD_DEL) && cursorPosition < text.length()) {
                // 处理Delete键删除
                text = text.substring(0, cursorPosition) + text.substring(cursorPosition + 1);
            }

            if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT) && cursorPosition > 0) {
                // 处理光标左移
                cursorPosition--;
            }

            if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT) && cursorPosition < text.length()) {
                // 处理光标右移
                cursorPosition++;
            }
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        Texture whitePixel = gameMain.getAssetsPathManager().get("white_pixel.png", Texture.class);
        // 绘制背景
        batch.setColor(backgroundColor);
        batch.draw(whitePixel, getX(), getY(), getWidth(), getHeight());

        // 绘制文本
        layout.setText(font, text);
        font.setColor(textColor);
        font.draw(batch, layout, getX() + padding, getY() + getHeight() - padding);

        // 绘制光标
        if (getStage() != null && getStage().getKeyboardFocus() == this && cursorVisible) {
            // 使用 GlyphLayout 来计算光标之前的文本宽度
            layout.setText(font, text.substring(0, cursorPosition));
            textBounds.set(getX() + padding, getY() + padding, layout.width, font.getCapHeight());

            // 绘制光标
            batch.setColor(cursorColor);
            batch.draw(whitePixel, textBounds.x + textBounds.width, textBounds.y, 1, textBounds.height);
        }
    }

    public String getText() {
        return text;
    }
    public void setText(String text) {
        this.text = text;
        cursorPosition = text.length();
    }
    public Color getBackgroundColor() {
        return backgroundColor;
    }
    public void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
    }
    public Color getTextColor() {
        return textColor;
    }
    public void setTextColor(Color textColor) {
        this.textColor = textColor;
    }
    public Color getCursorColor() {
        return cursorColor;
    }
    public void setCursorColor(Color cursorColor) {
        this.cursorColor = cursorColor;
    }
    public float getCursorBlinkTime() {
        return cursorBlinkTime;
    }
    public void setCursorBlinkTime(float cursorBlinkTime) {
        this.cursorBlinkTime = cursorBlinkTime;
    }
    public float getPadding() {
        return padding;
    }
    public void setPadding(float padding) {
        this.padding = padding;
    }
}
