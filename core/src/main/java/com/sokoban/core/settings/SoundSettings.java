package com.sokoban.core.settings;

public class SoundSettings {
    public float masterVolume;
    public float musicVolume;
    public float effectsVolume;

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof SoundSettings)) return false;
        SoundSettings graphicSettings = (SoundSettings) obj;
        
        if (getMasterVolume() != graphicSettings.getMasterVolume()) return false;
        if (getMusicVolume() != graphicSettings.getMusicVolume()) return false;
        if (getEffectsVolume() != graphicSettings.getEffectsVolume()) return false;
        return true;
    }

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
