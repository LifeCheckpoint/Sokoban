package com.sokoban.polygon.combine;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.sokoban.Main;
import com.sokoban.manager.APManager;
import com.sokoban.manager.FontManager;

/**
 * 字符串显示类，由 Image 组合而成
 * @author Life_Checkpoint
 */
public class ImageFontStringObject extends SokobanCombineObject {
    private float buff;
    private String stringContent;
    List<Image> charImageObject;

    private final float DEFAULT_BUFF = 0f;
    private final APManager.ImageAssets[] DEFAULT_PAGE_FILE = new APManager.ImageAssets[1];
    private final String DEFAULT_FNT_FILE_DATA;

    public ImageFontStringObject(Main gameMain, String stringContent) {
        super(gameMain);
        DEFAULT_PAGE_FILE[0] = APManager.ImageAssets.FontpageMetaNormal;
        DEFAULT_FNT_FILE_DATA = gameMain.getAssetsPathManager().fileObj("font/meta-normal.fnt").readString();

        init(stringContent, DEFAULT_BUFF, DEFAULT_PAGE_FILE, DEFAULT_FNT_FILE_DATA);
    }

    public ImageFontStringObject(Main gameMain, String stringContent, float buff) {
        super(gameMain);
        DEFAULT_PAGE_FILE[0] = APManager.ImageAssets.FontpageMetaNormal;
        DEFAULT_FNT_FILE_DATA = gameMain.getAssetsPathManager().fileObj("font/meta-normal.fnt").readString();

        init(stringContent, buff, DEFAULT_PAGE_FILE, DEFAULT_FNT_FILE_DATA);
    }

    public ImageFontStringObject(Main gameMain, String stringContent, float buff, APManager.ImageAssets[] pageFileEnums, String fntFileData) {
        super(gameMain);
        DEFAULT_FNT_FILE_DATA = "";

        init(stringContent, buff, pageFileEnums, fntFileData);
    }

    private void init(String stringContent, float buff, APManager.ImageAssets[] pageFileEnums, String fntFileData) {
        this.stringContent = stringContent;
        this.buff = buff;
        
        charImageObject = new ArrayList<>();
        FontManager fontManager = new FontManager(gameMain, pageFileEnums, fntFileData);

        // 添加字符
        for (int i = 0; i < stringContent.length(); i++) {
            Image currentCharacter = fontManager.getCharImage(stringContent.charAt(i));
            charImageObject.add(currentCharacter);
        }

        // 设置初始位置
        setPosition(0, 0);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setPosition(float x, float y) {
        float integrateX = x, maxHeight = -1f;

        // 计算最高高度
        for (int i = 0; i < stringContent.length(); i++) {
            maxHeight = Math.max(maxHeight, charImageObject.get(i).getHeight());
        }

        // 计算累计宽度并移位
        for (int i = 0; i < stringContent.length(); i++) {
            charImageObject.get(i).setPosition(integrateX, y);
            integrateX += charImageObject.get(i).getWidth() + (i == stringContent.length() - 1 ? 0 : buff);
        }

        width = integrateX - x;
        height = maxHeight;
    }

    /**
     * {@inheritDoc}
     * @param stage
     */
    @Override
    public void addActorsToStage(Stage stage) {
        charImageObject.forEach(dig -> stage.addActor(dig));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Actor> getAllActors() {
        List<Actor> actors = new ArrayList<>();
        actors.addAll(charImageObject);
        return actors;
    }
}
