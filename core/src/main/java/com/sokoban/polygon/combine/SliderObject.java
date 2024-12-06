package com.sokoban.polygon.combine;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.sokoban.Main;
import com.sokoban.assets.ImageAssets;
import com.sokoban.core.game.Logger;
import com.sokoban.polygon.PureSliderObject;
import com.sokoban.polygon.actioninterface.ValueUpdateCallback;
import com.sokoban.polygon.container.ImageLabelContainer;
import com.sokoban.utils.MathUtilsEx;

/**
 * 支持文本与计数器的 Slider 组合对象
 * @author Life_Checkpoint
 */
public class SliderObject extends SokobanCombineObject{
    private boolean showFirstZeros;
    private CombinedNumberDisplayObject combinedNumberDisplayObject;
    private float buff;
    private float originalMapMinValue, originalMapMaxValue;
    private float value;
    private Image hintTextImage;
    private PureSliderObject slider;
    private ValueUpdateCallback callback = null; // SliderObject 自定义更新事件，与 slider 本身更新事件不一致

    private final float DEFAULT_TEXT_SCALE = 0.005f;
    private final float DEFAULT_BUFF = 0.16f;
    private final float MIN_VALUE = 0f, MAX_VALUE = 1f;

    public SliderObject(Main gameMain, ImageAssets SliderHintResourceEnum) {
        super(gameMain);
        init(SliderHintResourceEnum, 0f, 1f, 0, 0, 2, DEFAULT_BUFF);
    }

    public SliderObject(Main gameMain, ImageAssets SliderHintResourceEnum, 
                        float toMapMinValue, float toMapMaxValue, float initialValue, int integerDigits, int decimalDigits) {
        super(gameMain);
        init(SliderHintResourceEnum, toMapMinValue, toMapMaxValue, initialValue, integerDigits, decimalDigits, DEFAULT_BUFF);
    }

    public SliderObject(Main gameMain, ImageAssets SliderHintResourceEnum, 
                        float toMapMinValue, float toMapMaxValue, float initialValue, int integerDigits, int decimalDigits, float buff) {
        super(gameMain);
        init(SliderHintResourceEnum, toMapMinValue, toMapMaxValue, initialValue, integerDigits, decimalDigits, buff);
    }

    /**
     * 初始化
     * @param SliderHintResourceEnum 滑动条提示文本资源枚举
     * @param originalMapMinValue 实际最小
     * @param originalMapMaxValue 实际最大
     * @param initialValue 实际初始
     * @param integerDigits 整数显示位数
     * @param decimalDigits 小数显示位数
     * @param buff 间距
     */
    private void init(ImageAssets SliderHintResourceEnum, float originalMapMinValue, float originalMapMaxValue, 
                        float initialValue, int integerDigits, int decimalDigits, float buff) {
        
        // 异常值处理
        if (originalMapMinValue > initialValue || originalMapMaxValue < initialValue) {
            Logger.error("SliderObject", 
                            String.format("The initial value is out of bound! Expect (%.2f, %.2f), get %.2f", originalMapMinValue, originalMapMaxValue, initialValue));
            initialValue = originalMapMinValue > initialValue ? originalMapMinValue : originalMapMaxValue;
        }
        this.value = initialValue;
        this.buff = buff;

        setOriginalMapMinValue(originalMapMinValue);
        setOriginalMapMaxValue(originalMapMaxValue);

        ImageLabelContainer TextContainer = new ImageLabelContainer(gameMain, DEFAULT_TEXT_SCALE);
        combinedNumberDisplayObject = new CombinedNumberDisplayObject(gameMain, integerDigits, decimalDigits, initialValue); // 计数器显示
        hintTextImage = TextContainer.create(SliderHintResourceEnum); // 提示文本
        slider = new PureSliderObject(gameMain, MathUtilsEx.linearMap(initialValue, originalMapMinValue, originalMapMaxValue, MIN_VALUE, MAX_VALUE)); // 滑块条

        if (decimalDigits == 0) combinedNumberDisplayObject.setShowDecimalPoint(false);

        slider.setActionWhenValueUpdate(new ValueUpdateCallback() {
            @Override
            public void onValueUpdate(float value) {
                combinedNumberDisplayObject.setValue(MathUtilsEx.linearMap(value, MIN_VALUE, MAX_VALUE, originalMapMinValue, originalMapMaxValue));
                // 调用 SliderObject 自定义更新事件
                if (callback != null) callback.onValueUpdate(value);
            }
        });

        setPosition(0f, 0f);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setPosition(float x, float y) {
        width = hintTextImage.getWidth() + buff + slider.getWidth() + buff + combinedNumberDisplayObject.getWidth();
        height = Math.max(Math.max(hintTextImage.getHeight(), slider.getHeight()), combinedNumberDisplayObject.getHeight());

        this.x = x;
        this.y = y;

        hintTextImage.setPosition(x, y);
        slider.setPosition(x + hintTextImage.getWidth() + buff, y - 0.1f);
        combinedNumberDisplayObject.setPosition(x + hintTextImage.getWidth() + buff + slider.getWidth() + buff, y);
    }

    /**
     * {@inheritDoc}
     * @param stage
     */
    @Override
    public void addActorsToStage(Stage stage) {
        stage.addActor(hintTextImage);
        stage.addActor(slider);
        combinedNumberDisplayObject.addActorsToStage(stage);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Actor> getAllActors() {
        List<Actor> actors = new ArrayList<>();
        actors.add(hintTextImage);
        actors.add(slider);
        actors.addAll(combinedNumberDisplayObject.getAllActors());
        return actors;
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

    public void setActionWhenValueUpdate(ValueUpdateCallback callback) {
        this.callback = callback;
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
}
