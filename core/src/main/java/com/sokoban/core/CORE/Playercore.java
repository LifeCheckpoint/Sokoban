package com.sokoban.core.CORE;

public class Playercore {
    public void moveup(Things player) {
        Pos Posreturn;
        Posreturn = player.getPosition();
        Posreturn.setX(Posreturn.getX() - 1);
    }

    public void movedown(Things player) {
        Pos Posreturn;
        Posreturn = player.getPosition();
        Posreturn.setX(Posreturn.getX() + 1);
    }

    public void moveleft(Things player) {
        Pos Posreturn;
        Posreturn = player.getPosition();
        Posreturn.setX(Posreturn.getY() - 1);
    }

    public void movedownleft(Things player) {
        Pos Posreturn;
        Posreturn = player.getPosition();
        Posreturn.setX(Posreturn.getX() + 1);
    }

}
