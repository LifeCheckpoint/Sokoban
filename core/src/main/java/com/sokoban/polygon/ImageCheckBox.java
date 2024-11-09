package com.sokoban.polygon;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Disposable;

/**
 * 复选框类
 * @author Claude
 */
public class ImageCheckBox extends Actor implements Disposable {
    private static final float ANIMATION_DURATION = 0.2f;
    private static final float CHECK_MARK_SCALE = 0.7f;
    private static final float HOVER_TRANSITION_DURATION = 0.15f;
    
    private final Texture backgroundTexture;
    private final Texture checkMarkTexture;
    private final Image hintImage;
    private final Sound clickSound;
    private final Sound hoverSound;
    
    private boolean isChecked = false;
    private boolean isEnabled = true;
    private boolean isHovered = false;
    private float animationProgress = 0f;
    private float hoverProgress = 0f;
    private boolean isAnimating = false;
    
    // 颜色定义
    private final Color NORMAL_COLOR = new Color(0.2f, 0.2f, 0.2f, 1f);
    private final Color HOVER_COLOR = new Color(0.3f, 0.3f, 0.3f, 1f);
    private final Color DISABLED_COLOR = new Color(0.5f, 0.5f, 0.5f, 0.5f);
    private final Color currentColor = new Color();
    
    public ImageCheckBox(float size) {
        backgroundTexture = createWhiteSquareTexture((int)size);
        checkMarkTexture = createWhiteSquareTexture((int)(size * CHECK_MARK_SCALE));
        
        setSize(size + size * 1.5f, size);
        
        hintImage = new Image();
        hintImage.setSize(size * 1.5f, size);
        hintImage.setPosition(size, 0);
        
        // 加载音效
        clickSound = Gdx.audio.newSound(Gdx.files.internal("sounds/click.wav"));
        hoverSound = Gdx.audio.newSound(Gdx.files.internal("sounds/hover.wav"));
        
        // 添加输入监听器
        addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (isEnabled) {
                    toggle();
                    clickSound.play(0.5f);
                }
            }
            
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                if (isEnabled) {
                    isHovered = true;
                    hoverSound.play(0.2f);
                }
            }
            
            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                isHovered = false;
            }
        });
    }
    
    private Texture createWhiteSquareTexture(int size) {
        // 创建白色方形纹理的实现代码
        // TODO
        return null;
    }
    
    public void toggle() {
        if (isEnabled) {
            isChecked = !isChecked;
            isAnimating = true;
            animationProgress = 0f;
        }
    }
    
    public void setEnabled(boolean enabled) {
        this.isEnabled = enabled;
        setTouchable(enabled ? Touchable.enabled : Touchable.disabled);
        
        // 更新提示图像的颜色
        if (enabled) {
            hintImage.setColor(Color.WHITE);
        } else {
            hintImage.setColor(DISABLED_COLOR);
        }
    }
    
    public void setHintImage(Texture texture) {
        hintImage.setDrawable(new TextureRegionDrawable(new TextureRegion(texture)));
        if (!isEnabled) {
            hintImage.setColor(DISABLED_COLOR);
        }
    }
    
    @Override
    public void act(float delta) {
        super.act(delta);
        
        // 处理选中/取消选中动画
        if (isAnimating) {
            animationProgress += delta / ANIMATION_DURATION;
            if (animationProgress >= 1f) {
                animationProgress = 1f;
                isAnimating = false;
            }
        }
        
        // 处理悬停动画
        if (isHovered && isEnabled) {
            hoverProgress = Math.min(1f, hoverProgress + delta / HOVER_TRANSITION_DURATION);
        } else {
            hoverProgress = Math.max(0f, hoverProgress - delta / HOVER_TRANSITION_DURATION);
        }
        
        // 计算当前颜色
        if (isEnabled) {
            currentColor.set(NORMAL_COLOR).lerp(HOVER_COLOR, hoverProgress);
        } else {
            currentColor.set(DISABLED_COLOR);
        }
    }
    
    @Override
    public void draw(Batch batch, float parentAlpha) {
        Color batchColor = batch.getColor();
        float alpha = batchColor.a * parentAlpha;
        
        // 绘制背景
        batch.setColor(
            currentColor.r,
            currentColor.g,
            currentColor.b,
            currentColor.a * alpha
        );
        
        batch.draw(backgroundTexture, 
                  getX(), getY(), 
                  backgroundTexture.getWidth(), 
                  backgroundTexture.getHeight());
        
        // 绘制选中标志（带动画）
        if ((isChecked || isAnimating) && isEnabled) {
            float progress = isChecked ? 
                Interpolation.smoother.apply(animationProgress) :
                Interpolation.smoother.apply(1f - animationProgress);
                
            float scale = CHECK_MARK_SCALE * progress;
            float centerOffset = (backgroundTexture.getWidth() - 
                                checkMarkTexture.getWidth() * scale) / 2;
            
            batch.setColor(1f, 1f, 1f, alpha * progress);
            batch.draw(checkMarkTexture,
                      getX() + centerOffset,
                      getY() + centerOffset,
                      checkMarkTexture.getWidth() * scale,
                      checkMarkTexture.getHeight() * scale);
        }
        
        // 绘制提示图像
        hintImage.draw(batch, parentAlpha);
        
        // 恢复batch的原始颜色
        batch.setColor(batchColor);
    }
    
    @Override
    public void dispose() {
        backgroundTexture.dispose();
        checkMarkTexture.dispose();
        clickSound.dispose();
        hoverSound.dispose();
    }
    
    // Getter方法
    public boolean isChecked() {
        return isChecked;
    }
    
    public boolean isEnabled() {
        return isEnabled;
    }
    
    // Setter方法
    public void setChecked(boolean checked) {
        if (this.isChecked != checked && isEnabled) {
            this.isChecked = checked;
            isAnimating = true;
            animationProgress = 0f;
        }
    }
}