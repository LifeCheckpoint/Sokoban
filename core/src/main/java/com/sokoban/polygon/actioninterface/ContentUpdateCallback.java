package com.sokoban.polygon.actioninterface;

/**
 * 文本更新回调接口
 */
public interface ContentUpdateCallback {
    /**
     * 当文本内容改变时，将会回调该函数
     * @param content 文本内容
     */
    void onContentUpdate(String content);
}
