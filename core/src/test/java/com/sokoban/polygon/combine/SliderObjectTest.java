package com.sokoban.polygon.combine;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * 数字显示器功能测试
 * @author Life_Checkpoint
 */
public class SliderObjectTest {
    
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

    @Test
    public void IntegerDigitTest() {
        float originalNumber = 1145.1919f;
        Assert.assertEquals(getIntegerDigit(originalNumber, 0), 5, "The result for digit=0 should be 5");
        Assert.assertEquals(getIntegerDigit(originalNumber, 1), 4, "The result for digit=1 should be 4");
        Assert.assertEquals(getIntegerDigit(originalNumber, 2),1, "The result for digit=2 should be 1");
        Assert.assertEquals(getIntegerDigit(originalNumber, 3), 1, "The result for digit=3 should be 1");
        Assert.assertEquals(getIntegerDigit(originalNumber, 4), 0, "The result for digit=4 should be 0");
    }

    @Test
    public void DecimalDigitTest() {
        float originalNumber = 1145.1919f;
        Assert.assertEquals(getDecimalDigit(originalNumber, 0), 1, "The result for digit=0 should be 1");
        Assert.assertEquals(getDecimalDigit(originalNumber, 1), 9, "The result for digit=1 should be 9");
        Assert.assertEquals(getDecimalDigit(originalNumber, 2),1, "The result for digit=2 should be 1");
    }
}
