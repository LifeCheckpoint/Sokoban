package com.sokoban.polygon;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;

/**
 * 复选框组件，提供动画效果和状态管理
 * 支持悬停效果、禁用状态和动画过渡
 */
public class ImageCheckBox extends Actor {
    // 动画相关常量
    private static final float ANIMATION_DURATION = 0.2f;
    private static final float CHECK_MARK_SCALE = 0.7f;
    private static final float HOVER_TRANSITION_DURATION = 0.15f;
    
    // 纹理资源
    private final Texture backgroundTexture;
    private final Texture checkMarkTexture;
    
    // 状态属性
    private boolean isChecked;
    private boolean isEnabled;
    private boolean isHovered;
    private float animationProgress;
    private float hoverProgress;
    private boolean isAnimating;
    
    // 颜色定义
    private final Color NORMAL_COLOR = new Color(0.2f, 0.2f, 0.2f, 1f);
    private final Color HOVER_COLOR = new Color(0.3f, 0.3f, 0.3f, 1f);
    private final Color DISABLED_COLOR = new Color(0.5f, 0.5f, 0.5f, 0.5f);
    private final Color currentColor = new Color(NORMAL_COLOR);
    
    // 回调接口
    private CheckBoxChangeListener changeListener;
    
    /**
     * 复选框状态变化监听器
     */
    public interface CheckBoxChangeListener {
        void onCheckBoxChanged(ImageCheckBox checkBox, boolean isChecked);
    }
    
    /**
     * 构造函数
     * @param size 复选框大小
     * @param backgroundTexure 背景纹理
     * @param checkMarkTexure 选中标记纹理
     * @param ownsTextures 是否负责释放纹理资源
     */
    public ImageCheckBox(float size, Texture backgroundTexure, Texture checkMarkTexure) {
        this.backgroundTexture = backgroundTexure;
        this.checkMarkTexture = checkMarkTexure;
        
        setSize(size, size);
        setEnabled(true);
        
        initializeInputHandling();
    }
    
    /**
     * 初始化输入处理
     */
    private void initializeInputHandling() {
        addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (isEnabled) {
                    toggle();
                    return true;
                }
                return false;
            }
            
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                setHovered(true);
            }
            
            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                setHovered(false);
            }
        });
    }
    
    /**
     * 切换选中状态
     */
    public void toggle() {
        if (isEnabled) {
            setChecked(!isChecked);
        }
    }
    
    /**
     * 更新悬停状态
     */
    public void setHovered(boolean hovered) {
        if (isEnabled && this.isHovered != hovered) {
            this.isHovered = hovered;
        }
    }
    
    /**
     * 设置启用状态
     */
    public void setEnabled(boolean enabled) {
        if (this.isEnabled != enabled) {
            this.isEnabled = enabled;
            setTouchable(enabled ? Touchable.enabled : Touchable.disabled);
            if (!enabled) {
                isHovered = false;
                hoverProgress = 0f;
            }
        }
    }
    
    /**
     * 设置选中状态
     */
    public void setChecked(boolean checked) {
        if (isEnabled && this.isChecked != checked) {
            this.isChecked = checked;
            isAnimating = true;
            animationProgress = 0f;
            
            if (changeListener != null) {
                changeListener.onCheckBoxChanged(this, checked);
            }
        }
    }
    
    /**
     * 设置状态变化监听器
     */
    public void setChangeListener(CheckBoxChangeListener listener) {
        this.changeListener = listener;
    }
    
    @Override
    public void act(float delta) {
        super.act(delta);
        
        updateAnimationState(delta);
        updateHoverState(delta);
        updateColor();
    }
    
    /**
     * 更新选中/取消动画状态
     */
    private void updateAnimationState(float delta) {
        if (isAnimating) {
            animationProgress += delta / ANIMATION_DURATION;
            if (animationProgress >= 1f) {
                animationProgress = 1f;
                isAnimating = false;
            }
        }
    }
    
    /**
     * 更新悬停动画状态
     */
    private void updateHoverState(float delta) {
        if (isHovered && isEnabled) {
            hoverProgress = Math.min(1f, hoverProgress + delta / HOVER_TRANSITION_DURATION);
        } else {
            hoverProgress = Math.max(0f, hoverProgress - delta / HOVER_TRANSITION_DURATION);
        }
    }
    
    /**
     * 更新当前颜色
     */
    private void updateColor() {
        if (isEnabled) {
            currentColor.set(NORMAL_COLOR).lerp(HOVER_COLOR, hoverProgress);
        } else {
            currentColor.set(DISABLED_COLOR);
        }
    }
    
    @Override
    public void draw(Batch batch, float parentAlpha) {
        Color batchColor = batch.getColor();
        float alpha = batchColor.a * parentAlpha * getColor().a;
        
        // 绘制背景
        batch.setColor(
            currentColor.r * getColor().r,
            currentColor.g * getColor().g,
            currentColor.b * getColor().b,
            alpha
        );
        
        batch.draw(backgroundTexture, 
            getX(), getY(),
            getWidth(), getHeight()
        );
        
        // 绘制选中标记
        if ((isChecked || isAnimating) && isEnabled) {
            float progress = isChecked ? 
                Interpolation.smoother.apply(animationProgress) : 
                Interpolation.smoother.apply(1f - animationProgress);
                
            float scale = CHECK_MARK_SCALE * progress;
            float centerOffset = (getWidth() - checkMarkTexture.getWidth() * scale) / 2;
            
            batch.setColor(getColor().r, getColor().g, getColor().b, alpha * progress);
            batch.draw(checkMarkTexture,
                getX() + centerOffset,
                getY() + centerOffset,
                checkMarkTexture.getWidth() * scale,
                checkMarkTexture.getHeight() * scale
            );
        }
        
        batch.setColor(batchColor);
    }
    
    public boolean isChecked() {
        return isChecked;
    }
    public boolean isEnabled() {
        return isEnabled;
    }
    public boolean isHovered() {
        return isHovered;
    }
}