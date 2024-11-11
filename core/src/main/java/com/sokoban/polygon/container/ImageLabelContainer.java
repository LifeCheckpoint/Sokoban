package com.sokoban.polygon.container;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.sokoban.Main;
import com.sokoban.manager.APManager;

/**
 * 图像标签类<br><br>
 * 与图像类功能几乎一致
 * @author Life_Checkpoint
 */
public class ImageLabelContainer extends ImageContainer {

    public ImageLabelContainer(float scaling, Main gameMain) {
        super(gameMain, scaling);
    }

    public ImageLabelContainer(Main gameMain) {
        super(gameMain);
    }

    public Image create(APManager.ImageAssets resourceEnum) {
        return create(readDrawableFromFile(resourceEnum));
    }
    public Image create(APManager.ImageAssets resourceEnum, float scaling) {
        return create(readDrawableFromFile(resourceEnum), scaling);
    }
    public Image create(Drawable drawable) {
        return super.create(drawable, false);
    }
    public Image create(Drawable drawable, float scaling) {
        return super.create(drawable, false, scaling);
    }

}
