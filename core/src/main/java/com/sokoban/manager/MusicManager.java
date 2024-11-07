package com.sokoban.manager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.sokoban.enums.AudioEnums;

import java.util.HashMap;
import java.util.Map;

// 管理音乐播放
public class MusicManager {
    private Map<AudioEnums, Music> musicMap;
    private Music currentMusic;
    private float volume = 1.0f;
    private boolean isPlaying = false;

    public MusicManager() {
        musicMap = new HashMap<>();
    }

    public void loadMusic(AudioEnums audioName, String filePath) {
        if (musicMap.containsKey(audioName)) {
            Gdx.app.log("MusicManager", "Music already loaded: " + audioName.toString());
            return;
        }
        Music music = AssetsPathManager.audioLoad(filePath);
        musicMap.put(audioName, music);
    }

    // 播放
    public void play(AudioEnums audioName, boolean loop) {
        // 停止当前音乐
        if (currentMusic != null) {
            currentMusic.stop();
        }

        currentMusic = musicMap.get(audioName);
        if (currentMusic != null) {
            currentMusic.setLooping(loop);
            currentMusic.setVolume(volume);
            currentMusic.play();
            isPlaying = true;
        } else {
            Gdx.app.error("MusicManager", "Music not found: " + audioName);
        }
    }

    // 停止
    public void stop() {
        if (currentMusic != null) {
            currentMusic.stop();
            isPlaying = false;
        }
    }

    // 暂停
    public void pause() {
        if (currentMusic != null && isPlaying) {
            currentMusic.pause();
            isPlaying = false;
        }
    }

    // 从暂停恢复播放
    public void resume() {
        if (currentMusic != null && !isPlaying) {
            currentMusic.play();
            isPlaying = true;
        }
    }

    // 设置音量
    public void setVolume(float volume) {
        this.volume = Math.max(0, Math.min(volume, 1));
        if (currentMusic != null) {
            currentMusic.setVolume(this.volume);
        }
    }

    // 切换到指定音乐
    public void switchMusic(AudioEnums audioName) {
        if (currentMusic != null && currentMusic.isPlaying()) {
            currentMusic.stop();
        }
        play(audioName, true);
    }

    public void switchMusic(AudioEnums audioName, boolean loop) {
        if (currentMusic != null && currentMusic.isPlaying()) {
            currentMusic.stop();
        }
        play(audioName, loop);
    }

    public void dispose() {
        stop();
        for (Music music : musicMap.values()) {
            music.dispose();
        }
        musicMap.clear();
    }

    public float getVolume() {
        return volume;
    }
}
