package com.sokoban;

import com.badlogic.gdx.math.Vector2;
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
    public void testEdgeCases() {
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
}

