package com.sokoban.core.CORE;

public class Playercore {
    public void moveup(Things player){
        pos posreturn;
        posreturn = player.getPosition();
        posreturn.setX(posreturn.getX()-1);
    }
    public void movedown(Things player){
        pos posreturn;
        posreturn = player.getPosition();
        posreturn.setX(posreturn.getX()+1);
    }
    public void moveleft(Things player){
        pos posreturn;
        posreturn = player.getPosition();
        posreturn.setX(posreturn.getY()-1);
    }
    public void movedownleft(Things player){
        pos posreturn;
        posreturn = player.getPosition();
        posreturn.setX(posreturn.getX()+1);
    }


    






}
