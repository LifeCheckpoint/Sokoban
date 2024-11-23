package com.sokoban.core.CORE;

import com.sokoban.core.CORE.TestMap;

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

    public boolean ifmoveUp(Things player, int[][] map) {
        if (player != null && player.getPosition() != null) {
            Pos position = player.getPosition();
            int typeclose = map[position.getX() - 1][position.getY()];
            int typefar = map[position.getX() - 2][position.getY()];
            if (MapMapper.MapNumToType(typeclose) == ObjectType.AIR) {
                return true;
            } else if (MapMapper.MapNumToType(typeclose) == ObjectType.BOX
                    && MapMapper.MapNumToType(typefar) == ObjectType.AIR) {
                return true;
            } else
                return false;
        }
        return false;
    }

    public boolean ifmoveDown(Things player, int[][] map) {
        if (player != null && player.getPosition() != null) {
            Pos position = player.getPosition();
            int typeclose = map[position.getX() + 1][position.getY()];
            int typefar = map[position.getX() + 2][position.getY()];
            if (MapMapper.MapNumToType(typeclose) == ObjectType.AIR) {
                return true;
            } else if (MapMapper.MapNumToType(typeclose) == ObjectType.BOX
                    && MapMapper.MapNumToType(typefar) == ObjectType.AIR) {
                return true;
            } else
                return false;
        }
        return false;
    }

    public boolean ifmoveLeft(Things player, int[][] map) {
        if (player != null && player.getPosition() != null) {
            Pos position = player.getPosition();
            int typeclose = map[position.getY() - 1][position.getY()];
            int typefar = map[position.getY() - 2][position.getY()];
            if (MapMapper.MapNumToType(typeclose) == ObjectType.AIR) {
                return true;
            } else if (MapMapper.MapNumToType(typeclose) == ObjectType.BOX
                    && MapMapper.MapNumToType(typefar) == ObjectType.AIR) {
                return true;
            } else
                return false;
        }
        return false;
    }

    public boolean ifmoveRight(Things player, int[][] map) {
        if (player != null && player.getPosition() != null) {
            Pos position = player.getPosition();
            int typeclose = map[position.getY() + 1][position.getY()];
            int typefar = map[position.getY() + 2][position.getY()];
            if (MapMapper.MapNumToType(typeclose) == ObjectType.AIR) {
                return true;
            }
            if (MapMapper.MapNumToType(typeclose) == ObjectType.BOX
                    && MapMapper.MapNumToType(typefar) == ObjectType.AIR) {
                return true;
            } else
                return false;
        }
        return false;
    }

    public void plMoveUp(Things player, int[][] map, Things[] things) {
        if (player != null && player.getPosition() != null) {
            Pos position = player.getPosition();
            int typeclose = map[position.getX() - 1][position.getY()];
            if (MapMapper.MapNumToType(typeclose) == ObjectType.BOX) {
                if (ifmoveUp(player, map)) {
                    Pos position1 = new Pos(position.getX() - 1, position.getY());
                    for (int i = 0; i < things.length; i++) {
                        if (position1 == things[i].getPosition()) {
                            moveUp(things[i]);
                        }
                    }
                    moveUp(player);
                }
            } else {
                if (ifmoveUp(player, map)) {
                    moveUp(player);
                }
            }
        }
    }

    public void plMoveDown(Things player, int[][] map, Things[] things) {
        if (player != null && player.getPosition() != null) {
            Pos position = player.getPosition();
            int typeclose = map[position.getX() + 1][position.getY()];
            if (MapMapper.MapNumToType(typeclose) == ObjectType.BOX) {
                if (ifmoveDown(player, map)) {
                    Pos position1 = new Pos(position.getX() + 1, position.getY());
                    for (int i = 0; i < things.length; i++) {
                        if (position1 == things[i].getPosition()) {
                            moveDown(things[i]);
                        }
                    }
                    moveDown(player);
                }
            } else {
                if (ifmoveDown(player, map)) {
                    moveDown(player);
                }
            }
        }
    }

    public void plMoveLeft(Things player, int[][] map, Things[] things) {
        if (player != null && player.getPosition() != null) {
            Pos position = player.getPosition();
            int typeclose = map[position.getX()][position.getY() - 1];
            if (MapMapper.MapNumToType(typeclose) == ObjectType.BOX) {
                if (ifmoveLeft(player, map)) {
                    Pos position1 = new Pos(position.getX(), position.getY() - 1);
                    for (int i = 0; i < things.length; i++) {
                        if (position1 == things[i].getPosition()) {
                            moveLeft(things[i]);
                        }
                    }
                    moveLeft(player);
                }
            } else {
                if (ifmoveLeft(player, map)) {
                    moveLeft(player);
                }
            }
        }
    }

    public void plMoveRight(Things player, int[][] map, Things[] things) {
        if (player != null && player.getPosition() != null) {
            Pos position = player.getPosition();
            int typeclose = map[position.getX()][position.getY() + 1];
            if (MapMapper.MapNumToType(typeclose) == ObjectType.BOX) {
                if (ifmoveRight(player, map)) {
                    Pos position1 = new Pos(position.getX(), position.getY() + 1);
                    for (int i = 0; i < things.length; i++) {
                        if (position1 == things[i].getPosition()) {
                            moveRight(things[i]);
                        }
                    }
                    moveRight(player);
                }
            } else {
                if (ifmoveRight(player, map)) {
                    moveRight(player);
                }
            }
        }
    }

    

}
