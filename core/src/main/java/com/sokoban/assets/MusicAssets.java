package com.sokoban.assets;

/**
 * 音乐资源枚举
 */
public enum MusicAssets {
    Light("audio/Light.mp3"),
    Rain("audio/Rain.mp3");

    private final String alias;
    MusicAssets(String alias) {this.alias = alias;}
    public String getAlias() {return alias;}
}