package com.sokoban.core.CORE;

public class TestMap {
    public static void main(String[] args) {
        int[][] map = new int[5][6];
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 6; j++) {
                map[i][j] = 0;
            }
        }
        for (int i = 0; i < 5; i++) {
            map[i][0] = 1;
            map[i][6] = 1;
        }
        for (int i = 0; i < 6; i++) {
            map[i][0] = 1;
            map[i][4] = 1;
        }
        Things player = new Things(new Pos(1,1,0),1);
        Things box1 = new Things(new Pos(2,3,0),20);
        Things box2 = new Things(new Pos(3,3,0),20);
        Things PosOfBox1 = new Things(new Pos(3,2,0),30);
        Things PosOfBox2 = new Things(new Pos(2,4,0),30);

        Things[] things = new Things[5];
        things[0] = player;
        things[1] = box1;
        things[2] = box2;
        things[3] = PosOfBox1;
        things[4] = PosOfBox2;


    }
}
