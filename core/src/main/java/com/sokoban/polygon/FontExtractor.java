package com.sokoban.polygon;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.sokoban.Main;
import com.sokoban.manager.APManager;
import com.sokoban.polygon.container.ImageLabelContainer;

import java.util.HashMap;
import java.util.Map;

public class FontExtractor {
    // 存储每个字符的 TextureRegion
    private Map<Character, TextureRegion> charMap;
    private Texture[] pages;
    private Main gameMain;

    public FontExtractor(Main gameMain, APManager.ImageAssets[] pageFileEnums, String fntFileData) {
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
}
