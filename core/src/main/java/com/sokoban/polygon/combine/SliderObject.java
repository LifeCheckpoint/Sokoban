package com.sokoban.polygon.combine;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.sokoban.Main;
import com.sokoban.MathUtilsEx;
import com.sokoban.manager.APManager;
import com.sokoban.polygon.PureSliderObject;
import com.sokoban.polygon.actioninterface.ValueUpdateCallback;
import com.sokoban.polygon.container.ImageLabelContainer;

/**
 * 支持文本与计数器的 Slider 组合对象
 * @author Life_Checkpoint
 */
public class SliderObject {
    private float originalMapMinValue, originalMapMaxValue;
    private float x, y, width, height, buff;
    private Image hintTextImage;
    private PureSliderObject slider;
    private CombinedNumberDisplayObject combinedNumberDisplayObject;
    private float value;
    private boolean showFirstZeros;

    private final float DEFAULT_TEXT_SCALE = 0.005f;
    private final float DEFAULT_BUFF = 0.16f;
    private final float MIN_VALUE = 0f, MAX_VALUE = 1f;

    public SliderObject(Main gameMain, APManager.ImageAssets SliderHintResourceEnum) {
        init(gameMain, SliderHintResourceEnum, 0f, 1f, 0, 0, 2, DEFAULT_BUFF);
    }

    public SliderObject(Main gameMain, APManager.ImageAssets SliderHintResourceEnum, 
                        float toMapMinValue, float toMapMaxValue, float initialValue, int integerDigits, int decimalDigits) {
        init(gameMain, SliderHintResourceEnum, toMapMinValue, toMapMaxValue, initialValue, integerDigits, decimalDigits, DEFAULT_BUFF);
    }

    public SliderObject(Main gameMain, APManager.ImageAssets SliderHintResourceEnum, 
                        float toMapMinValue, float toMapMaxValue, float initialValue, int integerDigits, int decimalDigits, float buff) {
        init(gameMain, SliderHintResourceEnum, toMapMinValue, toMapMaxValue, initialValue, integerDigits, decimalDigits, buff);
    }

    /**
     * 初始化
     * @param gameMain 全局句柄
     * @param SliderHintResourceEnum 滑动条提示文本资源枚举
     * @param toMapMinValue 实际最小
     * @param toMapMaxValue 实际最大
     * @param initialValue 实际初始
     * @param integerDigits 整数显示位数
     * @param decimalDigits 小数显示位数
     * @param buff 间距
     */
    private void init(Main gameMain, APManager.ImageAssets SliderHintResourceEnum, 
                        float toMapMinValue, float toMapMaxValue, float initialValue, int integerDigits, int decimalDigits, float buff) {
        
        this.value = initialValue;
        this.buff = buff;

        setOriginalMapMinValue(toMapMinValue);
        setOriginalMapMaxValue(toMapMaxValue);

        ImageLabelContainer TextContainer = new ImageLabelContainer(gameMain, DEFAULT_TEXT_SCALE);
        combinedNumberDisplayObject = new CombinedNumberDisplayObject(gameMain, integerDigits, decimalDigits, initialValue); // 计数器显示
        hintTextImage = TextContainer.create(SliderHintResourceEnum); // 提示文本
        slider = new PureSliderObject(gameMain, MathUtilsEx.linearMap(initialValue, toMapMinValue, toMapMaxValue, MIN_VALUE, MAX_VALUE)); // 滑块条

        slider.setActionWhenValueUpdate(new ValueUpdateCallback() {
            @Override
            public void onValueUpdate(float value) {
                combinedNumberDisplayObject.setValue(MathUtilsEx.linearMap(value, MIN_VALUE, MAX_VALUE, toMapMinValue, toMapMaxValue));
                // System.out.println(MathUtilsEx.linearMap(value, MIN_VALUE, MAX_VALUE, toMapMinValue, toMapMaxValue));
            }
        });

        setPosition(0f, 0f);
    }

    public void setPosition(float x, float y) {
        width = hintTextImage.getWidth() + buff + slider.getWidth() + buff + combinedNumberDisplayObject.getWidth();
        height = Math.max(Math.max(hintTextImage.getHeight(), slider.getHeight()), combinedNumberDisplayObject.getHeight());

        this.x = x;
        this.y = y;

        hintTextImage.setPosition(x, y);
        slider.setPosition(x + hintTextImage.getWidth() + buff, y - 0.1f);
        combinedNumberDisplayObject.setPosition(x + hintTextImage.getWidth() + buff + slider.getWidth() + buff, y);
    }

    public void updatePosition() {
        setPosition(x, y);
    }

    public PureSliderObject getSlider() {
        return slider;
    }

    public Image getHintTextImage() {
        return hintTextImage;
    }

    public CombinedNumberDisplayObject getCombinedNumberDisplayObject() {
        return combinedNumberDisplayObject;
    }

    public boolean isShowFirstZeros() {
        return showFirstZeros;
    }

    public void setShowFirstZeros(boolean showFirstZeros) {
        this.showFirstZeros = showFirstZeros;
    }

    public float getOriginalMapMinValue() {
        return originalMapMinValue;
    }

    public void setOriginalMapMinValue(float toMapMinValue) {
        this.originalMapMinValue = toMapMinValue;
    }

    public float getOriginalMapMaxValue() {
        return originalMapMaxValue;
    }

    public void setOriginalMapMaxValue(float toMapMaxValue) {
        this.originalMapMaxValue = toMapMaxValue;
    }

    public float getValue() {
        return value;
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
