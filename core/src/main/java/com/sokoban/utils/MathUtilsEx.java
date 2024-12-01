package com.sokoban.utils;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.sokoban.core.game.Direction;

/**
 * 数学增强类
 * @author Life_Checkpoint
 */
public class MathUtilsEx {
    // 二次贝塞尔曲线
    public static float bezier(float t, float p0, float p1, float p2) {
        float u = 1 - t;  // 1 - t
        return u * u * p0 + 2 * u * t * p1 + t * t * p2;
    }

    public static Vector2 bezier(float t, Vector2 p0, Vector2 p1, Vector2 p2) {
        float x = bezier(t, p0.x, p1.x, p2.x);
        float y = bezier(t, p0.y, p1.y, p2.y);
        return new Vector2(x, y);
    }

    // 线性映射
    public static float linearMap(float t, float originalMinValue, float originalMaxValue, float mapMinValue, float toMapMaxValue) {
        return (t - originalMinValue) / (originalMaxValue - originalMinValue) * (toMapMaxValue - mapMinValue) + mapMinValue;
    }

    // 整数次方，防止小数精度误差
    public static int pow(int base, int a) {
        int result = 1;
        if (a == 0) return 1;
        for (int i = 0; i < a; i++) result *= base;
        return result;
    }

    // 距离计算
    public static float distance(float x1, float y1, float x2, float y2) {
        return new Vector2(x1 - x2, y1 - y2).len();
    }

    /**
     * 测试新位置与旧位置连线跨越了矩形的哪条边
     * @param rectangle 矩形
     * @param originalX 原位置 X
     * @param originalY 原位置 Y
     * @param newX 新位置 X
     * @param newY 新位置 Y
     * @return Direction 常量，表示跨越方向
     */
    public static Direction crossEdgeTest(Rectangle rectangle, float originalX, float originalY, float newX, float newY) {
        // 没有发生变化
        if (
            (rectangle.contains(originalX, originalY) && rectangle.contains(newX, newY)) ||
            (!rectangle.contains(originalX, originalY) && !rectangle.contains(newX, newY))
        ) return Direction.None;

        // 计算矩形的四条边界线
        float left = rectangle.x;
        float right = rectangle.x + rectangle.width;
        float bottom = rectangle.y;
        float top = rectangle.y + rectangle.height;

        // 从内部移动到外部
        if (rectangle.contains(originalX, originalY)) {
            // 检查是否穿过左边界
            if (newX < left && originalX >= left) {
                return Direction.Left;
            }
            // 检查是否穿过右边界
            if (newX > right && originalX <= right) {
                return Direction.Right;
            }
            // 检查是否穿过上边界
            if (newY > top && originalY <= top) {
                return Direction.Up;
            }
            // 检查是否穿过下边界
            if (newY < bottom && originalY >= bottom) {
                return Direction.Down;
            }
        }

        // 从外部移动到内部
        else {
            // 检查是否穿过左边界
            if (newX >= left && originalX < left) {
                return Direction.Left;
            }
            // 检查是否穿过右边界
            if (newX <= right && originalX > right) {
                return Direction.Right;
            }
            // 检查是否穿过上边界
            if (newY <= top && originalY > top) {
                return Direction.Up;
            }
            // 检查是否穿过下边界
            if (newY >= bottom && originalY < bottom) {
                return Direction.Down;
            }
        }

        return Direction.None;
    }
}
