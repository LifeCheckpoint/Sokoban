package com.sokoban;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.sokoban.manager.AssetsPathManager;

public class AssetsList {
    public void setAssets(AssetsPathManager apm) {

        // Audio
        apm.addAsset(Music.class, "Light.mp3");
        apm.addAsset(Music.class, "Rain.mp3");

        // Image
        apm.addAsset(Texture.class, "about_info.png");
        apm.addAsset(Texture.class, "about_info2.png");
        apm.addAsset(Texture.class, "about.png");
        apm.addAsset(Texture.class, "box_active.png");
        apm.addAsset(Texture.class, "box.png");
        apm.addAsset(Texture.class, "exit.png");
        apm.addAsset(Texture.class, "left_arrow.png");
        apm.addAsset(Texture.class, "loading_assets.png");
        apm.addAsset(Texture.class, "particle1.png");
        apm.addAsset(Texture.class, "player_normal.png");
        apm.addAsset(Texture.class, "settings.png");
        apm.addAsset(Texture.class, "start_game.png");
        apm.addAsset(Texture.class, "target.png");
        apm.addAsset(Texture.class, "white_pixel.png");

        // Spine Atlas
        apm.addAsset(TextureAtlas.class, "img/test_player1/player1_sp.atlas");
        apm.addAsset(TextureAtlas.class, "img/checkbox/checkbox.atlas");
    }
}
