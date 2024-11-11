package com.sokoban.core.settings;

public class SoundSettings {
    public float masterVolume;
    public float musicVolume;
    public float effectsVolume;

    public float getMasterVolume() {
        return masterVolume;
    }
    public void setMasterVolume(float masterVolume) {
        this.masterVolume = masterVolume;
    }
    public float getMusicVolume() {
        return musicVolume;
    }
    public void setMusicVolume(float musicVolume) {
        this.musicVolume = musicVolume;
    }
    public float getEffectsVolume() {
        return effectsVolume;
    }
    public void setEffectsVolume(float effectsVolume) {
        this.effectsVolume = effectsVolume;
    } 
}
