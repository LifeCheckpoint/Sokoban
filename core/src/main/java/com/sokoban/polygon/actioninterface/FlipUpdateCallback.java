package com.sokoban.polygon.actioninterface;

/** 
 * 复选框翻转事件回调
 */
public interface FlipUpdateCallback {
    /**
     * 当复选框内容改变时，将会回调该函数
     * @param checked 改变后取值
     */
    void onCheckedUpdate(boolean checked);
}
