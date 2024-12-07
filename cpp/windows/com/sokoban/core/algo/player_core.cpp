#include "sokoban_const.h"
#include "pos.h"
#include "map.h"

// 寻找玩家坐标
Pos find_player_pos(Map* map) const {
    for (int y = 0; y < map->height; y++) {
        for (int x = 0; x < map->width; x++) {
            if (map->get_object(x, y) == PLAYER) return Pos(x, y);
        }
    }
    return Pos(-1, -1);
}

// 判断指定方向上的物块是否能连续推动
// pos: 当前物体坐标
bool pushable(Map* map, Pos* pos, int direction) {
    // 下一物块坐标
    Pos next_pos = pos->to_direction(direction);

    // 当前物体不是箱子，不可以推
    if (map->get_object(pos) != BOX) return false;

    // 下一物体超出边界
    if (!map->valid_pos(&next_pos)) return false;

    // 下一物体墙
    if (map->get_object(pos) == WALL) return false;

    // 下一物体箱子，递归判断
    

}