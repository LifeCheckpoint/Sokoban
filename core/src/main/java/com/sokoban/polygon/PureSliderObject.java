package com.sokoban.polygon;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.sokoban.Main;
import com.sokoban.assets.SpineAssets;
import com.sokoban.polygon.actioninterface.ValueUpdateCallback;

/**
 * Spine 滑动条
 * @author Life_Checkpoint
 */
public class PureSliderObject extends SpineObject {
    private float value;
    private ValueUpdateCallback callback = null;

    private final String SLIDEANIMATION = "slide";
    private final float WIDTH = 1.6f;
    private final float HEIGHT = 0.4f;

    /**
     * 初始化滑块条
     * @param gameMain 全局句柄
     */
    public PureSliderObject(Main gameMain) {
        super(gameMain, SpineAssets.Slider);
        init(gameMain, 0f);
    }

    /**
     * 初始化滑块条
     * @param gameMain 全局句柄
     * @param initialValue 滑块条初始取值，介于 0~1
     */
    public PureSliderObject(Main gameMain, float initialValue) {
        super(gameMain, SpineAssets.Slider);
        init(gameMain, initialValue);
    }

    /**
     * 初始化滑块条
     * @param gameMain 全局句柄
     * @param initialValue 滑块条初始取值，介于 0~1
     */
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
                    if (x >= 0 && x <= WIDTH) setValue(x / WIDTH); // 初始化时更新滑块值
                    if (x < 0) setValue(0f);
                    if (x > WIDTH) setValue(1f);
                    return true; // 捕获事件，防止进一步传递
                }
                return false;
            }

            @Override
            public void touchDragged(InputEvent event, float x, float y, int pointer) {
                // 如果鼠标按下且拖动过程中，更新滑块的值
                if (isMouseDownInside) {
                    if (x >= 0 && x <= WIDTH) setValue(x / WIDTH);
                    if (x < 0) setValue(0f);
                    if (x > WIDTH) setValue(1f);
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

    /**
     * 滑动条更新回调函数
     * @param callback 回调函数
     */
    public void setActionWhenValueUpdate(ValueUpdateCallback callback) {
        this.callback = callback;
    }

    /**
     * 设置滑块条取值
     * @param value 滑块条取值，介于 0~1
     */
    public void setValue(float value) {
        this.value = value;
        if (callback != null) callback.onValueUpdate(value);
        stayAnimationAtTime(SLIDEANIMATION, value);
    }

    /**
     * 获得滑块条取值
     * @return 取值，0~1
     */
    public float getValue() {
        return value;
    }
}
