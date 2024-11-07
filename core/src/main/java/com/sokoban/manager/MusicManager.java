package com.sokoban.manager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.sokoban.enums.AudioEnums;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

// 管理音乐播放
public class MusicManager {
    private AssetsPathManager apManager;
    private Map<AudioEnums, Music> musicMap;
    private Music currentMusic;
    private float volume = 1.0f;
    private boolean isPlaying = false;

    public MusicManager(AssetsPathManager apManager) {
        musicMap = new HashMap<>();
        this.apManager = apManager;
    }

    public void loadMusic(AudioEnums audioName, String filePath) {
        if (musicMap.containsKey(audioName)) {
            Gdx.app.log("MusicManager", "Music already loaded: " + audioName.toString());
            return;
        }
        Music music = apManager.get(filePath, Music.class);
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

            // 设置播放结束回调
            currentMusic.setOnCompletionListener(music -> onMusicCompleted());
        } else {
            Gdx.app.error("MusicManager", "Music not found: " + audioName);
        }
    }

    private void onMusicCompleted() {
        if (currentMusic != null) {
            play(getRandomAudioEnum(), false);
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

    // 从 AudioEnums 中随机选择枚举
    public AudioEnums getRandomAudioEnum() {
        AudioEnums[] values = AudioEnums.values();  // 获取所有枚举值
        Random random = new Random();
        int index = random.nextInt(values.length);  // 生成随机索引
        return values[index];  // 返回随机选中的枚举值
    }
}
