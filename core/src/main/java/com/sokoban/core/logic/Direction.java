package com.sokoban.core.logic;

public enum Direction {
    None("none"), Right("right"), Down("down"), Left("left"), Up("up");
    private String value;
    Direction(String value) {this.value = value;}
    public String getDirection() {return value;}
}