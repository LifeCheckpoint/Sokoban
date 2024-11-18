package com.sokoban.utils;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.sokoban.manager.AccelerationMovingManager.Direction;

import org.testng.Assert;
import org.testng.annotations.Test;

public class MathUtilsExTest {

    @Test
    public void testBezierFloat() {
        // 测试 bezier(t, float p0, float p1, float p2)
        
        // 测试用例 1: t = 0 (开始点)
        float result = MathUtilsEx.bezier(0f, 0f, 1f, 2f);
        Assert.assertEquals(result, 0f, "The result for t=0 should be 0");
        
        // 测试用例 2: t = 1 (结束点)
        result = MathUtilsEx.bezier(1f, 0f, 1f, 2f);
        Assert.assertEquals(result, 2f, "The result for t=1 should be 2");
        
        // 测试用例 3: t = 0.5 (中间点)
        result = MathUtilsEx.bezier(0.5f, 0f, 1f, 2f);
        Assert.assertEquals(result, 1f, "The result for t=0.5 should be 1");
        
        // 测试用例 4: t = 0.25 (贝塞尔曲线的另一点)
        result = MathUtilsEx.bezier(0.25f, 0f, 1f, 2f);
        Assert.assertEquals(result, 0.5f, "The result for t=0.25 should be 0.875");
    }

    @Test
    public void testBezierVector2() {
        // 测试 bezier(t, Vector2 p0, Vector2 p1, Vector2 p2)
        
        // 测试用例 1: t = 0 (开始点)
        Vector2 result = MathUtilsEx.bezier(0f, new Vector2(0f, 0f), new Vector2(1f, 1f), new Vector2(2f, 2f));
        Assert.assertEquals(result, new Vector2(0f, 0f), "The result for t=0 should be (0, 0)");
        
        // 测试用例 2: t = 1 (结束点)
        result = MathUtilsEx.bezier(1f, new Vector2(0f, 0f), new Vector2(1f, 1f), new Vector2(2f, 2f));
        Assert.assertEquals(result, new Vector2(2f, 2f), "The result for t=1 should be (2, 2)");
        
        // 测试用例 3: t = 0.5 (中间点)
        result = MathUtilsEx.bezier(0.5f, new Vector2(0f, 0f), new Vector2(1f, 1f), new Vector2(2f, 2f));
        Assert.assertEquals(result, new Vector2(1f, 1f), "The result for t=0.5 should be (1, 1)");

        // 测试用例 4: t = 0.25 (贝塞尔曲线的另一点)
        result = MathUtilsEx.bezier(0.25f, new Vector2(0f, 0f), new Vector2(1f, 1f), new Vector2(2f, 2f));
        Assert.assertEquals(result, new Vector2(0.5f, 0.5f), "The result for t=0.25 should be (0.875, 0.875)");
    }

    @Test
    public void testBezierEdgeCases() {
        // 测试边界情况：t = 0 和 t = 1
        
        // t = 0
        Vector2 result = MathUtilsEx.bezier(0f, new Vector2(0f, 0f), new Vector2(1f, 1f), new Vector2(2f, 2f));
        Assert.assertEquals(result, new Vector2(0f, 0f), "The result for t=0 should be (0, 0)");
        
        // t = 1
        result = MathUtilsEx.bezier(1f, new Vector2(0f, 0f), new Vector2(1f, 1f), new Vector2(2f, 2f));
        Assert.assertEquals(result, new Vector2(2f, 2f), "The result for t=1 should be (2, 2)");

        // t = 0.5 (检查中间点)
        result = MathUtilsEx.bezier(0.5f, new Vector2(0f, 0f), new Vector2(2f, 0f), new Vector2(4f, 0f));
        Assert.assertEquals(result, new Vector2(2f, 0f), "The result for t=0.5 should be (2, 0)");
    }

    // 线性映射测试
    @Test void testLinear() {
        float result;
        result = MathUtilsEx.linearMap(2f, 1f, 3f, 4f, 5f);
        Assert.assertEquals(result, 4.5, "The result for t=2 should be 4.5");

        result = MathUtilsEx.linearMap(1f, 1f, 3f, 4f, 5f);
        Assert.assertEquals(result, 4, "The result for t=1 should be 4");

        result = MathUtilsEx.linearMap(3f, 1f, 3f, 4f, 5f);
        Assert.assertEquals(result, 5, "The result for t=3 should be 5");
    }

    // 距离测试
    @Test void testDistance() {
        float result;
        result = MathUtilsEx.distance(114f, 514f, 191f, 981f);
        Assert.assertTrue(Math.abs(result - 473.3053982f) < 0.1f, "The distance between (114, 514) and (191, 981) should be 473.3053982f");

        result = MathUtilsEx.distance(999f, 888f, 999f, 888f);
        Assert.assertTrue(Math.abs(result - 0f) < 0.0001f, "The distance between (999, 888) and (999, 888) should be 0f");
    }

    // 矩形测试
    @Test
    void testCrossEdge() {
        Rectangle rectangle = new Rectangle(100, 100, 200, 200); // 创建一个矩形，左下角(100,100)，宽高200
        Direction result;

        // 测试穿越左边界（从内到外）
        result = MathUtilsEx.crossEdgeTest(rectangle, 150, 150, 50, 150);
        Assert.assertEquals(result, Direction.Left, "Moving from (150, 150) to (50, 150) should cross the left edge");

        // 测试穿越右边界（从内到外）
        result = MathUtilsEx.crossEdgeTest(rectangle, 150, 150, 350, 150);
        Assert.assertEquals(result, Direction.Right, "Moving from (150, 150) to (350, 150) should cross the right edge");

        // 测试穿越上边界（从内到外）
        result = MathUtilsEx.crossEdgeTest(rectangle, 150, 150, 150, 350);
        Assert.assertEquals(result, Direction.Up, "Moving from (150, 150) to (150, 350) should cross the top edge");

        // 测试穿越下边界（从内到外）
        result = MathUtilsEx.crossEdgeTest(rectangle, 150, 150, 150, 50);
        Assert.assertEquals(result, Direction.Down, "Moving from (150, 150) to (150, 50) should cross the bottom edge");

        // 测试穿越左边界（从外到内）
        result = MathUtilsEx.crossEdgeTest(rectangle, 50, 150, 150, 150);
        Assert.assertEquals(result, Direction.Left, "Moving from (50, 150) to (150, 150) should cross the left edge");

        // 测试穿越右边界（从外到内）
        result = MathUtilsEx.crossEdgeTest(rectangle, 350, 150, 150, 150);
        Assert.assertEquals(result, Direction.Right, "Moving from (350, 150) to (150, 150) should cross the right edge");

        // 测试穿越上边界（从外到内）
        result = MathUtilsEx.crossEdgeTest(rectangle, 150, 350, 150, 150);
        Assert.assertEquals(result, Direction.Up, "Moving from (150, 350) to (150, 150) should cross the top edge");

        // 测试穿越下边界（从外到内）
        result = MathUtilsEx.crossEdgeTest(rectangle, 150, 50, 150, 150);
        Assert.assertEquals(result, Direction.Down, "Moving from (150, 50) to (150, 150) should cross the bottom edge");

        // 测试内部移动（不穿越边界）
        result = MathUtilsEx.crossEdgeTest(rectangle, 150, 150, 200, 200);
        Assert.assertEquals(result, Direction.None, "Moving inside rectangle should return None");

        // 测试外部移动（不穿越边界）
        result = MathUtilsEx.crossEdgeTest(rectangle, 50, 50, 75, 75);
        Assert.assertEquals(result, Direction.None, "Moving outside rectangle should return None");

        // 测试从同一点移动到同一点
        result = MathUtilsEx.crossEdgeTest(rectangle, 150, 150, 150, 150);
        Assert.assertEquals(result, Direction.None, "Moving from a point to the same point should return None");
    }
}

