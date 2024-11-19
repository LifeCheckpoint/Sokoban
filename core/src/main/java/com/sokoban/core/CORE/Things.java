package com.sokoban.core.CORE;

public class Things {
    int x, y, z;
    int TypeNumber;
    ObjType ObjType;
    public Things(int x, int y, int z, int TypeNumber) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.TypeNumber = TypeNumber;
        getObjType();
    }
    public void getObjType() {
        this.ObjType = method.mapper.MapNumToType(TypeNumber);
    }
}


class pos {
    int x, y, z;

    public int getX() {
        return x;
    }
    public void setX(int x) {
        this.x = x;
    }
    public int getY() {
        return y;
    }
    public void setY(int y) {
        this.y = y;
    }
    public int getZ() {
        return z;
    }
    public void setZ(int z) {
        this.z = z;
    }

    public pos(int x, int y) {
        this.x = x;
        this.y = y;
    }
    public pos(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
}

enum ObjType{
    AIR,WALL,PLAYER,PlayerGetBoxpos,PlayerGetPLpos,BOX,BoxGetPLpos,PosOfBox,PosOfPL,Unknown
}

class method{
    class mapper{
        public static ObjType MapNumToType (int num){
            if(num == 0)
                return ObjType.AIR;
            else if (num >= 1 && num <= 9) {
                return ObjType.WALL;
            }
            else if (num == 10) {
                return ObjType.PLAYER;
            }
            else if (num == 11) {
                return ObjType.PlayerGetBoxpos;
            }
            else if (num == 12) {
                return ObjType.PlayerGetPLpos;
            }
            else if (num == 20) {
                return ObjType.BOX;
            }
            else if (num == 21) {
                return ObjType.BoxGetPLpos;
            }
            else if (num == 30) {
                return ObjType.PosOfBox;
            }
            else if (num == 31) {
                return ObjType.PosOfPL;
            }
            else
                return ObjType.Unknown;
        }
    }

}
