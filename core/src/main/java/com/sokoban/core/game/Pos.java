package com.sokoban.core.game;

import java.util.Objects;

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

    public Pos(int x, int y) {
        this.x = x;
        this.y = y;
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

    /**
     * 坐标加法
     */
    public Pos add(Pos pos) {
        return new Pos(x + pos.x, y + pos.y, z + pos.z);
    }

    /**
     * 坐标减法
     */
    public Pos sub(Pos pos) {
        return new Pos(x - pos.x, y - pos.y, z - pos.z);
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

    /**
     * 深复制
     * @return 深复制对象
     */
    public Pos cpy() {
        return new Pos(x, y, z);
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z);
    }
}
