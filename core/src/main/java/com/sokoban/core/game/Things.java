package com.sokoban.core.game;

public class Things {
    public Pos position = new Pos();
    public ObjectType objectType;

    public Things() {}

    public Things(Pos position, ObjectType object) {
        this.position = position;
        this.objectType = object;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Things)) return false;
        Things anotherThing = (Things) obj;

        if (!anotherThing.position.equals(position)) return false;
        if (!anotherThing.objectType.equals(objectType)) return false;

        return true;
    }
}

