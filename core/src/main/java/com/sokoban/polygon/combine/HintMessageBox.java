package com.sokoban.polygon.combine;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.sokoban.Main;
import com.sokoban.manager.APManager;
import com.sokoban.polygon.SpineObject;
import com.sokoban.utils.ActionUtils;

/**
 * 消息提示框组合对象
 * @author Life_Checkpoint
 */
public class HintMessageBox extends SokobanCombineObject {
    private ImageFontStringObject hintText;
    private SpineObject frame;
    private float durationTime;

    private final String ANIMATION_IN = "in";
    private final String ANIMATION_OUT = "out";
    private final float BOTTOM_BUFF = 0.2f;
    private final float FRAME_WIDTH = 10f, FRAME_HEIGHT = 0.6f;
    private final float DEFAULT_DURATION_TIME = 3f;

    public HintMessageBox(Main gameMain, ImageFontStringObject hintTextImage) {
        super(gameMain);
        init(hintTextImage, DEFAULT_DURATION_TIME);
    }

    public HintMessageBox(Main gameMain, ImageFontStringObject hintTextImage, float durationTime) {
        super(gameMain);
        init(hintTextImage, durationTime);
    }

    private void init(ImageFontStringObject hintTextImage, float durationTime) {
        this.hintText = hintTextImage;
        this.durationTime = durationTime;
        frame = new SpineObject(gameMain, APManager.SpineAssets.Rectangle);
        frame.setSize(FRAME_WIDTH, FRAME_HEIGHT);
        setPosition(0f, 0f);

        playIn();
        playOut();
    }

    /**
     * 设置的位置为<b>居中底对齐</b>
     */
    @Override
    public void setPosition(float x, float y) {
        hintText.setPosition(x - hintText.getWidth() / 2, y + BOTTOM_BUFF);
        frame.setPosition(x - frame.getWidth() / 2, y);

        this.x = x;
        this.y = y;
        this.width = frame.getWidth();
        this.height = frame.getHeight();
    }

    /**
     * 播放启动动画
     */
    public void playIn() {
        ActionUtils.FadeInEffect(frame, 0f);
        hintText.getAllActors().forEach(ActionUtils::FadeInEffect);
        frame.setAnimation(0, ANIMATION_IN, false);
    }

    /**
     * 延迟播放结束动画
     */
    public void playOut() {
        ActionUtils.FadeOutEffect(frame, durationTime);
        hintText.getAllActors().forEach(actor -> ActionUtils.FadeOutEffect(actor, durationTime));
        frame.addAction(Actions.sequence(Actions.delay(durationTime), Actions.run(() -> frame.setAnimation(0, ANIMATION_OUT, false))));
    }

    /**
     * {@inheritDoc}
     * @param stage stage 对象
     */
    @Override
    public void addActorsToStage(Stage stage) {
        stage.addActor(frame);
        hintText.addActorsToStage(stage);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Actor> getAllActors() {
        List<Actor> actors = new ArrayList<>();
        actors.addAll(((ImageFontStringObject) hintText).getAllActors());
        return actors;
    }

    public Object getHintText() {
        return hintText;
    }

    public SpineObject getFrame() {
        return frame;
    }

}
