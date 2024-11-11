package com.sokoban.polygon;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.sokoban.Main;
import com.sokoban.manager.APManager;

/**
 * Spine 滑动条
 * @author Life_Checkpoint
 */
public class PureSliderObject extends SpineObject {
    private final String SLIDEANIMATION = "slide";
    private final float WIDTH = 1.6f;
    private final float HEIGHT = 0.4f;

    private float value;

    public PureSliderObject(Main gameMain) {
        super(gameMain, APManager.SpineAtlasAssets.Slider, APManager.SpineJsonAssets.Slider);
        init(gameMain, 0f);
    }

    public PureSliderObject(Main gameMain, float initialValue) {
        super(gameMain, APManager.SpineAtlasAssets.Slider, APManager.SpineJsonAssets.Slider);
        init(gameMain, initialValue);
    }

    private void init(Main gameMain, float initialValue) {
        setDefaultMixTime(0f);
        setSize(WIDTH, HEIGHT);

        setValue(initialValue);

        // 设置位移监听
        addListener(new ClickListener() {
            private boolean isMouseDownInside = false;

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                // 检查鼠标左键是否按下
                if (button == 0) {
                    isMouseDownInside = true;
                    setValue(x / WIDTH); // 初始化时更新滑块值
                    return true; // 捕获事件，防止进一步传递
                }
                return false;
            }

            @Override
            public void touchDragged(InputEvent event, float x, float y, int pointer) {
                // 如果鼠标按下且拖动过程中，更新滑块的值
                if (isMouseDownInside) {
                    setValue(x / WIDTH);
                }
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                // 鼠标松开时，重置拖动状态
                if (isMouseDownInside && button == 0) {
                    isMouseDownInside = false;
                }
            }
        });
    }

    public void setValue(float value) {
        this.value = value;
        stayAnimationAtTime(SLIDEANIMATION, value);
    }

    public float getValue() {
        return value;
    }
}
