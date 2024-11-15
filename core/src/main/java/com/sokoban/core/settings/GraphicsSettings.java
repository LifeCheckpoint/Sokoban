package com.sokoban.core.settings;

public class GraphicsSettings {
    public boolean mipmap;
    public boolean vsync;
    public int msaa;

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof GraphicsSettings)) return false;
        GraphicsSettings graphicSettings = (GraphicsSettings) obj;
        
        if (isMipmap() != graphicSettings.isMipmap()) return false;
        if (isVsync() != graphicSettings.isVsync()) return false;
        if (getMsaa() != graphicSettings.getMsaa()) return false;
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
    public int getMsaa() {
        return msaa;
    }
    public void setMsaa(int msaa) {
        this.msaa = msaa;
    }
}
