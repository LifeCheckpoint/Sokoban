#include "sokoban_const.h"
#include "pos.h"

#ifndef MAP_H
#define MAP_H

struct Map {
    int* map;
    int height;
    int width;

    // 动态分配二维数组
    Map(int h, int w) : height(h), width(w) {
        map = new int[height * width]; // 分配一维内存空间来表示二维数组
    }

    // 释放动态分配内存
    ~Map() {
        delete[] map;
    }

    // 判断位置是否合法
    inline int valid_pos(int x, int y) const {
        return x < 0 || y < 0 || x >= width || y >= height;
    }

    // 判断位置是否合法
    inline int valid_pos(Pos* pos) const {
        return pos->x < 0 || pos->y < 0 || pos->x >= width || pos->y >= height;
    }

    // 获取位置 (x, y) 的对象值
    inline int get_object(int x, int y) const {
        if (valid_pos(x, y)) return UNKNOWN;
        return *(map + y * width + x);
    }

    // 获取位置 pos 的对象值
    inline int get_object(Pos* pos) const {
        if (valid_pos(pos->x, pos->y)) return UNKNOWN;
        return *(map + pos->y * width + pos->x);
    }

    // 设置位置 (x, y) 的对象值
    inline void set_object(int x, int y, int value) {
        if (valid_pos(x, y)) return;
        *(map + y * width + x) = value;
    }

    // 设置位置 pos 的对象值
    inline void get_object(Pos* pos, int value) {
        if (valid_pos(pos->x, pos->y)) return;
        *(map + pos->y * width + pos->x) = value;
    }
};

#endif
