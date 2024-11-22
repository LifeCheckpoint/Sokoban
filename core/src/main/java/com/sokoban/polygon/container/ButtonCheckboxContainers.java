package com.sokoban.polygon.container;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.sokoban.Main;
import com.sokoban.manager.APManager;
import com.sokoban.polygon.combine.CheckboxObject;

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
    public CheckboxObject create(Main gameMain, APManager.ImageAssets resourceEnum, boolean isChecked, boolean isEnabled) {
        Image button = new ImageButtonContainer(gameMain).create(resourceEnum);
        return new CheckboxObject(gameMain, button, isChecked, isEnabled);
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
    public CheckboxObject create(Main gameMain, APManager.ImageAssets resourceEnum, boolean isChecked, boolean isEnabled, float buff) {
        Image button = new ImageButtonContainer(gameMain).create(resourceEnum);
        return new CheckboxObject(gameMain, button, isChecked, isEnabled, buff);
    }

    /**
     * 创建复选框装饰按钮
     * @param gameMain 全局句柄
     * @param internalpath 提示图像路径
     * @param isChecked 是否选中
     * @param isEnabled 是否启用
     * @param buff 左右间距
     * @param scaling 缩放大小
     * @return CheckboxTextObject对象
     */
    public CheckboxObject create(Main gameMain, APManager.ImageAssets resourceEnum, boolean isChecked, boolean isEnabled, float buff, float scaling) {
        Image button = new ImageButtonContainer(gameMain, scaling).create(resourceEnum);
        return new CheckboxObject(gameMain, button, isChecked, isEnabled, buff);
    }
}
