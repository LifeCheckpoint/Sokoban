package com.sokoban.polygon;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.sokoban.Main;

/**
 * 利用 Spine 实现的复选框组件，提供动画效果和状态管理
 * 支持禁用状态和动画过渡
 */
public class CheckboxObject extends SpineObject {
    private boolean isChecked;
    private boolean isEnabled;
    
    private final String TO_UNCHECKED = "unchecked";
    private final String TO_CHECKED = "checked";

    public CheckboxObject(Main gameMain) {
        super(gameMain, "img/checkbox/checkbox.atlas", "img/checkbox/checkbox.json");
        
        setChecked(true);
        setEnabled(true);
        setDefaultMixTime(0f);
        setSize(0.25f, 0.25f);

        // 设置 check 监听
        addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                isChecked = !isChecked;
                setAnimation(0, isChecked ? TO_CHECKED : TO_UNCHECKED, false);
            }
        });
    }
    
    public void setChecked(boolean isChecked) {
        if (this.isChecked != isChecked) {
            this.isChecked = isChecked;
            setAnimation(0, isChecked ? TO_CHECKED : TO_UNCHECKED, false);
        }
    }

    public void filp() {
        isChecked = !isChecked;
        setAnimation(0, isChecked ? TO_CHECKED : TO_UNCHECKED, false);
    }

    public void setEnabled(boolean isEnabled) {
        // TODO
        if (this.isEnabled != isEnabled) {
            this.isEnabled = isEnabled;
        }
    }

    public boolean getChecked() {
        return isChecked;
    }
    public boolean getEnabled() {
        return isEnabled;
    }
}