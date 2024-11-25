package com.sokoban.assets;

/**
 * Shader 资源枚举
 * <br><br>
 * 顶点和面的 shader 文件之间使用 | 分割
 * 该类型资源不会被统一读取
 */
public enum ShaderAssets {
    Blur("shaders/blurVertex.glsl|shaders/blurFragment.glsl");

    private final String alias;
    ShaderAssets(String alias) {this.alias = alias;}
    public String getAlias() {return alias;}
    public String getAliasVertex() {return alias.split("\\|")[0];}
    public String getAliasFragment() {return alias.split("\\|")[1];}
}