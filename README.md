# Sokoban

利用 `LibGDX` 开发的推箱子游戏，~~作为 Java 课堂的项目作业~~.

美术风格与游戏机制参考了 `Patrick's Parabox` 和 `Baba is You` 两款推箱子解谜游戏, 操作逻辑融合游戏 `A Dance of Fire and Ice` 等

## Platforms

Desktop 桌面平台, 预计将打包为即开即玩的游戏

## Progress 开发进度

立项时间 `2024.11.5`

|预期功能|实现进度|
|---|---|
|立项|✔️已完成|
|合作开发|✔️已完成|
|素材绘制|✔️已完成|
|推箱子核心玩法|✔️已完成|
|基本界面开发|✔️已完成|
|用户管理|✔️已完成|
|基础AI|✔️已完成|
|动画效果|✔️已完成|
|设置配置|✔️已完成|

当前版本 `1.0.0`

## Feature 项目特色

1. 基于主流游戏引擎 `LibGDX` 开发，移植性强，可维护性高
2. 允许素材异步加载，支持启用 `Mipmap` `MSAA` 等显示优化
3. 原创美术素材，附加素材源文件，实现大量自定义的 GUI 动画组件，界面美观整洁
4. GUI 代码采用 `服务定位器` 与 `单例模式` 的结合设计 ，通过 `gameMain` 分发全局控制句柄，简化开发，重用率高
5. 核心功能基于 `TestNG` 进行了充分单元 / 覆盖率测试
6. 通过 `JNI` 高效进行跨语言协作，并且保证游戏对特定平台 api 的兼容

~~（只是因为要答辩所以写这些）~~

## Project Requirements 课程项目进度表

*In this project, we will provide 5 basic maps. You must implement these 5 maps as 5 levels of the game. You should finish the following tasks:*

### Task 1: Game Initialization (10 points) 

1. ▶️ After the user logs in or chooses the guest mode, the game will directly enter a level (a level selection interface is an advanced requirement) and correctly display the map. The game should also display the level number and the number of character movement. 
2. ✔️ The game should allow players to restart a new game at any time during gameplay. (Not exiting the program and run it again.)
3. ✔️ When restarting a new game, the game data needs to be consistent with the new game.

### Task 2: Multi-user Login (15 points) 

1. ✔️ Implement a login selection interface for both guests and registered users.
2. ✔️ Guests can play without registration but do not have the functionality to save game progress. 
3. ✔️ The user login interface includes a registration page and allows login after entering account credentials.
4. ✔️ After the program exits and is run again, previously registered users can still log in.

### Task 3: Save and Load Games (15 points) 

1. ✔️ Each user (except guests) has the option to load their previous saved game; the save is a single save file, and saving again will overwrite the previous save (Overwriting the original save is the basic requirement. Additional points would not be given if multiple save slots are implemented per user.)
2. ✔️ From the game start interface, players can choose to load their last save which should contain information about the the game board's status and the number of moves made so far.
3. ✔️ Each user's save data is unique.
4. ✔️ Manual saving is a basic requirement; implementing automatic saving at timed intervals or upon exit can earn points in the advanced section.
5. ✔️ Save File Error Check: If a save file's format or contents are corrupted when loading, the damaged save will not be loaded, and the game will still run rather than crash. (If your game is capable of detecting save files that have been modified by others while still maintaining the legitimacy of the save data，it will earn the advanced points.)

### Task 4: Gameplay (30 points) 

1. ✔️ Pushing Boxes: When a player moves the character, they can push any box in the direction they are moving, provided there is an empty space behind the box. A box can be moved until it hits a wall or another box.
2. ✔️ Button control: The interface must include up, down, left, and right buttons to facilitate player movement in different directions. 
3. ✔️ Keyboard control: Keyboard control are required for player movement (up, down, left, right) in different directions. 
4. ✔️ Game Victory: The goal of the game is to push all the boxes onto the designated target locations. Once all targets are covered by boxes, display a victory screen indicating that the puzzle has been successfully solved.  
5. ✔️ Game Fail: If the box cannot be moved (e.g., a box is pushed into a corner) when the game is not victorious, either a game-over message or an option to restart the level should be displayed. 

### Task 5: Graphical User Interface (GUI) (10 points) 

1. ✔️ Implement a graphical interface for the game using JavaFX, Swing, or any other Java 
graphical framework.
2. ✔️ You will earn points for this section by completing the code based on the demo provided in the course.
3. ✔️ Independently creating a GUI will count as Advanced points.
4. ✔️ If your program need to input into command line，you can not get full points of this task.

### Task 6: Advanced Features (20 points) 

Any additional features beyond the basic requirements described above will earn points in this advanced category, including but not limited to:

1. ✔️ Enhanced graphics and aesthetics
2. ✔️ More game modes design
3. ✔️ level selection interface 
4. ✔️ Implementation of AI to achieve high scores
5. ✔️ Adding some animated effects
6. ✔️ Adding game time display, save time in the archive, introducing a time-limited mode
7. ✔️ Adding props in the game 
8. ✔️ Adding maps of your own design

## Log 更新日志

### 1.0.0 beta update

1. 项目截止
2. 新增自动求解功能
3. 新增移动按钮
4. 视角现在会跟随移动
5. 角落死锁检测提示

### 0.4.1 update

1. 新增了第二个大关卡 Moving，目前有 5 个地图
2. 新增了撤销功能，点按 Z 可以进行撤销
3. 存档功能现在可以使用了
4. 竞速模式现在可以使用了
5. 优化了视觉效果
6. 修复了编辑器内存泄露的问题（暂时性）

### 0.4.0 update

1. 推箱子游戏现在可以开始游玩了
2. 完善了第一个关卡选择界面 Origin
3. 新增了大关卡 Origin 的五个地图
4. 重构代码，优化地图更新效率
5. 调整了游玩界面退出菜单的界面
6. 修复了地图文件不存在会导致关卡选择界面崩溃问题
7. 修复询问框重叠和响应问题
8. 修复人物移动有概率卡墙问题
9. 修复编辑器打开时自动删除路径的问题

### 0.3.2 update

1. 新增了对游戏核心逻辑的处理，即将推出
2. 完善关卡编辑器的操作逻辑，现在可以使用它编辑关卡了
3. 优化游戏的动画美术效果
4. 修复 DLL 运行依赖不完整问题
5. 修复编辑器无法覆盖保存文件的问题

### 0.3.1 update

1. 新增了关卡编辑器功能，下拉菜单等动效组件
2. 新增玩家存档选择功能
3. 新增重玩按钮
4. 新增了教程关
5. 优化了日志输出
6. 修复设置初始化无法创建目录的问题
7. 修复初始设置错误的问题

### 0.3.0 update

1. 新增用户注册与登录功能
2. 优化日志显示
3. 优化了一部分素材表现

### 0.2.3 update

1. 新增体积阻挡机制
2. 新增主游戏界面
3. 优化了代码逻辑
4. 修复画面闪烁的问题
5. 修复地图进入崩溃的问题
6. 修复玩家移动异常的问题

### 0.2.2 update

1. 新增箱子素材
2. 优化一部分视觉效果
3. 修复 `Esc` 键退出场景会崩溃的问题
4. 修复 FPS 与游戏逻辑耦合的问题
5. 修复键盘位移控制异常的问题
6. 修复玩家行动位移边界

### 0.2.1 update

1. 新增网格小世界组件
2. 新增箱子素材
3. 新增了计时器小组件
4. 优化键盘操作逻辑
5. 修复主界面箱子移动后会重叠的问题

### 0.2.0 update

1. 新增了关卡选择场景，以及选择动效
2. 新增了地图选择场景，支持自由移动
3. 新增玩家形象及动画
4. 新增调整 `MSAA` 设置项
5. 优化计数器效果
6. 修复初始音量与设置读取不一致的问题

### 0.1.4 update

1. 新增对位图字体切分读取功能
2. 新增字符串图像与提示条组件
3. 新增设置保存功能
4. 新增 GUI 测试屏幕
5. 优化素材加载方式
6. 修复复选框无法选中的 bug

### 0.1.3 update

1. 新增垂直同步、音量设置
2. 新增滑块条、计数器组件
3. 优化一些组件的渐变加载

### 0.1.2 update

1. 新增 `Json` 解析与测试
2. 新增设置界面框架
3. 重构素材加载机制，统一管理素材加载

### 0.1.1 update

1. 新增对 `Spine` 组件的支持，这也是游戏的核心图形效果实现引擎之一
2. 新增地图加载器的框架
3. 新增复选框组件及其动画

### 0.1.0 Update

1. `Sokoban` 项目的第一个初始界面搭建完成，包括背景音乐，粒子效果，鼠标跟踪，基础素材等等
2. 这是发布到 `main` 分支的第一个版本

## Structure 项目结构

有机会再解释