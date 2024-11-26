package com.sokoban.core.CORE;

public class Things {
    Pos position;
    int TypeNumber;
    ObjectType ObjectType;

    public Things(Pos position, int TypeNumber) {
        this.TypeNumber = TypeNumber;
        this.position = position;
        setObjectType();
    }

    public void setObjectType() {
        this.ObjectType = MapMapper.MapNumToType(TypeNumber);
    }

    public ObjectType getObjectType() {
        return ObjectType;
    }

    public Pos getPosition() {
        return position;
    }

    public void setPosition(Pos position) {
        this.position = position;
    }

}

