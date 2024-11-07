package com.sokoban.manager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

// 文件路径管理
public class AssetsPathManager {
    private static final String audioPath = "audio";
    private static final String imagePath = "img";
    private static final String shaderPath = "shaders";
    private static final String uiPath = "ui";

    public static Music audioLoad(String fileName) {
        return Gdx.audio.newMusic(Gdx.files.internal(audioFile(fileName)));
    }
    public static Texture textureLoad(String fileName) {
        return new Texture(imageFile(fileName));
    }
    public static ShaderProgram shaderLoad(String vertexFileName, String fragmentFileName) {
        return new ShaderProgram(
            Gdx.files.internal(shaderFile(vertexFileName)),
            Gdx.files.internal(shaderFile(fragmentFileName))
        );
    }

    public static String audioFile(String fileName) {
        return audioPath + "/" + fileName;
    }
    public static String imageFile(String fileName) {
        return imagePath + "/" + fileName;
    }
    public static String shaderFile(String fileName) {
        return shaderPath + "/" + fileName;
    }
    public static String uiFile(String fileName) {
        return uiPath + "/" + fileName;
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
