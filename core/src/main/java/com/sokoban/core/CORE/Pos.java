package com.sokoban.core.CORE;

public class Pos {
    int x, y, z;

    public Pos() {
        this.x = 0;
        this.y = 0;
        this.z = 0;
    }

    public Pos(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
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

    public int getZ() {
        return z;
    }

    public void setZ(int z) {
        this.z = z;
    }

    public Pos(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * 判断两个 Pos 对象是否相等
     * <br><br>
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        // 如果 obj 类型确实为 Pos 类
        if (obj instanceof Pos) {
            // 将类型对象转换为 Pos 类
            Pos anotherPos = (Pos) obj; 

            if (x != anotherPos.x) return false;
            if (y != anotherPos.y) return false;
            if (z != anotherPos.z) return false;
            return true;

        } else {
            // 如果类型不是 Pos 类，这两个对象肯定不一样
            return false;
        }
    }

}
