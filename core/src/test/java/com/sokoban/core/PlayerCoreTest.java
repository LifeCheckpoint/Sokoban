package com.sokoban.core;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.sokoban.core.CORE.Playercore;
import com.sokoban.core.CORE.Pos;
import com.sokoban.core.CORE.Things;

public class PlayerCoreTest {

    private Playercore playercore;
    private Things player;

    @BeforeMethod
    public void setUp() {
        playercore = new Playercore();
        player = new Things(new Pos(5, 5, 0), 10); // 创建一个玩家，初始位置为 (5, 5, 0)
    }

    @Test
    public void testMoveUp() {
        playercore.moveUp(player);
        Assert.assertEquals(player.getPosition().getX(), 4);
        Assert.assertEquals(player.getPosition().getY(), 5);
    }

    @Test
    public void testMoveDown() {
        playercore.moveDown(player);
        Assert.assertEquals(player.getPosition().getX(), 6);
        Assert.assertEquals(player.getPosition().getY(), 5);
    }

    @Test
    public void testMoveLeft() {
        playercore.moveLeft(player);
        Assert.assertEquals(player.getPosition().getX(), 5);
        Assert.assertEquals(player.getPosition().getY(), 4);
    }

    @Test
    public void testMoveRight() {
        playercore.moveRight(player);
        Assert.assertEquals(player.getPosition().getX(), 5);
        Assert.assertEquals(player.getPosition().getY(), 6);
    }

    @Test
    public void testNullPlayer() {
        // 测试传入 null 的情况，不应该抛出异常
        playercore.moveUp(null);
        playercore.moveDown(null);
        playercore.moveLeft(null);
        playercore.moveRight(null);
    }
}