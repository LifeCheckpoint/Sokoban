// #include "stdio.h"
#include "sokoban_const.h"

#ifndef POS_H
#define POS_H

struct Pos {
    int x;
    int y;
    int z;

    Pos(int x, int y) : x(x), y(y), z(0) {}
    Pos(int x, int y, int z) : x(x), y(y), z(z) {}

    Pos add(const Pos& pos) const {
        return Pos(x + pos.x, y + pos.y, z + pos.z);
    }

    Pos sub(const Pos& pos) const {
        return Pos(x - pos.x, y - pos.y, z - pos.z);
    }

    Pos delta_direction(int direction) const {
        switch (direction) {
        case UP:
            return Pos(0, 1);
        case DOWN:
            return Pos(0, -1);
        case LEFT:
            return Pos(-1, 0);
        case RIGHT:
            return Pos(1, 0);
        case NONE_DIRECTION:
            return Pos(0, 0);
        default:
            // throw std::invalid_argument("Invalid direction");
            return Pos(0, 0);
        }
    }

    Pos to_direction(int direction) const {
        Pos delta = delta_direction(direction);
        return Pos(x + delta.x, y + delta.y, z + delta.z);
    }
};

#endif
