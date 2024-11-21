package com.sokoban.core.CORE;

public class Playercore {

    public void moveUp(Things player) {
        if (player != null && player.getPosition() != null) {
            player.getPosition().setX(player.getPosition().getX() - 1);
        }
    }

    public void moveDown(Things player) {
        if (player != null && player.getPosition() != null) {
            player.getPosition().setX(player.getPosition().getX() + 1);
        }
    }

    public void moveLeft(Things player) {
        if (player != null && player.getPosition() != null) {
            player.getPosition().setY(player.getPosition().getY() - 1);
        }
    }

    public void moveRight(Things player) {
        if (player != null && player.getPosition() != null) {
            player.getPosition().setY(player.getPosition().getY() + 1);
        }
    }
}
