package com.sokoban.core;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.sokoban.core.game.MapMapper;
import com.sokoban.core.game.ObjectType;
import com.sokoban.core.game.Pos;
import com.sokoban.core.game.Things;

/**
 * 测试地图映射是否正常
 * @author ChatGPT
 */
public class MapThingTest {
    @Test
    public void testThingsConstructor() {
        // 创建一个 Things 对象，并检查它的初始状态
        Pos Position = new Pos(2, 3, 0);
        Things thing = new Things(Position, MapMapper.MapNumToType(20));
        
        // 检查 Things 的位置是否正确设置
        Assert.assertEquals(thing.position.getX(), 2);
        Assert.assertEquals(thing.position.getY(), 3);
        Assert.assertEquals(thing.position.getZ(), 0);
        
        // 检查 Things 的 ObjectType 是否正确映射
        Assert.assertEquals(thing.objectType, ObjectType.BOX);
    }

    @Test
    public void testGetObjectTypeMapping() {
        // 检查不同 TypeNumber 映射到的 ObjectType 是否正确
        Assert.assertEquals(MapMapper.MapNumToType(0), ObjectType.AIR);
        Assert.assertEquals(MapMapper.MapNumToType(5), ObjectType.WALL);
        Assert.assertEquals(MapMapper.MapNumToType(10), ObjectType.PLAYER);
        Assert.assertEquals(MapMapper.MapNumToType(20), ObjectType.BOX);
        Assert.assertEquals(MapMapper.MapNumToType(31), ObjectType.PosOfPL);
        Assert.assertEquals(MapMapper.MapNumToType(99), ObjectType.Unknown);  // 一个无效的数字
    }

    @Test
    public void testSetPosition() {
        // 创建一个 Things 对象并改变它的位置，确保位置能正确更新
        Pos newPosition = new Pos(4, 5, 0);
        Things thing = new Things(new Pos(1, 1, 0), MapMapper.MapNumToType(1));
        thing.position = newPosition;
        
        // 检查新设置的位置
        Assert.assertEquals(thing.position.getX(), 4);
        Assert.assertEquals(thing.position.getY(), 5);
        Assert.assertEquals(thing.position.getZ(), 0);
    }

    @Test
    public void testObjectTypeAfterPositionChange() {
        // 仅测试 ObjectType 是否随 TypeNumber 变化
        Things thing = new Things(new Pos(1, 1, 0), MapMapper.MapNumToType(20));
        Assert.assertEquals(thing.objectType, ObjectType.BOX);
        
        // 修改 TypeNumber
        thing = new Things(new Pos(1, 1, 0), MapMapper.MapNumToType(10));
        Assert.assertEquals(thing.objectType, ObjectType.PLAYER);
    }
}
