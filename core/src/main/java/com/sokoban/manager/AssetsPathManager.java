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

/**
 * 统一资源管理器
 * @author Life_Checkpoint
 */
public class AssetsPathManager {
    private static final String audioPath = "audio";
    private static final String texturePath = "img";
    private static final String shaderPath = "shaders";
    private static final String skinPath = "ui";
    private static final String soundPath = "sound";

    private static final boolean usingMipMap = true;

    AssetManager assetManager = new AssetManager();

    // 资源类型-路径映射表
    private Map<Class<?>, List<String>> assetsMap = new HashMap<>();

    public AssetsPathManager() {}

    /**
     * 向映射表加入资源<br><br>
     * <b>只有 Texture, Music Audio Sound Skin 可以使用路径缩写</b><br><br>
     * <b> Atlas 等文件使用全称而非路径缩写</b><br><br>
     * SkeletonData 暂未得出实现方案，暂时不实现
     * @param resourceClass 资源类型
     * @param resourcePath 资源路径
     */
    public <T> void addAsset(Class<T> resourceClass, String resourcePath) {
        assetsMap.computeIfAbsent(resourceClass, k -> new ArrayList<>()).add(resourcePath);
    }

    // 更新加载器
    public boolean update() {
        return assetManager.update();
    }

    // 获取加载进度
    public float getProgress() {
        return assetManager.getProgress();
    }

    // 将加载字典注入加载器
    // 注意，不包含 Shader
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
                     if (usingMipMap) assetManager.load(textureFile(resourcePath), Texture.class, textureMipmapParam);
                     else assetManager.load(textureFile(resourcePath), Texture.class);

                } else if (resourceClass == Music.class) {
                    assetManager.load(audioFile(resourcePath), Music.class);

                } else if (resourceClass == Skin.class) {
                    assetManager.load(skinFile(resourcePath), Skin.class);

                } else if (resourceClass == Sound.class) {
                    assetManager.load(soundFile(resourcePath), Sound.class);

                } else if (resourceClass == TextureAtlas.class) {
                    assetManager.load(resourcePath, TextureAtlas.class);

                }
            }
        }

        assetsMap.clear();
    }

    // 获得资源对象
    public <T> T get(String resourcePath, Class<T> resourceClass) {
        String fullPath;

        // 根据资源类型添加相对路径前缀
        if (resourceClass == Texture.class) {
            fullPath = textureFile(resourcePath);
        } else if (resourceClass == Music.class) {
            fullPath = audioFile(resourcePath);
        } else if (resourceClass == Skin.class) {
            fullPath = skinFile(resourcePath);
        } else if (resourceClass == Sound.class) {
            fullPath = soundFile(resourcePath);
        } else {
            fullPath = resourcePath;
        }

        // 未实际加载的资源进行同步加载
        if (!assetManager.isLoaded(fullPath)) {
            Gdx.app.log("AssetsPathManager", fullPath + " hasn't loaded by AssetManager. Load synchronously");
            assetManager.load(fullPath, resourceClass);
            assetManager.finishLoading();
        }

        return assetManager.get(fullPath, resourceClass);
    }

    public static Music audioLoad(String fileName) {
        return Gdx.audio.newMusic(Gdx.files.internal(audioFile(fileName)));
    }
    public static Texture textureLoad(String fileName) {
        return new Texture(textureFile(fileName));
    }
    public static ShaderProgram shaderLoad(String vertexFileName, String fragmentFileName) {
        return new ShaderProgram(
            Gdx.files.internal(shaderFile(vertexFileName)),
            Gdx.files.internal(shaderFile(fragmentFileName))
        );
    }
    public static Skin skinLoad(String skinJsonFile) {
        return new Skin(Gdx.files.internal(skinFile(skinJsonFile)));
    }
    public static Sound soundLoad(String soundFile) {
        return Gdx.audio.newSound(Gdx.files.internal(soundFile(soundFile)));
    }
    public static TextureAtlas textureAtlasLoad(String testureAtlasFile) {
        return new TextureAtlas(Gdx.files.internal(testureAtlasFile));
    }

    public static String audioFile(String fileName) {
        return audioPath + "/" + fileName;
    }
    public static String textureFile(String fileName) {
        return texturePath + "/" + fileName;
    }
    public static String shaderFile(String fileName) {
        return shaderPath + "/" + fileName;
    }
    public static String skinFile(String fileName) {
        return skinPath + "/" + fileName;
    }
    public static String soundFile(String fileName) {
        return soundPath + "/" + fileName;
    }

    public static String getAudioPath() {
        return audioPath;
    }
    public static String getTexturePath() {
        return texturePath;
    }
    public static String getShaderPath() {
        return shaderPath;
    }
    public static String getSkinPath() {
        return skinPath;
    }
    public static String getSoundPath() {
        return soundPath;
    }

    public FileHandle fileObj(String filePath) {
        return Gdx.files.internal(filePath);
    }
    public void dispose() {
        assetManager.dispose();
    }

}
