package com.sokoban.core.settings;

public class GameSettings {
    public GraphicsSettings graphics = new GraphicsSettings();
    public SoundSettings sound = new SoundSettings();

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof GameSettings)) return false;
        GameSettings gameSettings = (GameSettings) obj;

        return getGraphics().equals(gameSettings.getGraphics()) && getSound().equals(gameSettings.getSound());
    }

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
