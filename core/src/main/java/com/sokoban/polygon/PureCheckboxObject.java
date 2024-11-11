package com.sokoban.polygon;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.sokoban.Main;
import com.sokoban.manager.APManager;

/**
 * 利用 Spine 实现的复选框组件，提供动画效果和状态管理
 * 支持禁用状态和动画过渡
 * @author Life_Checkpoint
 */
public class PureCheckboxObject extends SpineObject {
    private boolean isChecked;
    private boolean isEnabled;
    private boolean responsable = true;
    
    private final String TO_UNCHECKED = "unchecked";
    private final String TO_CHECKED = "checked";
    private final String TO_DISABLED = "disabled";

    public PureCheckboxObject(Main gameMain) {
        super(gameMain, APManager.SpineAtlasAssets.Checkbox, APManager.SpineJsonAssets.Checkbox);
        
        this.isChecked = false;
        this.isEnabled = true;

        // 保持停止在动画第一帧，取反
        if (isEnabled) stayAnimationAtFirst(isChecked ? TO_CHECKED : TO_UNCHECKED);
        else stayAnimationAtLast(TO_DISABLED);

        setDefaultMixTime(0f);
        setSize(0.25f, 0.25f);

        // 设置 check 监听
        addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // 只有在点击事件中需要判定是否响应与可选
                if (responsable && isEnabled) {
                    isChecked = !isChecked;
                    setAnimation(0, isChecked ? TO_CHECKED : TO_UNCHECKED, false);
                }
            }
        });
    }
    
    public void setChecked(boolean isChecked) {
        if (this.isChecked != isChecked) {
            this.isChecked = isChecked;
            if (isEnabled) setAnimation(0, isChecked ? TO_CHECKED : TO_UNCHECKED, false);
        }
    }

    public void filp() {
        isChecked = !isChecked;
        if (isEnabled) setAnimation(0, isChecked ? TO_CHECKED : TO_UNCHECKED, false);
    }

    public void setEnabled(boolean isEnabled) {
        if (this.isEnabled != isEnabled) {
            this.isEnabled = isEnabled;
            if (!isEnabled) setAnimation(0, TO_DISABLED, false);
            if (isEnabled) setAnimation(0, isChecked ? TO_CHECKED : TO_UNCHECKED, false);
        }
    }

    /**
     * 设置是否响应点击
     * @param responsable 是否响应点击
     */
    public void setResponsable(boolean responsable) {
        this.responsable = responsable;
    }

    public boolean getChecked() {
        return isChecked;
    }
    public boolean getEnabled() {
        return isEnabled;
    }
}