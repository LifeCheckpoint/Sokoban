package com.sokoban;

import com.badlogic.gdx.math.Vector2;

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
}
