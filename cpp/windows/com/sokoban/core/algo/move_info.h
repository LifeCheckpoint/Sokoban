#include "sokoban_const.h"
#include "pos.h"

#ifndef MOVE_INFO_H
#define MOVE_INFO_H

struct MoveInfo {
    Pos from;
    Pos to;

    MoveInfo(Pos from, Pos to) : from(from), to(to) {};
};

#endif
