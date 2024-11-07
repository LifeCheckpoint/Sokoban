package com.sokoban.manager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;

// 文件路径管理，fileName 中所有的 . 都会被替换为 /
public class AssetsPathManager {
    private static final String audioPath = "audio";
    private static final String imagePath = "img";
    private static final String shaderPath = "shaders";
    private static final String uiPath = "ui";

    public static Music audioLoad(String fileName) {
        return Gdx.audio.newMusic(Gdx.files.internal(audioFile(fileName)));
    }
    public static Texture imageLoad(String fileName) {
        return new Texture(imageFile(fileName));
    }

    public static String audioFile(String fileName) {
        return audioPath + "/" + fileName.replace(".", "/");
    }
    public static String imageFile(String fileName) {
        return imagePath + "/" + fileName.replace(".", "/");
    }
    public static String shaderFile(String fileName) {
        return shaderPath + "/" + fileName.replace(".", "/");
    }
    public static String uiFile(String fileName) {
        return uiPath + "/" + fileName.replace(".", "/");
    }

    public static String getAudioPath() {
        return audioPath;
    }
    public static String getImagePath() {
        return imagePath;
    }
    public static String getShaderPath() {
        return shaderPath;
    }
    public static String getUiPath() {
        return uiPath;
    }
}
