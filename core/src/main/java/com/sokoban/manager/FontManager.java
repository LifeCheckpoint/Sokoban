package com.sokoban.manager;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.sokoban.Main;
import com.sokoban.assets.ImageAssets;
import com.sokoban.polygon.container.ImageLabelContainer;

import java.util.HashMap;
import java.util.Map;

/**
 * 位图字体管理器，从 BitmapFont 文件中读取字体
 * @author ChatGPT
 */
public class FontManager {
    /** 
     * 存储每个字符的 TextureRegion
     */
    private Map<Character, TextureRegion> charMap;
    
    private Texture[] pages;
    private Main gameMain;

    /**
     * 位图字体管理器构造
     * @param gameMain 全局句柄
     * @param pageFileEnums 字体页位图资源枚举
     * @param fntFileData fnt 字体数据文件内容
     */
    public FontManager(Main gameMain, ImageAssets[] pageFileEnums, String fntFileData) {
        this.gameMain = gameMain;
        
        // 初始化字体页数组和字符映射
        pages = new Texture[pageFileEnums.length];
        charMap = new HashMap<>();

        // 加载每一页纹理
        for (int i = 0; i < pageFileEnums.length; i++) {
            pages[i] = gameMain.getAssetsPathManager().get(pageFileEnums[i]);
        }

        // 解析fnt文件数据，并填充 charMap
        parseFntFileData(fntFileData);
    }

    /**
     * fnt 文件格式解析
     * @param fntFileData fnt 文件内容
     */
    private void parseFntFileData(String fntFileData) {
        String[] lines = fntFileData.split("\n");

        for (String line : lines) {
            if (line.startsWith("char id=")) {
                // 解析每一行的字符数据
                String[] parts = line.split("\\s+");
                int id = Integer.parseInt(parts[1].split("=")[1]);          // 字符ID
                int x = Integer.parseInt(parts[2].split("=")[1]);           // 纹理x位置
                int y = Integer.parseInt(parts[3].split("=")[1]);           // 纹理y位置
                int width = Integer.parseInt(parts[4].split("=")[1]);       // 字符宽度
                int height = Integer.parseInt(parts[5].split("=")[1]);      // 字符高度
                int page = Integer.parseInt(parts[10].split("=")[1]);       // 所在页

                // 通过TextureRegion裁剪出字符图像区域
                TextureRegion region = new TextureRegion(pages[page], x, y, width, height);

                // 将字符ID和TextureRegion存储到charMap中
                charMap.put((char) id, region);
            }
        }
    }

    /**
     * 获取字符的 TextureRegion
     * @param character 字符
     * @return 字符对应 TextureRegion
     */
    public TextureRegion getCharRegion(char character) {
        return charMap.get(character);
    }

    /**
     * 获取字符的 Image
     * @param character 字符
     * @return 字符对应 Image
     */
    public Image getCharImage(char character) {
        ImageLabelContainer charImageLabelContainer = new ImageLabelContainer(gameMain);
        return charImageLabelContainer.create(new TextureRegionDrawable(getCharRegion(character)));
    }

    /**
     * 获取字符的 Image
     * @param character 字符
     * @param scaling 缩放比例
     * @return 字符对应 Image
     */
    public Image getCharImage(char character, float scaling) {
        ImageLabelContainer charImageLabelContainer = new ImageLabelContainer(gameMain, scaling);
        return charImageLabelContainer.create(new TextureRegionDrawable(getCharRegion(character)));
    }
}
