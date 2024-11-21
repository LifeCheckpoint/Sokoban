package com.sokoban.core;

enum ObjectType {
    Air(0), 
    Wall(1), 
    Unknown(114514);

    private int typeNumber;
    ObjectType(int typeNumber) {this.typeNumber = typeNumber;}
    public int getTypeNumber() {return typeNumber;}
}

class Mapper {
    public static ObjectType mapNumberToObj(int numberType) {
        if (numberType == 0) {
            return ObjectType.Air;
        }

        if (numberType >= 1 && numberType <= 9) {
            return ObjectType.Wall;
        }

        return ObjectType.Unknown;
    }

    public static int mapObjToNumber(ObjectType obj) {
        // 常量映射数字时，如果有多种可能，只需要返回其中一个即可
        return obj.getTypeNumber();
    }
}

public class ReadSomeThing {
    public static void main(String[] args) {
        System.out.printf("The Number Map To Type: %d -> %s\n", 3, Mapper.mapNumberToObj(3));
        System.out.printf("The Type Map To Number: %s -> %d\n", ObjectType.Wall, Mapper.mapObjToNumber(ObjectType.Wall));
        System.out.printf("The Type Map To Number: %s -> %d\n", ObjectType.Unknown, Mapper.mapObjToNumber(ObjectType.Unknown));

        Thing thing1 = new Thing(ObjectType.Air, 0, 0);

        System.out.println(thing1.getTypeOfThing());

        thing1.setX(1);
        
        if (thing1.getX() == 1) {
            thing1.setTypeOfThing(ObjectType.Wall);
        }

        System.out.println(thing1.getTypeOfThing());
    }
}

class Thing {
    ObjectType typeOfThing;
    int x, y;

    public Thing(ObjectType typeOfThing) {
        this.typeOfThing = typeOfThing;
    }

    public Thing(ObjectType typeOfThing, int x, int y) {
        this.typeOfThing = typeOfThing;
        this.x = x;
        this.y = y;
    }

    public ObjectType getTypeOfThing() {
        return typeOfThing;
    }

    public void setTypeOfThing(ObjectType typeOfThing) {
        this.typeOfThing = typeOfThing;
    }

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


    
}