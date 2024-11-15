package com.sokoban.polygon.actioninterface;

/**
 * 观察者模式
 * <br><br>
 * 实现该接口后，当新实例创建，将会通知所有旧实例
 */
public interface Observer {
    void onNewInstanceCreated();
}
