package com.sokoban.polygon;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.sokoban.manager.AssetsPathManager;

/**
 * 图像标签类<br><br>
 * 与图像类功能几乎一致
 * @author Life_Checkpoint
 */
public class ImageLabelContainer extends ImageContainer {

    public ImageLabelContainer(float scaling, AssetsPathManager apManager) {
        super(apManager, scaling);
    }

    public ImageLabelContainer(AssetsPathManager apManager) {
        super(apManager);
    }

    public Image create(String internalpath) {
        return create(readDrawableFromFile(internalpath));
    }
    public Image create(String internalpath, float scaling) {
        return create(readDrawableFromFile(internalpath), scaling);
    }
    public Image create(Drawable drawable) {
        return super.create(drawable, false);
    }
    public Image create(Drawable drawable, float scaling) {
        return super.create(drawable, false, scaling);
    }

}
