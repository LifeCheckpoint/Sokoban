package com.sokoban.assets;

/**
 * 图像资源枚举
 */
public enum ImageAssets {
    AboutButton("img/button/about.png"),
    AboutInfo("img/about_info.png"),
    AboutInfoEGG("img/about_info2.png"),
    BlackPixel("img/pixel/black_pixel.png"),
    Box("img/box.png"),
    BoxActive("img/box_active.png"),
    BoxTarget("img/target.png"),
    CancelButton("img/button/cancel.png"),
    ContinueGame("img/button/continue_game.png"),
    EffectsVolume("img/effects_volume.png"),
    ExitButton("img/button/exit.png"),
    GrayPixel("img/pixel/gray_pixel.png"),
    GuestModeButton("img/button/guest_mode.png"),
    LeftArrowButton("img/button/left_arrow.png"),
    LeftSquareArrow("img/button/left_square_arrow.png"),
    LoadingAssetsLabel("img/loading_assets.png"),
    LoginButton("img/button/login.png"),
    LogOutButton("img/button/log_out.png"),
    MasterVolume("img/master_volume.png"),
    Mipmap("img/button/mipmap.png"),
    MSAA("img/button/msaa.png"),
    MusicVolume("img/music_volume.png"),
    PlayAgainButton("img/button/play_again.png"),
    RacingModeButton("img/button/racing_mode.png"),
    RegisterButton("img/button/register.png"),
    RememberPasswordButton("img/button/remember_password.png"),
    RightArrowButton("img/button/right_arrow.png"),
    RightSquareArrow("img/button/right_square_arrow.png"),
    Save1Button("img/button/save_1.png"),
    Save2Button("img/button/save_2.png"),
    Save3Button("img/button/save_3.png"),
    SaveButton("img/button/save.png"),
    SelectArchiveButton("img/button/select_archive.png"),
    SettingsButton("img/button/settings.png"),
    ShowLevel1("img/show_level_1.png"),
    ShowLevel2("img/show_level_2.png"),
    ShowLevel3("img/show_level_3.png"),
    StartGameButton("img/button/start_game.png"),
    Vsync("img/button/vsync.png"),
    WhitePixel("img/pixel/white_pixel.png"),

    FontpageMetaNormal("font/meta-normal.png");

    private final String alias;
    ImageAssets(String alias) {this.alias = alias;}
    public String getAlias() {return alias;}
}