package com.sokoban.core;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.sokoban.core.game.MapMapper;
import com.sokoban.core.game.Playercore;
import com.sokoban.core.game.Pos;
import com.sokoban.core.game.Things;

/**
 * 测试玩家简单移动是否正确
 * @author ChatGPT
 */
public class PlayerCoreTest {

    private Playercore playercore;
    private Things player;

    @BeforeMethod
    public void setUp() {
        playercore = new Playercore();
        player = new Things(new Pos(5, 5, 0), MapMapper.MapNumToType(10)); // 创建一个玩家，初始位置为 (5, 5, 0)
    }

    @Test
    public void testMoveUp() {
        playercore.moveUp(player);
        Assert.assertEquals(player.position.getX(), 4);
        Assert.assertEquals(player.position.getY(), 5);
    }

    @Test
    public void testMoveDown() {
        playercore.moveDown(player);
        Assert.assertEquals(player.position.getX(), 6);
        Assert.assertEquals(player.position.getY(), 5);
    }

    @Test
    public void testMoveLeft() {
        playercore.moveLeft(player);
        Assert.assertEquals(player.position.getX(), 5);
        Assert.assertEquals(player.position.getY(), 4);
    }

    @Test
    public void testMoveRight() {
        playercore.moveRight(player);
        Assert.assertEquals(player.position.getX(), 5);
        Assert.assertEquals(player.position.getY(), 6);
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
