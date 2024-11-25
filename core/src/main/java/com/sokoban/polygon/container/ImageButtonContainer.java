package com.sokoban.polygon.container;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.sokoban.Main;
import com.sokoban.assets.ImageAssets;
import com.badlogic.gdx.math.Interpolation;

/**
 * 图像按钮类<br><br>
 * 相对于图像类有缓动功能
 * @author Life_Checkpoint
 */
public class ImageButtonContainer extends ImageContainer {
    private final float buttonScale = 1.2f;
    private final float scaleTime = 0.2f;

    public ImageButtonContainer(Main gameMain, float scaling) {
        super(gameMain, scaling);
    }

    public ImageButtonContainer(Main gameMain) {
        super(gameMain);
    }

    // 创建按钮
    public Image create(ImageAssets resourceEnum) {
        return create(readDrawableFromFile(resourceEnum));
    }

    public Image create(Drawable drawable) {
        Image button = super.create(drawable, true);
    
        // 按钮增大缩小缓动
        button.addListener(new InputListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                button.addAction(Actions.scaleTo(buttonScale, buttonScale, scaleTime, Interpolation.sine));
            }
    
            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                button.addAction(Actions.scaleTo(1f, 1f, scaleTime, Interpolation.sine));
            }
        });
    
        return button;
    }
}
