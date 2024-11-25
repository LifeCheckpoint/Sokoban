package com.sokoban.polygon.combine;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.sokoban.Main;
import com.sokoban.assets.ImageAssets;
import com.sokoban.core.Logger;
import com.sokoban.polygon.PureCheckboxObject;
import com.sokoban.polygon.container.ImageButtonContainer;

/**
 * 带 Image 文本的复选框类
 * <br><br>
 * 由于事件队列的特性，可以保证新加入的复选框读取事件一定会读取到复选框改变过后的取值
 * @author Life_Checkpoint
 */
public class CheckboxObject extends SokobanCombineObject {
    private PureCheckboxObject checkbox;
    private Image checkboxText;
    private float buff;

    private final float DEFAULT_TEXT_SCALE = 0.005f;
    private final float DEFAULT_BUFF = 0.16f;
    
    /** 
     * true = 普通复选框  false = 响应装饰复选框
     */
    private boolean checkboxType;

    public CheckboxObject(Main gameMain, ImageAssets checkboxImageResourceEnum, boolean isChecked, boolean isEnabled) {
        super(gameMain);
        ImageButtonContainer CheckboxImageContainer = new ImageButtonContainer(gameMain, DEFAULT_TEXT_SCALE);
        Image CheckboxImage = CheckboxImageContainer.create(checkboxImageResourceEnum);
        init(CheckboxImage, isChecked, isEnabled, DEFAULT_BUFF);
    }

    public CheckboxObject(Main gameMain, Image checkboxText, boolean isChecked, boolean isEnabled) {
        super(gameMain);
        init(checkboxText, isChecked, isEnabled, DEFAULT_BUFF);
    }

    public CheckboxObject(Main gameMain, ImageAssets checkboxResourceEnum, boolean isChecked, boolean isEnabled, float buff) {
        super(gameMain);
        ImageButtonContainer CheckboxImageContainer = new ImageButtonContainer(gameMain, DEFAULT_TEXT_SCALE);
        Image CheckboxImage = CheckboxImageContainer.create(checkboxResourceEnum);
        init(CheckboxImage, isChecked, isEnabled, buff);
    }

    public CheckboxObject(Main gameMain, Image checkboxText, boolean isChecked, boolean isEnabled, float buff) {
        super(gameMain);
        init(checkboxText, isChecked, isEnabled, buff);
    }

    protected void init(Image checkboxText, boolean isChecked, boolean isEnabled, float buff) {
        this.checkbox = new PureCheckboxObject(gameMain);
        this.checkboxText = checkboxText;
        this.buff = buff;

        updatePosition();

        checkbox.setChecked(isChecked);
        checkbox.setEnabled(isEnabled);

        // 文本点击响应 + 位移装饰响应
        checkboxText.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (checkboxType) checkbox.flip();
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
        checkbox.setResponsable(checkboxType);
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
     * {@inheritDoc}
     */
    @Override
    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
        checkbox.setPosition(x, y + height / 2 - checkbox.getHeight() / 2);
        checkboxText.setPosition(x + checkbox.getWidth() + buff, y + height / 2 - checkboxText.getHeight() / 2);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addActorsToStage(Stage stage) {
        stage.addActor(checkboxText);
        stage.addActor(checkbox);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Actor> getAllActors() {
        List<Actor> actors = new ArrayList<>();
        actors.add(checkbox);
        actors.add(checkboxText);
        return actors;
    }

    public PureCheckboxObject getCheckbox() {
        if (checkbox != null) return checkbox;
        else {
            Logger.error("CheckboxObject", "Checkbox hans't initalized.");
            return null;
        }
    }

    public Image getCheckboxText() {
        if (checkboxText != null) return checkboxText;
        else {
            Logger.error("CheckboxObject", "CheckboxText hans't initalized.");
            return null;
        }
    }

    public boolean getChecked() {
        return checkbox.getChecked();
    }
}
