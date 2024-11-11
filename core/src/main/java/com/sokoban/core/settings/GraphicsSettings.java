package com.sokoban.core.settings;

public class GraphicsSettings {
    public boolean mipmap;
    public boolean vsync;

    public boolean isMipmap() {
        return mipmap;
    }
    public void setMipmap(boolean mipmap) {
        this.mipmap = mipmap;
    }
    public boolean isVsync() {
        return vsync;
    }
    public void setVsync(boolean vsync) {
        this.vsync = vsync;
    }
}
