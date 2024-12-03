package com.sokoban.polygon.manager;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.sokoban.Main;
import com.sokoban.core.Logger;
import com.sokoban.polygon.actioninterface.ActionInstanceReset;

/**
 * 管理动画的互斥，同一时间只允许单一动画
 * @author Life_Checkpoint
 */
public class SingleActionInstanceManager {
    // private Main gameMain;
    private Map<Actor, Boolean> actorState;
    
    public SingleActionInstanceManager(Main gameMain) {
        // this.gameMain = gameMain; // 要一个 gameMain 是好文明 (x)
        actorState = new HashMap<>();
    }

    /**
     * 单一动画模式：只允许一个动画存在且不可变
     * @param actor 要执行动画的 actor 对象 
     * @param actions 动画
     */
    public void executeAction(Actor actor, Action actions) {
        // 要求 actor 状态不为动画进行中，否则直接忽略
        if (actorState.containsKey(actor) && !actorState.get(actor)) return;
    
        actorState.put(actor, false);

        // 动画 -> 解锁
        actor.addAction(Actions.sequence(
            actions,
            Actions.run(() -> unlockActionState(actor))
        ));
    }

    /**
     * 单一动画模式：只允许一个动画存在，但是允许 reset 后直接执行新动画
     * @param actor 要执行动画的 actor 对象 
     * @param actions 动画
     * @param resetEvent 重置工作，可为 null
     */
    public void executeAction(Actor actor, Action actions, ActionInstanceReset resetEvent) {
        // TODO BUG
        
        // 重置 -> 动画 -> 解锁
        actor.addAction(Actions.sequence(
            // 重置事件不为空 动画状态存在且动画状态标志真 -> 进行重置
            Actions.run(() -> {if (resetEvent != null && actorState.containsKey(actor) && actorState.get(actor)) resetEvent.reset();}),
            actions,
            Actions.run(() -> unlockActionState(actor))
            ));

        if (actorState.containsKey(actor)) actorState.replace(actor, false);
        actorState.put(actor, false);
    }

    /**
     * 归还动画执行权
     * @param actor actor 对象 
     */
    public void unlockActionState(Actor actor) {
        if (actor != null && actorState.containsKey(actor)) actorState.replace(actor, true);
        else Logger.warning("SingleActionInstanceManager", "Object is null or not recorded.");
    }

    /**
     * 是否在动画中
     * @param actor actor 组件
     * @return 动画进行状态
     */
    public boolean isInAction(Actor actor) {
        if (!actorState.containsKey(actor)) return false;
        return !actorState.get(actor);
    }
}
