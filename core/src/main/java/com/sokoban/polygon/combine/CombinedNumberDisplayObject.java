package com.sokoban.polygon.combine;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.sokoban.Main;
import com.sokoban.assets.ImageAssets;
import com.sokoban.polygon.NumberDisplayObject;
import com.sokoban.polygon.container.ImageLabelContainer;

/**
 * 组合数字显示类
 * @author Life_Checkpoint
 */
public class CombinedNumberDisplayObject extends SokobanCombineObject {
    private float buff;
    private Image decimalPoint;
    private int integerDigits, decimalDigits;
    private boolean showFirstZeros, showDecimalPoint;
    private List<NumberDisplayObject> numberDigitDisplayObjects;

    private final float DEFAULT_BUFF = 0.16f;
    private final float DEFAULT_POINT_SCALE = 0.003f;

    public CombinedNumberDisplayObject(Main gameMain, int integerDigits, int decimalDigits) {
        super(gameMain);
        init(integerDigits, decimalDigits, 0, DEFAULT_BUFF);
    }

    public CombinedNumberDisplayObject(Main gameMain, int integerDigits, int decimalDigits, float initialValue) {
        super(gameMain);
        init(integerDigits, decimalDigits, initialValue, DEFAULT_BUFF);
    }

    public CombinedNumberDisplayObject(Main gameMain, int integerDigits, int decimalDigits, float initialValue, float buff) {
        super(gameMain);
        init(integerDigits, decimalDigits, initialValue, buff);
    }

    private void init(int integerDigits, int decimalDigits, float initialValue, float buff) {
        this.integerDigits = integerDigits;
        this.decimalDigits = decimalDigits;
        this.showDecimalPoint = true;
        numberDigitDisplayObjects = new ArrayList<>();

        // 添加整数
        for (int i = 0; i < integerDigits; i++) {
            NumberDisplayObject numberDisplayObj = new NumberDisplayObject(gameMain, getIntegerDigit(initialValue, i));
            numberDigitDisplayObjects.add(numberDisplayObj);
        }

        // 添加小数
        for (int i = 0; i < decimalDigits; i++) {
            NumberDisplayObject numberDisplayObj = new NumberDisplayObject(gameMain, getDecimalDigit(initialValue, i));
            numberDigitDisplayObjects.add(numberDisplayObj);
        }

        // 添加小数点
        ImageLabelContainer TextContainer = new ImageLabelContainer(gameMain, DEFAULT_POINT_SCALE);
        decimalPoint = TextContainer.create(ImageAssets.WhitePixel);

        // 设置初始位置
        setPosition(0f, 0f);
        setValue(initialValue);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setPosition(float x, float y) {
        // 整数
        for (int i = 0; i < integerDigits; i++) {
            NumberDisplayObject digitNumber = numberDigitDisplayObjects.get(i);
            // 原始 x + 整数位宽度
            digitNumber.setPosition(x + i * (digitNumber.getWidth() + buff), y);
        }

        // 小数点
        // 原始 x + 整数位宽度 + buff
        decimalPoint.setPosition(x + integerDigits * (numberDigitDisplayObjects.get(0).getWidth() + buff) + buff + 0.035f , y + 0.05f);

        // 小数
        for (int i = 0; i < decimalDigits; i++) {
            NumberDisplayObject digitNumber = numberDigitDisplayObjects.get(integerDigits + i);
            // 原始 x + 整数位宽度 + buff + 小数点宽度 + buff + 小数位宽度
            digitNumber.setPosition(x + integerDigits * (digitNumber.getWidth() + buff) + buff + decimalPoint.getWidth() + buff + i * (digitNumber.getWidth() + buff), y);
        }

        width = (integerDigits + decimalDigits) * (numberDigitDisplayObjects.get(0).getWidth() + buff) + 2 * buff + decimalPoint.getWidth();
        height = numberDigitDisplayObjects.get(0).getHeight();
    }

    /**
     * {@inheritDoc}
     * @param stage
     */
    @Override
    public void addActorsToStage(Stage stage) {
        numberDigitDisplayObjects.forEach(dig -> stage.addActor(dig));
        if (showDecimalPoint) stage.addActor(decimalPoint);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Actor> getAllActors() {
        List<Actor> actors = new ArrayList<>();
        actors.add(decimalPoint);
        actors.addAll(numberDigitDisplayObjects);
        return actors;
    }

    /**
     * 设置数字显示器数值
     * @param value 真实数值
     */
    public void setValue(float value) {
        // 设置整数，整数的数位要反向取值
        boolean zerosEndflag = false;
        for (int i = 0; i < integerDigits; i++) {
            NumberDisplayObject numberDisplayObject = numberDigitDisplayObjects.get(i);
            numberDisplayObject.setValue(getIntegerDigit(value, integerDigits - i - 1));

            // 忽略首零
            if (!showFirstZeros) {
                // 若为首零，且不为最后一位整数，则隐藏
                if (!zerosEndflag && numberDisplayObject.getValue() == 0 && i != integerDigits - 1) numberDisplayObject.hide();
                if (numberDisplayObject.getValue() != 0) {
                    numberDisplayObject.show();
                    zerosEndflag = true;
                }
            }
        }

        // 设置小数
        for (int i = 0; i < decimalDigits; i++) {
            numberDigitDisplayObjects.get(integerDigits + i).setValue(getDecimalDigit(value, i));
        }
    }

    /**
     * 获得整数数位对应值
     * @param originalNumber 原始数字
     * @param digit 从个位 (0) 开始第 digit 位
     * @return 对应位数值
     */
    public int getIntegerDigit(float originalNumber, int digit) {
        int less = (int) originalNumber / (int) (Math.pow(10, digit));
        return less % 10;
    }

    /**
     * 获得小数数位对应值
     * <br><br>
     * 小数位数精度问题，不保证正确显示
     * @param originalNumber 原始数字
     * @param digit 从第一小数位 (0) 开始第 digit 位
     * @return 对应位数值
     */
    public int getDecimalDigit(float originalNumber, int digit) {
        final double eps = 0.04;
        double less = originalNumber * Math.pow(10, digit + 1);
        int lowerBound = (int) less, upperBound = lowerBound + 1;
        if (Math.abs(lowerBound - less) <= eps) return (int) less % 10;
        if (Math.abs(upperBound - less) <= eps) return ((int) less + 1) % 10;
        return (int) less % 10;
    }

    public List<NumberDisplayObject> getNumberDigitDisplayObjects() {
        return numberDigitDisplayObjects;
    }

    public Image getDecimalPoint() {
        return decimalPoint;
    }

    /**
     * 获得当前显示的数位值
     */
    public float getShowingValue() {
        // 整数部分
        StringBuilder valueString = new StringBuilder();
        for (int i = 0; i < integerDigits; i++) valueString.append(numberDigitDisplayObjects.get(i).getValue());
        valueString.append(".");
        for (int i = 0; i < decimalDigits; i++) valueString.append(numberDigitDisplayObjects.get(i + integerDigits).getValue());
        return Float.parseFloat(valueString.toString());
    }

    /**
     * 获得当前显示的整数数位值
     */
    public int getShowingValueInt() {
        // 整数部分
        StringBuilder valueString = new StringBuilder();
        for (int i = 0; i < integerDigits; i++) valueString.append(numberDigitDisplayObjects.get(i).getValue());
        return Integer.parseInt(valueString.toString());
    }

    public boolean isShowDecimalPoint() {
        return showDecimalPoint;
    }

    public void setShowDecimalPoint(boolean showDecimalPoint) {
        this.showDecimalPoint = showDecimalPoint;
    }

    public boolean isShowFirstZeros() {
        return showFirstZeros;
    }

    public void setShowFirstZeros(boolean showFirstZeros) {
        this.showFirstZeros = showFirstZeros;
    }
}
