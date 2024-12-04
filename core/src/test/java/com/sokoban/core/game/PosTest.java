package com.sokoban.core.game;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.sokoban.core.logic.Pos;

public class PosTest {

    @Test
    public void testDefaultConstructor() {
        Pos pos = new Pos();
        Assert.assertEquals(pos.getX(), 0, "Default X should be 0");
        Assert.assertEquals(pos.getY(), 0, "Default Y should be 0");
        Assert.assertEquals(pos.getZ(), 0, "Default Z should be 0");
    }

    @Test
    public void testConstructorWithThreeParameters() {
        Pos pos = new Pos(1, 2, 3);
        Assert.assertEquals(pos.getX(), 1, "X should be initialized to 1");
        Assert.assertEquals(pos.getY(), 2, "Y should be initialized to 2");
        Assert.assertEquals(pos.getZ(), 3, "Z should be initialized to 3");
    }

    @Test
    public void testConstructorWithTwoParameters() {
        Pos pos = new Pos(4, 5);
        Assert.assertEquals(pos.getX(), 4, "X should be initialized to 4");
        Assert.assertEquals(pos.getY(), 5, "Y should be initialized to 5");
        Assert.assertEquals(pos.getZ(), 0, "Z should default to 0 when not set");
    }

    @Test
    public void testSetters() {
        Pos pos = new Pos();
        pos.setX(10);
        pos.setY(20);
        pos.setZ(30);
        Assert.assertEquals(pos.getX(), 10, "X should be set to 10");
        Assert.assertEquals(pos.getY(), 20, "Y should be set to 20");
        Assert.assertEquals(pos.getZ(), 30, "Z should be set to 30");
    }

    @Test
    public void testEqualsSameObject() {
        Pos pos = new Pos(1, 2, 3);
        Assert.assertTrue(pos.equals(pos), "An object should be equal to itself");
    }

    @Test
    public void testEqualsEqualObjects() {
        Pos pos1 = new Pos(1, 2, 3);
        Pos pos2 = new Pos(1, 2, 3);
        Assert.assertTrue(pos1.equals(pos2), "Objects with the same x, y, z should be equal");
    }

    @Test
    public void testEqualsDifferentObjects() {
        Pos pos1 = new Pos(1, 2, 3);
        Pos pos2 = new Pos(4, 5, 6);
        Assert.assertFalse(pos1.equals(pos2), "Objects with different x, y, z should not be equal");
    }
}
