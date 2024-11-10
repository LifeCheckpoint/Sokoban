package com.sokoban.polygon;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.sokoban.Main;

/**
 * 带 Image 文本的复选框类
 */
public class CheckboxTextObject {
    private CheckboxObject checkbox;
    private Image checkboxText;
    private float x, y, width, height, buff;

    public CheckboxTextObject(Main gameMain, Image checkboxText, boolean isChecked, boolean isEnabled, float buff) {
        this.checkbox = new CheckboxObject(gameMain);
        this.checkboxText = checkboxText;

        this.buff = buff;

        float centerY = checkboxText.getY() + checkboxText.getWidth() / 2;
        this.x = checkboxText.getX() - buff - checkbox.getWidth();
        this.y = centerY - Math.max(checkbox.getHeight(), checkboxText.getHeight()) / 2;
        this.width = checkboxText.getWidth() + buff + checkbox.getWidth();
        this.height = Math.max(checkbox.getHeight(), checkboxText.getHeight());

        checkboxText.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                checkbox.filp();
            }
        });
    }

    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
        checkbox.setPosition(x, y + height / 2 - checkbox.getHeight() / 2);
        checkboxText.setPosition(x + checkbox.getWidth() + buff, y + height / 2 - checkboxText.getHeight() / 2);
    }

    public CheckboxObject getCheckbox() {
        return checkbox;
    }

    public Image getCheckboxText() {
        return checkboxText;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

}
