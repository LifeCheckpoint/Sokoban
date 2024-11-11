package com.sokoban.polygon;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.sokoban.Main;

/**
 * 带 Image 文本的复选框类
 * @author Life_Checkpoint
 */
public class CheckboxTextObject {
    private CheckboxObject checkbox;
    private Image checkboxText;
    private float x, y, width, height, buff;

    private final float DEFAULT_TEXT_SCALE = 0.005f;
    private final float DEFAULT_BUFF = 0.16f;
    
    /** 
     * true = 普通复选框  false = 响应装饰复选框
     */
    private boolean checkboxType;

    public CheckboxTextObject(Main gameMain, String checkboxTextTexturePath, boolean isChecked, boolean isEnabled) {
        ImageButtonContainer CheckboxImageContainer = new ImageButtonContainer(gameMain.getAssetsPathManager(), DEFAULT_TEXT_SCALE);
        Image CheckboxImage = CheckboxImageContainer.create(checkboxTextTexturePath);
        init(gameMain, CheckboxImage, isChecked, isEnabled, DEFAULT_BUFF);
    }

    public CheckboxTextObject(Main gameMain, Image checkboxText, boolean isChecked, boolean isEnabled) {
        init(gameMain, checkboxText, isChecked, isEnabled, DEFAULT_BUFF);
    }

    public CheckboxTextObject(Main gameMain, String checkboxTextTexturePath, boolean isChecked, boolean isEnabled, float buff) {
        ImageButtonContainer CheckboxImageContainer = new ImageButtonContainer(gameMain.getAssetsPathManager(), DEFAULT_TEXT_SCALE);
        Image CheckboxImage = CheckboxImageContainer.create(checkboxTextTexturePath);
        init(gameMain, CheckboxImage, isChecked, isEnabled, buff);
    }

    public CheckboxTextObject(Main gameMain, Image checkboxText, boolean isChecked, boolean isEnabled, float buff) {
        init(gameMain, checkboxText, isChecked, isEnabled, buff);
    }

    protected void init(Main gameMain, Image checkboxText, boolean isChecked, boolean isEnabled, float buff) {
        this.checkbox = new CheckboxObject(gameMain);
        this.checkboxText = checkboxText;
        this.buff = buff;

        updatePosition();

        checkbox.setChecked(isChecked);
        checkbox.setEnabled(isEnabled);

        // 文本点击响应 + 位移装饰响应
        checkboxText.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (checkboxType) checkbox.filp();
            }

            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                if (!checkboxType) checkbox.setChecked(true);
            }
    
            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                if (!checkboxType) checkbox.setChecked(false);
            }
        });
    }

    /**
     * 设置复选框类型
     * @param checkboxType true = 普通复选框  false = 响应装饰复选框
     */
    public void setCheckboxType(boolean checkboxType) {
        this.checkboxType = checkboxType;

        // 点击响应判定
        checkbox.setResponsable(!checkboxType);
    }

    /**
     * 更改组件大小后进行组件位置更新
     */
    public void updatePosition() {
        float centerY = checkboxText.getY() + checkboxText.getWidth() / 2;
        this.x = checkboxText.getX() - buff - checkbox.getWidth();
        this.y = centerY - Math.max(checkbox.getHeight(), checkboxText.getHeight()) / 2;
        this.width = checkboxText.getWidth() + buff + checkbox.getWidth();
        this.height = Math.max(checkbox.getHeight(), checkboxText.getHeight());
    }

    /**
     * 设置组件位置
     */
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
