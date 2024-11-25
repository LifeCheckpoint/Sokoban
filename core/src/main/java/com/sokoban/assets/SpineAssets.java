package com.sokoban.assets;

/**
 * Spine Atlas 资源枚举
 * <br><br>
 * 只有 atlas 会被统一读取，Json <b>不会</b>被统一读取
 */
public enum SpineAssets {
    Checkbox("spine/checkbox/checkbox.atlas|spine/checkbox/checkbox.json"),
    Slider("spine/slider/slider.atlas|spine/slider/slider.json"),
    Numbers("spine/numbers/numbers.atlas|spine/numbers/numbers.json"),
    Rectangle("spine/rec/rec.atlas|spine/rec/rec.json"),
    Player1("spine/player_1/player_1.atlas|spine/player_1/player_1.json"),
    Timer("spine/timer/timer.atlas|spine/timer/timer.json"),
    
    BoxBlueBox("spine/boxes/blue_box/blue_box.atlas|spine/boxes/blue_box/blue_box.json"),
    BoxCornerRightDown("spine/boxes/box_corner_rightdown/box_corner_rightdown.atlas|spine/boxes/box_corner_rightdown/box_corner_rightdown.json"),
    BoxDarkBlueBack("spine/boxes/darkblue_pixel/darkblue_pixel.atlas|spine/boxes/darkblue_pixel/darkblue_pixel.json"),
    BoxDarkGrayBack("spine/boxes/darkgray_pixel/darkgray_pixel.atlas|spine/boxes/darkgray_pixel/darkgray_pixel.json"),
    BoxGreenBox("spine/boxes/green_box/green_box.atlas|spine/boxes/green_box/green_box.json"),
    BoxBoxTarget("spine/boxes/box_target/box_target.atlas|spine/boxes/box_target/box_target.json"),
    BoxGreenBoxLight("spine/boxes/green_box_light/green_box_light.atlas|spine/boxes/green_box_light/green_box_light.json"),
    BoxPlayerTarget("spine/boxes/player_target/player_target.atlas|spine/boxes/player_target/player_target.json");

    private final String alias;
    SpineAssets(String alias) {this.alias = alias;}
    public String getAlias() {return alias;}
    public String getAliasAtlas() {return alias.split("\\|")[0];}
    public String getAliasJson() {return alias.split("\\|")[1];}
}