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

    // 拷贝构造函数：深复制
    Map(const Map& other) : height(other.height), width(other.width) {
        map = new int[height * width]; // 为新对象分配内存
        std::copy(other.map, other.map + height * width, map); // 深复制
    }

    // 拷贝赋值操作符：深复制
    Map& operator=(const Map& other) {
        if (this == &other) return *this;  // 避免自赋值

        // 释放当前对象的内存
        delete[] map;

        // 分配新内存
        height = other.height;
        width = other.width;
        map = new int[height * width];

        // 深复制
        std::copy(other.map, other.map + height * width, map);

        return *this;
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
    inline void set_object(Pos* pos, int value) {
        if (valid_pos(pos->x, pos->y)) return;
        *(map + pos->y * width + pos->x) = value;
    }
};

#endif
