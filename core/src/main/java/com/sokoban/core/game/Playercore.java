package com.sokoban.core.game;

public class Playercore {

    

    // public void moveUp(ObjectType player) {
    //     if (player != null && player.position != null) {
    //         player.position.setX(player.position.getX() - 1);
    //     }
    // }

    // public void moveDown(ObjectType player) {
    //     if (player != null && player.position != null) {
    //         player.position.setX(player.position.getX() + 1);
    //     }
    // }

    // public void moveLeft(ObjectType player) {
    //     if (player != null && player.position != null) {
    //         player.position.setY(player.position.getY() - 1);
    //     }
    // }

    // public void moveRight(ObjectType player) {
    //     if (player != null && player.position != null) {
    //         player.position.setY(player.position.getY() + 1);
    //     }
    // }

    // public boolean ifmoveUp(ObjectType player, int[][] map) {
    //     if (player != null && player.position != null) {
    //         Pos position = player.position;
    //         int typeclose = map[position.getX() - 1][position.getY()];
    //         int typefar = map[position.getX() - 2][position.getY()];
    //         if (MapMapper.MapNumToType(typeclose) == ObjectType.AIR) {
    //             return true;
    //         } else if (MapMapper.MapNumToType(typeclose) == ObjectType.BOX
    //                 && MapMapper.MapNumToType(typefar) == ObjectType.AIR) {
    //             return true;
    //         } else
    //             return false;
    //     }
    //     return false;
    // }

    // public boolean ifmoveDown(ObjectType player, int[][] map) {
    //     if (player != null && player.position != null) {
    //         Pos position = player.position;
    //         int typeclose = map[position.getX() + 1][position.getY()];
    //         int typefar = map[position.getX() + 2][position.getY()];
    //         if (MapMapper.MapNumToType(typeclose) == ObjectType.AIR) {
    //             return true;
    //         } else if (MapMapper.MapNumToType(typeclose) == ObjectType.BOX
    //                 && MapMapper.MapNumToType(typefar) == ObjectType.AIR) {
    //             return true;
    //         } else
    //             return false;
    //     }
    //     return false;
    // }

    // public boolean ifmoveLeft(ObjectType player, int[][] map) {
    //     if (player != null && player.position != null) {
    //         Pos position = player.position;
    //         int typeclose = map[position.getY() - 1][position.getY()];
    //         int typefar = map[position.getY() - 2][position.getY()];
    //         if (MapMapper.MapNumToType(typeclose) == ObjectType.AIR) {
    //             return true;
    //         } else if (MapMapper.MapNumToType(typeclose) == ObjectType.BOX
    //                 && MapMapper.MapNumToType(typefar) == ObjectType.AIR) {
    //             return true;
    //         } else
    //             return false;
    //     }
    //     return false;
    // }

    // public boolean ifmoveRight(ObjectType player, int[][] map) {
    //     if (player != null && player.position != null) {
    //         Pos position = player.position;
    //         int typeclose = map[position.getY() + 1][position.getY()];
    //         int typefar = map[position.getY() + 2][position.getY()];
    //         if (MapMapper.MapNumToType(typeclose) == ObjectType.AIR) {
    //             return true;
    //         }
    //         if (MapMapper.MapNumToType(typeclose) == ObjectType.BOX
    //                 && MapMapper.MapNumToType(typefar) == ObjectType.AIR) {
    //             return true;
    //         } else
    //             return false;
    //     }
    //     return false;
    // }

    // public void plMoveUp(ObjectType player, int[][] map, ObjectType[] ObjectType) {
    //     if (player != null && player.position != null) {
    //         Pos position = player.position;
    //         int typeclose = map[position.getX() - 1][position.getY()];
    //         if (MapMapper.MapNumToType(typeclose) == ObjectType.BOX) {
    //             if (ifmoveUp(player, map)) {
    //                 Pos position1 = new Pos(position.getX() - 1, position.getY());
    //                 for (int i = 0; i < ObjectType.length; i++) {
    //                     if (position1.equals(ObjectType[i].position)) {
    //                         moveUp(ObjectType[i]);
    //                     }
    //                 }
    //                 moveUp(player);
    //             }
    //         } else {
    //             if (ifmoveUp(player, map)) {
    //                 moveUp(player);
    //             }
    //         }
    //     }
    // }

    // public void plMoveDown(ObjectType player, int[][] map, ObjectType[] ObjectType) {
    //     if (player != null && player.position != null) {
    //         Pos position = player.position;
    //         int typeclose = map[position.getX() + 1][position.getY()];
    //         if (MapMapper.MapNumToType(typeclose) == ObjectType.BOX) {
    //             if (ifmoveDown(player, map)) {
    //                 Pos position1 = new Pos(position.getX() + 1, position.getY());
    //                 for (int i = 0; i < ObjectType.length; i++) {
    //                     if (position1.equals(ObjectType[i].position)) {
    //                         moveDown(ObjectType[i]);
    //                     }
    //                 }
    //                 moveDown(player);
    //             }
    //         } else {
    //             if (ifmoveDown(player, map)) {
    //                 moveDown(player);
    //             }
    //         }
    //     }
    // }

    // public void plMoveLeft(ObjectType player, int[][] map, ObjectType[] ObjectType) {
    //     if (player != null && player.position != null) {
    //         Pos position = player.position;
    //         int typeclose = map[position.getX()][position.getY() - 1];
    //         if (MapMapper.MapNumToType(typeclose) == ObjectType.BOX) {
    //             if (ifmoveLeft(player, map)) {
    //                 Pos position1 = new Pos(position.getX(), position.getY() - 1);
    //                 for (int i = 0; i < ObjectType.length; i++) {
    //                     if (position1.equals(ObjectType[i].position)) {
    //                         moveLeft(ObjectType[i]);
    //                     }
    //                 }
    //                 moveLeft(player);
    //             }
    //         } else {
    //             if (ifmoveLeft(player, map)) {
    //                 moveLeft(player);
    //             }
    //         }
    //     }
    // }

    // public void plMoveRight(ObjectType player, int[][] map, ObjectType[] ObjectType) {
    //     if (player != null && player.position != null) {
    //         Pos position = player.position;
    //         int typeclose = map[position.getX()][position.getY() + 1];
    //         if (MapMapper.MapNumToType(typeclose) == ObjectType.BOX) {
    //             if (ifmoveRight(player, map)) {
    //                 Pos position1 = new Pos(position.getX(), position.getY() + 1);
    //                 for (int i = 0; i < ObjectType.length; i++) {
    //                     if (position1.equals(ObjectType[i].position)) {
    //                         moveRight(ObjectType[i]);
    //                     }
    //                 }
    //                 moveRight(player);
    //             }
    //         } else {
    //             if (ifmoveRight(player, map)) {
    //                 moveRight(player);
    //             }
    //         }
    //     }
    // }
}
