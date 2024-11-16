package com.sokoban.polygon.actioninterface;

/**
 * 用于实现动画单一实例的重置工作
 */
@FunctionalInterface
public interface ActionInstanceReset {
    public void reset();
}
