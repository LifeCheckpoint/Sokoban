package com.sokoban.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

// 文件路径管理
public class AssetsPathManager {
    private static final String audioPath = "audio";
    private static final String texturePath = "img";
    private static final String shaderPath = "shaders";
    private static final String skinPath = "ui";
    private static final String soundPath = "sound";

    AssetManager assetManager = new AssetManager();

    // 用于存储资源类型与路径的映射
    private Map<Class<?>, List<String>> assetsMap = new HashMap<>();

    /**
     * 向加载字典加入资源
     * @param resourceClass 资源类型
     * @param resourcePath 资源路径
     */
    public <T> void addAsset(Class<T> resourceClass, String resourcePath) {
        assetsMap.computeIfAbsent(resourceClass, k -> new ArrayList<>()).add(resourcePath);
    }

    public void setAssetsMap(Map<Class<?>, List<String>> assetsMap) {
        this.assetsMap = assetsMap;
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
        for (Map.Entry<Class<?>, List<String>> entry : assetsMap.entrySet()) {
            Class<?> resourceClass = entry.getKey();
            List<String> resourcePaths = entry.getValue();

            for (String resourcePath : resourcePaths) {
                // 根据类型加载资源
                if (resourceClass == Texture.class) {
                    assetManager.load(textureFile(resourcePath), Texture.class);

                } else if (resourceClass == Music.class) {
                    assetManager.load(audioFile(resourcePath), Music.class);

                } else if (resourceClass == Skin.class) {
                    assetManager.load(skinFile(resourcePath), Skin.class);

                } else if (resourceClass == Sound.class) {
                    assetManager.load(soundFile(resourcePath), Sound.class);
                }
            }
        }

        assetsMap.clear();
    }

    // 获得资源对象
    public <T> T get(String resourcePath, Class<T> resourceClass) {
        String fullPath = resourcePath;

        // 根据资源类型添加相对路径前缀
        if (resourceClass == Texture.class) {
            fullPath = textureFile(resourcePath);
        } else if (resourceClass == Music.class) {
            fullPath = audioFile(resourcePath);
        } else if (resourceClass == Skin.class) {
            fullPath = skinFile(resourcePath);
        } else if (resourceClass == Sound.class) {
            fullPath = soundFile(resourcePath);
        }

        // 未实际加载的资源进行同步加载
        if (!assetManager.isLoaded(fullPath)) {
            Gdx.app.log("AssetsPathManager", fullPath + " is not loaded by AssetManager. Load synchronously");
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

    public void dispose() {
        assetManager.dispose();
    }

}
