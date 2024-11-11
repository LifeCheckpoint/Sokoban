package com.sokoban.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.TextureLoader.TextureParameter;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.sokoban.Main;

/**
 * 统一资源管理器 AssetsPathManager
 * @author Life_Checkpoint
 */
public class APManager {
    private static boolean usingMipMap = true;

    AssetManager assetManager = new AssetManager();
    // 资源类型-路径映射表
    private Map<Class<?>, List<String>> assetsMap = new HashMap<>();

    /**
     * 资源管理器构造
     * @param gameMain 游戏全局句柄
     */
    public APManager(Main gameMain) {
        usingMipMap = gameMain.getSettingManager().gameSettings.graphics.mipmap;
    }

    /*  以下为资源枚举 */

    /**
     * 图像资源枚举
     */
    public enum ImageAssets {
        AboutButton("img/button/about.png"),
        AboutInfo("img/about_info.png"),
        AboutInfoEGG("img/about_info2.png"),
        Box("img/box.png"),
        BoxActive("img/box_active.png"),
        BoxTarget("img/target.png"),
        ExitButton("img/button/exit.png"),
        LeftArrowButton("img/button/left_arrow.png"),
        LoadingAssetsLabel("img/loading_assets.png"),
        Mipmap("img/button/mipmap.png"),
        ParticleGray("img/particle1.png"),
        PlayerNormal("img/player_normal.png"),
        SettingsButton("img/button/settings.png"),
        StartGameButton("img/button/start_game.png"),
        WhitePixel("img/white_pixel.png");

        private final String alias;
        ImageAssets(String alias) {this.alias = alias;}
        public String getAlias() {return alias;}
    }

    /**
     * 音乐资源枚举
     */
    public enum MusicAssets {
        Light("audio/Light.mp3"),
        Rain("audio/Rain.mp3");

        private final String alias;
        MusicAssets(String alias) {this.alias = alias;}
        public String getAlias() {return alias;}
    }

    /**
     * Spine Atlas 资源枚举
     */
    public enum SpineAtlasAssets {
        Player1("img/test_player1/player1_sp.atlas"),
        Checkbox("img/checkbox/checkbox.atlas");

        private final String alias;
        SpineAtlasAssets(String alias) {this.alias = alias;}
        public String getAlias() {return alias;}
    }

    /**
     * Spine Atlas 资源枚举
     * <br><br>
     * 该类型资源不会被统一读取
     */
    public enum SpineJsonAssets {
        Player1("img/test_player1/player1_sp.json"),
        Checkbox("img/checkbox/checkbox.json");

        private final String alias;
        SpineJsonAssets(String alias) {this.alias = alias;}
        public String getAlias() {return alias;}
    }

    /**
     * Shader 资源枚举
     * <br><br>
     * 顶点和面的 shader 文件之间使用 | 分割
     * 该类型资源不会被统一读取
     */
    public enum ShaderAssets {
        Blur("shaders/blurVertex.glsl|shaders/blurFragment.glsl");

        private final String alias;
        ShaderAssets(String alias) {this.alias = alias;}
        public String getAlias() {return alias;}
        public String getAliasVertex() {return alias.split("\\|")[0];}
        public String getAliasFragment() {return alias.split("\\|")[1];}
    }

    /*  以上为资源枚举 */

    /**
     * 通过以上定义的资源枚举<b>将所有 Image Music 和 Spine 资源加入映射表</b>
     */
    public void preloadAllAssets() {

        // 加载 ImageAssets
        for (ImageAssets imageAsset : ImageAssets.values()) {
            addAsset(Texture.class, imageAsset.getAlias());
        }

        // 加载 MusicAssets
        for (MusicAssets musicAsset : MusicAssets.values()) {
            addAsset(Music.class, musicAsset.getAlias());
        }

        // 加载 SpineAssets
        for (SpineAtlasAssets spineAsset : SpineAtlasAssets.values()) {
            addAsset(TextureAtlas.class, spineAsset.getAlias());
        }
    }
    
    /**
     * 向映射表加入资源
     * <br><br>
     * <b>只有 Texture, Music Audio Sound Skin 可以使用路径缩写</b>
     * <br><br>
     * <b> Atlas 等文件使用全称而非路径缩写</b>
     * @param resourceClass 资源类型
     * @param resourcePath 资源路径
     */
    public <T> void addAsset(Class<T> resourceClass, String resourcePath) {
        assetsMap.computeIfAbsent(resourceClass, k -> new ArrayList<>()).add(resourcePath);
    }

    /** 
     * 更新加载器
     */
    public boolean update() {
        return assetManager.update();
    }

    /** 
     * 获取加载进度
     */
    public float getProgress() {
        return assetManager.getProgress();
    }

    /** 
     * 将加载字典注入加载器
     * <br><br>
     * 某些类型只能使用同步加载而非当前异步加载
     */
    public void startAssetsLoading() {

        // mipmap配置
        TextureParameter textureMipmapParam = new TextureParameter();
        textureMipmapParam.minFilter = TextureFilter.Linear;
        textureMipmapParam.genMipMaps = true;

        for (Map.Entry<Class<?>, List<String>> entry : assetsMap.entrySet()) {
            Class<?> resourceClass = entry.getKey();
            List<String> resourcePaths = entry.getValue();

            for (String resourcePath : resourcePaths) {
                // 根据类型加载资源

                if (resourceClass == Texture.class) {
                    if (usingMipMap) assetManager.load(resourcePath, Texture.class, textureMipmapParam);
                    else assetManager.load(resourcePath, Texture.class);
                } else if (resourceClass == Music.class) {
                    assetManager.load(resourcePath, Music.class);
                } else if (resourceClass == Skin.class) {
                    assetManager.load(resourcePath, Skin.class);
                } else if (resourceClass == Sound.class) {
                    assetManager.load(resourcePath, Sound.class);
                } else if (resourceClass == TextureAtlas.class) {
                    assetManager.load(resourcePath, TextureAtlas.class);
                }
            }
        }

        assetsMap.clear();
    }

    /**
     * 获得 Image 资源对象
     * @param resourceEnum 图像资源枚举
     * @return 指定资源
     */
    public Texture get(ImageAssets resourceEnum) {
        return get(resourceEnum.getAlias(), Texture.class);
    }

    /**
     * 获得 Music 资源对象
     * @param resourceEnum 音乐资源枚举
     * @return 指定资源
     */
    public Music get(MusicAssets resourceEnum) {
        return get(resourceEnum.getAlias(), Music.class);
    }

    /**
     * 获得 Spine 资源对象
     * @param resourceEnum Spine 枚举
     * @return 指定资源
     */
    public TextureAtlas get(SpineAtlasAssets resourceEnum) {
        return get(resourceEnum.getAlias(), TextureAtlas.class);
    }

    /**
     * 获得 Shader 资源对象
     */
    public ShaderProgram get(ShaderAssets resourceEnum) {
        return shaderLoad(resourceEnum.getAliasVertex(), resourceEnum.getAliasFragment());
    }

    /**
     * 获得资源对象
     * @param resourcePath 资源路径
     * @param resourceClass 资源类型
     * @return 指定资源
     */
    public <T> T get(String resourcePath, Class<T> resourceClass) {
        // 未实际加载的资源进行同步加载
        if (!assetManager.isLoaded(resourcePath)) {
            Gdx.app.log("AssetsPathManager", resourcePath + " hasn't loaded by AssetManager. Load synchronously");
            assetManager.load(resourcePath, resourceClass);
            assetManager.finishLoading();
        }

        return assetManager.get(resourcePath, resourceClass);
    }

    public static Music audioLoad(String fileName) {
        return Gdx.audio.newMusic(Gdx.files.internal(fileName));
    }
    public static Texture textureLoad(String fileName) {
        return new Texture(fileName);
    }
    public static ShaderProgram shaderLoad(String vertexFileName, String fragmentFileName) {
        return new ShaderProgram(
            Gdx.files.internal(vertexFileName),
            Gdx.files.internal(fragmentFileName)
        );
    }
    public static Skin skinLoad(String skinJsonFile) {
        return new Skin(Gdx.files.internal(skinJsonFile));
    }
    public static Sound soundLoad(String soundFile) {
        return Gdx.audio.newSound(Gdx.files.internal(soundFile));
    }
    public static TextureAtlas textureAtlasLoad(String testureAtlasFile) {
        return new TextureAtlas(Gdx.files.internal(testureAtlasFile));
    }

    public FileHandle fileObj(String filePath) {
        return Gdx.files.internal(filePath);
    }
    public void dispose() {
        assetManager.dispose();
    }

}