package com.sokoban.polygon.combine;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.sokoban.Main;
import com.sokoban.assets.ImageAssets;
import com.sokoban.core.Logger;
import com.sokoban.polygon.manager.FontManager;

/**
 * 字符串显示类，由 Image 组合而成
 * @author Life_Checkpoint
 */
public class ImageFontStringObject extends SokobanCombineObject {
    private float buff;
    private String stringContent;
    private ImageAssets[] pageFileEnums;
    private String fntFileData;
    private int spaceRepeat; // 通过空格重复改进空格显示
    List<Image> charImageObject;

    private final float DEFAULT_BUFF = 0f;
    private final ImageAssets[] DEFAULT_PAGE_FILE = new ImageAssets[1];
    private final String DEFAULT_FNT_FILE_DATA;
    private final int DEFAULT_SPACE_REPEAT = 6;

    public ImageFontStringObject(Main gameMain, String stringContent) {
        super(gameMain);
        DEFAULT_PAGE_FILE[0] = ImageAssets.FontpageMetaNormal;
        DEFAULT_FNT_FILE_DATA = gameMain.getAssetsPathManager().fileObj("font/meta-normal.fnt").readString();

        init(stringContent, DEFAULT_BUFF, DEFAULT_SPACE_REPEAT, DEFAULT_PAGE_FILE, DEFAULT_FNT_FILE_DATA);
    }

    public ImageFontStringObject(Main gameMain, String stringContent, float buff) {
        super(gameMain);
        DEFAULT_PAGE_FILE[0] = ImageAssets.FontpageMetaNormal;
        DEFAULT_FNT_FILE_DATA = gameMain.getAssetsPathManager().fileObj("font/meta-normal.fnt").readString();

        init(stringContent, buff, DEFAULT_SPACE_REPEAT, DEFAULT_PAGE_FILE, DEFAULT_FNT_FILE_DATA);
    }

    public ImageFontStringObject(Main gameMain, String stringContent, float buff, int spaceRepeat) {
        super(gameMain);
        DEFAULT_FNT_FILE_DATA = "";

        init(stringContent, buff, spaceRepeat, pageFileEnums, fntFileData);
    }

    public ImageFontStringObject(Main gameMain, String stringContent, float buff, int spaceRepeat, ImageAssets[] pageFileEnums, String fntFileData) {
        super(gameMain);
        DEFAULT_FNT_FILE_DATA = "";

        init(stringContent, buff, spaceRepeat, pageFileEnums, fntFileData);
    }

    private void init(String stringContent, float buff, int spaceRepeat, ImageAssets[] pageFileEnums, String fntFileData) {
        // 空格显示改进
        String improvedString = stringContent.replace(" ", new String(" ").repeat(DEFAULT_SPACE_REPEAT));
        this.stringContent = stringContent;
        this.buff = buff;
        this.pageFileEnums = pageFileEnums;
        this.fntFileData = fntFileData;
        
        charImageObject = new ArrayList<>();
        FontManager fontManager = new FontManager(gameMain, pageFileEnums, fntFileData);

        // 添加字符
        for (int i = 0; i < improvedString.length(); i++) {
            Image currentCharacter = fontManager.getCharImage(improvedString.charAt(i));
            charImageObject.add(currentCharacter);
        }

        // 设置初始位置
        setPosition(0, 0);
    }

    public void reset(String stringContent) {
        charImageObject.forEach(Actor::remove);
        init(stringContent, buff, spaceRepeat, pageFileEnums, fntFileData);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setPosition(float x, float y) {
        float integrateX = x, maxHeight = 0f;

        // 计算最高高度
        for (int i = 0; i < charImageObject.size(); i++) {
            maxHeight = Math.max(maxHeight, charImageObject.get(i).getHeight());
        }

        // 计算累计宽度并移位
        for (int i = 0; i < charImageObject.size(); i++) {
            charImageObject.get(i).setPosition(integrateX, y);
            integrateX += charImageObject.get(i).getWidth() + (i == stringContent.length() - 1 ? 0 : buff);
        }

        this.x = x;
        this.y = y;
        this.width = integrateX - x;
        this.height = maxHeight;
    }

    /**
     * 获取前 k 个文本框的宽度
     * @param k 0 <= k < charImageObject.size()
     * @return 宽度
     */
    public float getIntegrateWidth(int k) {
        if (k == 0) return 0f;

        if (k < 0 || k > charImageObject.size()) {
            Logger.error("ImageFontStringObject", String.format("%d is not a valid subscript. Expect (0, %d)", k, charImageObject.size()));
            return 0f;
        }
        
        // 计算累计宽度
        float integrateWidth = 0;
        for (int i = 0; i < k; i++) {
            integrateWidth += charImageObject.get(i).getWidth() + (i == stringContent.length() - 1 ? 0 : buff);
        }
        return integrateWidth;
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

    public List<Image> getCharImageObject() {
        return charImageObject;
    }
}
