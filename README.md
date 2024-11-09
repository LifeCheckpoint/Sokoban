# Sokoban

利用 `LibGDX` 开发的推箱子游戏，~~作为 Java 课堂的项目作业~~.

美术风格与游戏机制参考了 `Patrick's Parabox` 和 `Baba is You` 两款推箱子解谜游戏.

## Platforms

Desktop 桌面平台, 预计将打包为即开即玩的游戏

## Progress 开发进度

立项时间 `2024.11.5`

|预期功能|实现进度|
|---|---|
|立项|✔️已完成|
|合作开发|▶️进行中|
|素材绘制|▶️进行中|
|推箱子核心玩法|❌未开始|
|基本界面开发|▶️进行中|
|用户管理|❌未开始|
|基础AI|❌未开始|
|动画效果|▶️进行中|
|设置配置|❌未开始|

## Feature 项目特色

1. 基于主流游戏引擎 `LibGDX` 开发，移植性强，可维护性高
2. 允许素材异步加载，支持启用 `Mipmap` `MSAA` 等显示优化，以及加载场景界面
3. 实现自定义的 GUI 组件，组件间通过 `gameMain` 分发全局控制权协调工作
4. 通过高层抽象节省繁复画面元素操作，代码重用率高
5. 基于 `TestNG` 的充分单元 / 覆盖率测试
6. 完全原创的美术素材，附加素材源文件
7. 充分运用 AI 工具，极大加快项目学习编程工作，使工作流在高度抽象的逻辑中进行，必要的底层细节在学习后可以交由 AI 实现

~~（只是因为要答辩所以写这些）~~

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

## Project Requirements 课程项目进度表

*In this project, we will provide 5 basic maps. You must implement these 5 maps as 5 levels of the game. You should finish the following tasks:*

### Task 1: Game Initialization (10 points) 

1. ❌ After the user logs in or chooses the guest mode, the game will directly enter a level (a level selection interface is an advanced requirement) and correctly display the map. The game should also display the level number and the number of character movement. 
2. ❌ The game should allow players to restart a new game at any time during gameplay. (Not exiting the program and run it again.)
3. ❌ When restarting a new game, the game data needs to be consistent with the new game.

### Task 2: Multi-user Login (15 points) 

1. ❌ Implement a login selection interface for both guests and registered users.
2. ❌ Guests can play without registration but do not have the functionality to save game progress. 
3. ❌ The user login interface includes a registration page and allows login after entering account credentials.
4. ❌ After the program exits and is run again, previously registered users can still log in.

### Task 3: Save and Load Games (15 points) 

1. ❌ Each user (except guests) has the option to load their previous saved game; the save is a single save file, and saving again will overwrite the previous save (Overwriting the original save is the basic requirement. Additional points would not be given if multiple save slots are implemented per user.)
2. ❌ From the game start interface, players can choose to load their last save which should contain information about the the game board's status and the number of moves made so far.
3. ❌ Each user's save data is unique.
4. ❌ Manual saving is a basic requirement; implementing automatic saving at timed intervals or upon exit can earn points in the advanced section.
5. ▶️ Save File Error Check: If a save file's format or contents are corrupted when loading, the damaged save will not be loaded, and the game will still run rather than crash. (If your game is capable of detecting save files that have been modified by others while still maintaining the legitimacy of the save data，it will earn the advanced points.)

### Task 4: Gameplay (30 points) 

1. ❌ Pushing Boxes: When a player moves the character, they can push any box in the direction they are moving, provided there is an empty space behind the box. A box can be moved until it hits a wall or another box.
2. ❌ Button control: The interface must include up, down, left, and right buttons to facilitate player movement in different directions. 
3. ❌ Keyboard control: Keyboard control are required for player movement (up, down, left, right) in different directions. 
4. ❌ Game Victory: The goal of the game is to push all the boxes onto the designated target locations. Once all targets are covered by boxes, display a victory screen indicating that the puzzle has been successfully solved.  
5. ❌ Game Fail: If the box cannot be moved (e.g., a box is pushed into a corner) when the game is not victorious, either a game-over message or an option to restart the level should be displayed. 

### Task 5: Graphical User Interface (GUI) (10 points) 

1. ✔️ Implement a graphical interface for the game using JavaFX, Swing, or any other Java 
graphical framework.
2. ❌ You will earn points for this section by completing the code based on the demo provided in the course.
3. ▶️ Independently creating a GUI will count as Advanced points.
4. ⭕ If your program need to input into command line，you can not get full points of this task.

### Task 6: Advanced Features (20 points) 

Any additional features beyond the basic requirements described above will earn points in this advanced category, including but not limited to:

1. ▶️ Enhanced graphics and aesthetics
2. ❌ More game modes design
3. ❌ level selection interface 
4. ❌ Implementation of AI to achieve high scores
5. ▶️ Adding some animated effects
6. ❌ Adding game time display, save time in the archive, introducing a time-limited mode
7. ❌ Adding props in the game 
8. ❌ Adding maps of your own design