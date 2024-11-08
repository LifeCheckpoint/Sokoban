# Sokoban

利用 `GDX` 开发的推箱子小游戏，作为 Java 课堂的项目作业.

## Platforms

Desktop 桌面平台, 预计将打包为即开即玩的游戏

## Progress 开发进度

建立时间 `2024.11.5`

|预期功能|实现进度|
|---|---|
|立项|✔️|
|合作开发|▶️进行中|
|素材绘制|▶️进行中|
|推箱子核心玩法|❌|
|基本界面开发|▶️进行中|
|用户管理|❌|
|基础AI|❌|
|动画效果|▶️进行中|
|设置配置|❌|

## Structure 项目结构

**关键项目文件如下**

```
> .gradle           用于构建项目依赖包的 gradle 配置文件
> .vscode           vscode生成的一些编辑器配置文件
> assets            游戏使用的资源文件，会被打包到游戏中
    > audio             游戏音乐存放
    > img               游戏图像素材存放
    > shaders           着色器 (glsl) 代码
    > sound             游戏音效存放
    assets.txt          素材清单
> assets-backup     资源文件备份，结构同 assets
> core              游戏核心代码
    > src
        > main  游戏主程序代码
            > java\com\sokoban
                > enums     游戏用到的枚举常数
                    AudioEnums.java     音频枚举
                    ParticleEnums.java  粒子枚举
                > manager   游戏一些运行机制的管理
                    AssetsPathManager                   所有素材的统一管理类
                    BackgroundGrayParticleManager.java  背景灰色漂浮粒子效果管理类
                    JsonManager.java                    JSON 数据管理类
                    MouseMovingTraceManager.java        画面鼠标跟踪管理类
                    MusicManager.java                   背景音乐管理类
                    ScreenManager.java                  游戏场景切换管理类
                > polygon   游戏一些图形组件的实现
                    BackgroundParticle.java     背景粒子图形
                    ImageButtonContainer.java   图像按钮容器
                    ImageLabelContainer.java    图像标签容器
                    InputTextField.java         输入框
                    TextureSquare.java          素材方块
                    WhiteProgressBar.java       进度条
                > scenes    游戏不同场景的实现
                    AboutScene.java         关于场景
                    GameWelcomeScene.java   游戏主界面
                    LoadingScene.java       素材加载界面
                    SettingScene.java       设置界面
                    SokoyoScene.java        抽象界面类，是所有界面的父类
                AssetsList.java     存放要加载素材的类
                Main.java           游戏主类，负责总体控制
                MathUtilsEx.java    数学计算补充类
        > test  测试代码
> develop-resources 存放素材的一些源文件
    > img               作为素材的一些图像
    > raw               图像源文件
> develop-tool      一些帮助开发构建的工具
    > img-crop          图像裁剪工具
> gradle            构建项目依赖包的 gradle
> lwjgl3            游戏绘图引擎相关文件
    > src\main\java\com\sokoban\lwjgl3
        Lwjgl3Launcher.java 游戏启动配置
        StartupHelper.java  游戏启动器
README.md           此文档
```