# `CutVertexCheckerDocument` 割点检查算法类

## 主要背景、问题

写一些便捷功能或搜索算法的时候，我们常常会遇到需要判断**玩家是否能从箱子某一侧到达另一侧**的问题

传统的算法，判断玩家是否能从一个点移动到另一个点，需要纯 `BFS` 或 `DFS` 这样“大水漫灌”或“逐步搜索”的方法，寻找最终结果

然而这种算法的复杂度是 $O(n^2)$，面对较大的地图可能会花费较长的时间。具体解释如下：

> “在一个箱子推动路径搜索过程中，要反复判断人是否能从箱子的一侧自由移动（即不推动箱子情况下）到箱子的另一侧
> 
> “这个不难判断，用简单的广度和深度优先搜索都能在线性时间内得到答案
> 
> “但是箱子推动过程中，箱子位置在变化，要在不同的位置都作出判断
> 
> “假设涉及到的格子有 $n$ 个，每判断一次要 $O(n)$ 时间，但箱子最多也可能出现在 $n$ 个不同的格子，要做 $n$ 次这样的判断，所以总的时间复杂度是 $O(n^2)$
> 
> “当关卡比较大时，如《一箭十万》是 $50\times50$ 的关卡，不算墙体，格子也上千，导致计算时间比较长

那么，**割点算法**通过其 $O(V+E)$ 的优秀时间复杂度，成为我们进行判断的首选

## 割点算法引入

一个推箱子地图的割点，指的是**能够把一个连通区域切割成两个或多个区域的位置**

例如

```
#######
# #   #
# #  ##
#     #
#   # #
#######
```

这个地图的割点有

```
#######
# # o #
#o#  ##
#o ooo#
#   # #
#######
```

如果知道了地图的割点，我们就可以优化玩家行动的计算：

1. 如果箱子不在割点上，说明玩家一定能从箱子一侧移动到另一侧
2. 如果箱子在割点上，如果箱子两侧的块处在同一个连通区域，那么也能移动，反之不行

## 参考资料

[推箱子游戏的一个箱子推动路径搜索算法 （二）](http://sokoban.ws/blog/?p=843)

[用深度优先搜索(DFS)确定图的割点](http://sokoban.ws/blog/?p=1000)