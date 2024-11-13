package com.sokoban.core.settings;

public class GraphicsSettings {
    public boolean mipmap;
    public boolean vsync;

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof GraphicsSettings)) return false;
        GraphicsSettings graphicSettings = (GraphicsSettings) obj;
        
        if (isMipmap() != graphicSettings.isMipmap()) return false;
        if (isVsync() != graphicSettings.isVsync()) return false;
        return true;
    }

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
