# 有关地图类的说明

## `MapData` 的构成

`MapData` 是一个具有众多字段的类，主要由以下几个部分构成：

1. **地图路径信息** `mapFileInfo`

`mapFileInfo` 是一个 `MapInfo` 类对象，用于储存一些有关这个大地图的文件信息，一般在逻辑处理中不需要理会

2. **地图附加信息** `addtionalInfo`

`addtionalInfo` 是一个 `String` 类对象，用于说明一些地图的附加信息，例如标题等，一般在逻辑处理中不需要理会

3. **所有子地图** `allMaps`

`allMaps` 是一个 `SubMapData` 类对象，储存了当前大地图所有的子地图，如果一个关卡有多个子地图就会用上的

在处理逻辑的时候，大多数情况（普通玩法）都只是在一个子地图上进行

获取子地图数量用 `allMaps.size()`

## `SubMapData` 的构成

为什么一个子地图不直接使用 `ObjectType[][]` 这样一个二维数组表示呢？

答案是————这样管理比较混乱

例如，如果我们只有这么一个二维数组表示地图，那么我们可能要规定很多符号来表示地图上的**组合物体**：

> 1 -> `Player`
> 
> 2 -> `PlayerAtBoxTarget`
> 
> 3 -> `PlayerAtPlayerTarget`
> 
> ...

这样会非常繁琐。因此，如果我们换个思路，使用多层 `ObjectType[][]` 来管理，例如：

> 假定一共有三层子地图，**第零层**存放当前地图实际存在的物体，**第一层**存放目标点，**第二层**存放一些地图的装饰，
>
> 第零层只会有：`Player` `Box` ...
> 
> 第一层只会有两种目标点：`PlayerTarget` `BoxTarget`
>
> 第二层 ...

可以看到，这样分工就较为明确，最终设计就得到了 `SubMapData` 这个存放子地图的类

当然，具体访问第几层，不需要 `MapData` 关心，`SubMapData` 已经封装好了访问函数：

1. `getObjectLayer()` 获得地图的物体层
2. `getTargetLayer()` 获得地图的目标点层
3. `getDecorationLayer()` 获得地图的装饰层，在逻辑处理中较少用到，但是前端会用

具体的使用方法也很简单，移动的时候在物体层移动，判断 game over 的时候就检查目标点对应的坐标在物体层中对应物体是否存在即可

当然，如果需要获得具体是第几层，可以访问 `SubMapData` 的如下公开字段：

```Java
public static final int LAYER_OBJECT = 0; // 存放地图物体的层索引
public static final int LAYER_TARGET = 1; // 存放地图目标点的层索引
public static final int LAYER_DECORATION = 2; // 存放地图装饰的层索引
```

*如果有任何补充，需要及时更新该文档*