package com.sokoban.polygon.actioninterface;

/**
 * 值更新回调接口
 */
public interface ValueUpdateCallback {

    /**
     * 当滑动条值改变时，将会回调该函数
     * @param value 滑动条值，介于 0~1
     */
    void onValueUpdate(float value);
}