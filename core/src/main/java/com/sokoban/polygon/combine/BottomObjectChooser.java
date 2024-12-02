package com.sokoban.polygon.combine;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.sokoban.Main;
import com.sokoban.assets.ImageAssets;
import com.sokoban.polygon.actioninterface.BottomObjectChoosen;
import com.sokoban.polygon.container.ImageButtonContainer;

/**
 * 可滚动的底部物体轮选器
 */
public class BottomObjectChooser extends SokobanCombineObject {
    private Image PrevTab, NextTab;
    private List<List<Actor>> objectForChoose = new ArrayList<>();
    private List<List<ImageFontStringObject>> objectHintText = new ArrayList<>();
    private Image backgroundRectangle;
    private float buff;
    private float backWidth;

    private int tabIndex = 0;
    private BottomObjectChoosen chosenEvent;

    private final float DEFAULT_BUFF = 0.4f;
    private final float DEFAULT_BACK_WIDTH = 12f;
    private final float DEFAULT_SCALING = 0.008f;
    private final float DEFAULT_BUTTON_SCALING = 0.01f;
    private final float DEFAULT_TEXT_SCALING = 0.75f;
    private final float DEFAULT_OBJECT_CENTER_TO_TOP = 0.7f;

    public BottomObjectChooser(Main gameMain) {
        super(gameMain);
        init(DEFAULT_BUFF, DEFAULT_BACK_WIDTH, DEFAULT_SCALING);
    }

    public BottomObjectChooser(Main gameMain, float buff) {
        super(gameMain);
        init(buff, DEFAULT_BACK_WIDTH, DEFAULT_SCALING);
    }

    public BottomObjectChooser(Main gameMain, float buff, float backWidth, float scaling) {
        super(gameMain);
        init(buff, backWidth, scaling);
    }

    private void init(float buff, float backWidth, float scaling) {
        this.backWidth = backWidth;
        this.buff = buff;

        ImageButtonContainer menuButtonContainer = new ImageButtonContainer(gameMain, DEFAULT_BUTTON_SCALING);
        PrevTab = menuButtonContainer.create(ImageAssets.LeftSquareArrow);
        NextTab = menuButtonContainer.create(ImageAssets.RightSquareArrow);

        backgroundRectangle = new Image(gameMain.getAssetsPathManager().get(ImageAssets.BottomBackgroundRectangleWide));
        backgroundRectangle.setWidth(backWidth);
        backgroundRectangle.setHeight(backWidth / 16f * 2.5f);
        backgroundRectangle.getColor().a = 0.8f;
    }

    /**
     * 向选择器中添加
     */
    public void addObject(int tabIndex, Object tag, Actor objectActor, String hintText) {
        // 栏位不足，动态填充
        if (tabIndex >= objectForChoose.size()) for (int i = 0; i < tabIndex - objectForChoose.size() + 1; i++) {
            objectForChoose.add(new ArrayList<>());
            objectHintText.add(new ArrayList<>());
        } 

        objectForChoose.get(tabIndex).add(objectActor);

        ImageFontStringObject fontString = new ImageFontStringObject(gameMain, hintText, 0f, DEFAULT_TEXT_SCALING);
        objectHintText.get(tabIndex).add(fontString);

        // 为标签添加回调
        objectActor.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                chosenEvent.switchChoose(tag);
            }
        });
    }

    /**
     * 设置物体被选择回调事件
     */
    public void setOnClickEvent(BottomObjectChoosen chosenEvent) {
        this.chosenEvent = chosenEvent;
    }

    /**
     * 设置选择器位置
     * <br><br>
     * {@inheritDoc}
     * @param x <b>中心</b>位置 x
     * @param y <b>顶部</b>位置 y
     */
    @Override
    public void setPosition(float x, float y) {
        this.width = backWidth;
        this.height = backgroundRectangle.getHeight();
        this.x = x - backWidth / 2;
        this.y = y - backgroundRectangle.getHeight();

        backgroundRectangle.setPosition(this.x, this.y);

        PrevTab.setPosition(this.x - PrevTab.getWidth() - 0.1f, y - DEFAULT_OBJECT_CENTER_TO_TOP - PrevTab.getHeight() / 2);
        NextTab.setPosition(this.x + backWidth + 0.1f, y - DEFAULT_OBJECT_CENTER_TO_TOP - NextTab.getHeight() / 2);

        // 居左排列所有物体
        for (int tabIndex = 0; tabIndex < objectForChoose.size(); tabIndex++) {
            List<Actor> tab = objectForChoose.get(tabIndex);
            List<ImageFontStringObject> hints = objectHintText.get(tabIndex);

            float integrateX = PrevTab.getX() + buff * 3;
            for (int objectIndex = 0; objectIndex < tab.size(); objectIndex++) {
                Actor obj = tab.get(objectIndex);
                ImageFontStringObject objHint = hints.get(objectIndex);

                obj.setPosition(integrateX, y - DEFAULT_OBJECT_CENTER_TO_TOP - obj.getHeight() / 2);
                objHint.setPosition(integrateX + obj.getWidth() / 2 - objHint.getWidth() / 2, y - DEFAULT_OBJECT_CENTER_TO_TOP - obj.getHeight() / 2 - objHint.getHeight());
                integrateX += obj.getWidth() + buff;
            }
        }
    }
    
    public int getTabIndex() {
        return tabIndex;
    }

    public void setTabIndex(int tabIndex) {
        this.tabIndex = tabIndex;
    }

    public boolean addTabIndex() {
        if (tabIndex + 1 >= objectForChoose.size()) return false;
        tabIndex += 1;
        return true;
    }

    public boolean subTabIndex() {
        if (tabIndex - 1 < 0) return false;
        tabIndex -= 1;
        return true;
    }

    public Image getPrevTab() {
        return PrevTab;
    }

    public Image getNextTab() {
        return NextTab;
    }

    /**
     * 设置<b>当前标签栏索引</b>后通过该方法获得对应 Actor
     * <br><br>
     * {@inheritDoc}
     */
    @Override
    public List<Actor> getAllActors() {
        List<Actor> actors = new ArrayList<>();
        actors.add(backgroundRectangle); // 背景
        actors.addAll(objectForChoose.get(tabIndex)); // 物体
        objectHintText.get(tabIndex).forEach(hint -> actors.addAll(hint.getAllActors())); // 提示文本
        actors.add(PrevTab); // 前一栏
        actors.add(NextTab); // 后一栏
        return actors;
    }

    @Override
    public void addActorsToStage(Stage stage) {
        getAllActors().forEach(actor -> stage.addActor(actor));
    }
}
