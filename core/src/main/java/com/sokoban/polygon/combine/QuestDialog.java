package com.sokoban.polygon.combine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.sokoban.Main;
import com.sokoban.assets.ImageAssets;
import com.sokoban.assets.SpineAssets;
import com.sokoban.polygon.SpineObject;

/**
 * 确定 / 取消对话框
 * @author Life_Checkpoint
 */
public class QuestDialog extends SokobanCombineObject {
    private Image backDialog;
    private SpineObject cancelButton, confirmButton;
    private String hintText;
    private List<ImageFontStringObject> hintTextImages;

    private final float DEFAULT_BACK_WIDTH = 8f;
    private final int DEFAULT_MAX_CHARS_PERLINE = 40;
    private final float DEFAULT_BUTTON_WIDTH = 1.6f, DEFAULT_BUTTON_ATIO = 66f / 227f;
    private final String ANIMATION_IN = "in";
    private final String ANIMATION_OUT = "out";

    /**
     * 创建询问对话框
     * @param gameMain 主句柄
     * @param hintText 提示文本，较长文本会被自动分割
     */
    public QuestDialog(Main gameMain, String hintText) {
        super(gameMain);
        this.hintText = hintText;
        init(DEFAULT_BACK_WIDTH);
    }

    /**
     * 创建询问对话框
     * @param gameMain 主句柄
     * @param hintText 提示文本，较长文本会被自动分割
     * @param backWidth 背景框宽度
     */
    public QuestDialog(Main gameMain, String hintText, float backWidth) {
        super(gameMain);
        this.hintText = hintText;
        init(backWidth);
    }

    public void init(float backWidth) {
        backDialog = new Image(gameMain.getAssetsPathManager().get(ImageAssets.RoundedRectangleBack2));
        backDialog.setSize(backWidth, backWidth / 16 * 9);
        
        cancelButton = new SpineObject(gameMain, SpineAssets.CancelSpineButton);
        cancelButton.setSize(DEFAULT_BUTTON_WIDTH, DEFAULT_BUTTON_WIDTH * DEFAULT_BUTTON_ATIO);
        cancelButton.setAnimation(0, ANIMATION_IN, false);
        cancelButton.setDefaultMixTime(0f);
        cancelButton.setAnimationTotalTime(0, 0.1f);
        cancelButton.stayAnimationAtFirst(ANIMATION_IN);

        confirmButton = new SpineObject(gameMain, SpineAssets.ConfirmSpineButton);
        confirmButton.setSize(DEFAULT_BUTTON_WIDTH, DEFAULT_BUTTON_WIDTH * DEFAULT_BUTTON_ATIO);
        confirmButton.setAnimation(0, ANIMATION_IN, false);
        confirmButton.setDefaultMixTime(0f);
        confirmButton.setAnimationTotalTime(0, 0.1f);
        confirmButton.stayAnimationAtFirst(ANIMATION_IN);

        // 调整自适应文本长度
        hintTextImages = new ArrayList<>();
        for (String line : splitHintString(hintText)) hintTextImages.add(new ImageFontStringObject(gameMain, line, 0.02f));

        // 增加按钮位移响应
        cancelButton.addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                cancelButton.setAnimation(0, ANIMATION_IN, false);
            }
    
            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                cancelButton.setAnimation(0, ANIMATION_OUT, false);
            }
        });

        confirmButton.addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                confirmButton.setAnimation(0, ANIMATION_IN, false);
            }
    
            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                confirmButton.setAnimation(0, ANIMATION_OUT, false);
            }
        });

        setPosition(0f, 0f);
    }

    /**
     * 自适应提示文本调整
     * @param text 待分割文本
     * @return
     */
    private List<String> splitHintString(String text) {
        List<String> textList = Arrays.asList(text.split("\n"));
        List<String> result = new ArrayList<>();

        for (String line : textList) {
            while (line.length() > DEFAULT_MAX_CHARS_PERLINE) {
                int breakPoint = findBreakPoint(line);
                result.add(line.substring(0, breakPoint).trim());
                line = line.substring(breakPoint).trim();
            }
            result.add(line);
        }

        return result;
    }

    /**
     * 选择换行分割点
     * @param line 输入字符串
     * @return 分割点索引
     */
    private int findBreakPoint(String line) {
        if (line.length() <= DEFAULT_MAX_CHARS_PERLINE) {
            return line.length();
        }
        int breakPoint = line.lastIndexOf(' ', DEFAULT_MAX_CHARS_PERLINE); // 在 DEFAULT_MAX_CHARS_PERLINE 字符之前找到最后一个空格
        return breakPoint == -1 ? DEFAULT_MAX_CHARS_PERLINE : breakPoint; // 如果没有空格，直接截断
    }

    /**
     * {@inheritDoc}
     * @param x <b>中心</b>x坐标
     * @param y <b>中心</b>y坐标
     */
    @Override
    public void setPosition(float x, float y) {
        backDialog.setPosition(x - backDialog.getWidth() / 2, y - backDialog.getHeight() / 2);
        cancelButton.setPosition(x - backDialog.getWidth() * 0.4f, y - backDialog.getHeight() / 2 - cancelButton.getHeight() / 2);
        confirmButton.setPosition(x + backDialog.getWidth() * 0.4f - confirmButton.getWidth(), y - backDialog.getHeight() / 2 - confirmButton.getHeight() / 2);

        // 文字排版
        // 计算换行中心
        float totalHeight = (float) hintTextImages.stream().mapToDouble(image -> image.getHeight()).sum();
        float integrateY = y - totalHeight / 2;
        // 倒序排放文字
        for (int i = hintTextImages.size() - 1; i >= 0; i--) {
            ImageFontStringObject lineImages = hintTextImages.get(i);
            lineImages.setPosition(x - lineImages.getWidth() / 2, integrateY);
            integrateY += lineImages.getHeight();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Actor> getAllActors() {
        List<Actor> actors = new ArrayList<>();
        actors.add(backDialog);
        actors.add(cancelButton);
        actors.add(confirmButton);
        hintTextImages.forEach(textImage -> actors.addAll(textImage.getAllActors()));
        return actors; 
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addActorsToStage(Stage stage) {
        getAllActors().forEach(actor -> stage.addActor(actor));
    }

    public SpineObject getCancelButton() {
        return cancelButton;
    }

    public SpineObject getConfirmButton() {
        return confirmButton;
    }

    
}
