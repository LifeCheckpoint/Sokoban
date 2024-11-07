package com.sokoban;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.sokoban.manager.AssetsPathManager;

public class AssetsList {
    public void setAssets(AssetsPathManager apm) {

        // Audio
        apm.addAsset(Music.class, "Light.mp3");
        apm.addAsset(Music.class, "Rain.mp3");

        // Image
        // apm.addAsset(Texture.class, "Light.mp3");
    }
}
