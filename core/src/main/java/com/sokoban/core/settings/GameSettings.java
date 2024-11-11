package com.sokoban.core.settings;

public class GameSettings {
    public GraphicsSettings graphics;
    public SoundSettings sound;

    public GraphicsSettings getGraphics() {
        return graphics;
    }
    public void setGraphics(GraphicsSettings graphics) {
        this.graphics = graphics;
    }
    public SoundSettings getSound() {
        return sound;
    }
    public void setSound(SoundSettings sound) {
        this.sound = sound;
    }
}
