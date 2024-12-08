#include <list>

#include "sokoban_const.h"
#include "pos.h"
#include "map.h"
#include "move_info.h"

// 寻找玩家坐标
Pos find_player_pos(Map* map) {
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
    if (map->get_object(&next_pos) == WALL) return false;

    // 下一物体箱子，递归判断
    if (map->get_object(&next_pos) == BOX) return pushable(map, &next_pos, direction);

    // 下一物体空，可以推
    if (map->get_object(&next_pos) == AIR) return true;

    // 其他情况，直接拒绝
    return false;
}

// 对所在物体进行一次指定方向推动，包括玩家
bool doPush(Map* map, Pos* pos, int direction, std::list<MoveInfo>* move_list) {
    // 新坐标
    Pos nextPos = pos->to_direction(direction);

    // 获得物体
    int thisObj = map->get_object(pos), nextObj = map->get_object(&nextPos);

    // 当前物块为玩家
    if (thisObj == PLAYER) {
        // 下一物块为空气
        if (nextObj == AIR) {
            map->set_object(pos, AIR);
            map->set_object(&nextPos, PLAYER);
            move_list->push_back(MoveInfo(*pos, nextPos));
            return true;
        }

        // 下一物块为箱子，连续推动检验通过
        if (nextObj == BOX && pushable(map, &nextPos, direction)) {
            // 复制新地图，已重载为深复制
            Map new_map = *map;

            // 使用循环实现
            Pos current_pos = *pos;
            Pos current_pos_next = current_pos.to_direction(direction);

            // 当前物块非空气，未达结束条件
            while (map->get_object(&current_pos) != AIR) {
                // 移动旧地图对应物块
                new_map.set_object(&current_pos_next, map->get_object(&current_pos));

                // 信息加入到位移列表
                move_list->push_back(MoveInfo(current_pos, current_pos_next));

                // 更新坐标
                current_pos = current_pos_next;
                current_pos_next = current_pos_next.to_direction(direction);
            }

            // 去除最开始玩家位置
            new_map.set_object(pos, AIR);

            // 新地图覆盖旧地图
            map = &Map(new_map);
            return true;
        }

        // 玩家未发生移动
        return false;
    }

    return false;
}
