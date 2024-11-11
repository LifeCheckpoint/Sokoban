package com.sokoban.polygon;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.sokoban.Main;

/**
 * 复选框装饰按钮容器
 */
public class ButtonCheckboxContainers {

    /**
     * 创建复选框装饰按钮
     * @param gameMain 全局句柄
     * @param internalpath 提示图像路径
     * @param isChecked 是否选中
     * @param isEnabled 是否启用
     * @return CheckboxTextObject对象
     */
    public CheckboxTextObject create(Main gameMain, String internalpath, boolean isChecked, boolean isEnabled) {
        Image button = new ImageButtonContainer(gameMain.getAssetsPathManager()).create(internalpath);
        return new CheckboxTextObject(gameMain, button, isChecked, isEnabled);
    }

    /**
     * 创建复选框装饰按钮
     * @param gameMain 全局句柄
     * @param internalpath 提示图像路径
     * @param isChecked 是否选中
     * @param isEnabled 是否启用
     * @param buff 左右间距
     * @return CheckboxTextObject对象
     */
    public CheckboxTextObject create(Main gameMain, String internalpath, boolean isChecked, boolean isEnabled, float buff) {
        Image button = new ImageButtonContainer(gameMain.getAssetsPathManager()).create(internalpath);
        return new CheckboxTextObject(gameMain, button, isChecked, isEnabled, buff);
    }
}
