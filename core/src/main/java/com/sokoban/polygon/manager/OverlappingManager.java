package com.sokoban.polygon.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.sokoban.Main;
import com.sokoban.core.game.Logger;
import com.sokoban.utils.MathUtilsEx;

/**
 * 碰撞检测管理器
 */
public class OverlappingManager {
    // private Main gameMain;
    private int collisionMethod;
    private Actor collisionObject;
    private List<Actor> collisionSecondaryObject;
    private Map<Actor, OverlapStatue> collisionStatue;

    /**
     * 状态说明
     * <br><br>
     * <b>Leave</b> 初始状态，或者已经离开<br><br><br><br>
     * <b>FirstOverlap</b> 首次碰撞<br><br>
     * <b>Overlap</b> 碰撞，但是非首次<br><br>
     * <b>FirstLeave</b> 首次离开<br><br>
     * <b>Disable</b> 碰撞处于不检测状态<br><br>
     */
    public enum OverlapStatue {
        Leave(0), FirstOverlap(1), Overlap(2), FirstLeave(3), Disable(4);
        private int statue;
        OverlapStatue(int statue) {this.statue = statue;}
        public int getStatue() {return statue;}
    }

    /**
     * 碰撞检测器构造
     * @param gameMain 全局句柄
     * @param collisionObject 主碰撞对象
     */
    public OverlappingManager(Main gameMain, Actor collisionObject) {
        // this.gameMain = gameMain;
        init(collisionObject, new ArrayList<>(), 0);
    }
    
    /**
     * 碰撞检测器构造
     * @param gameMain 全局句柄
     * @param collisionObject 主碰撞对象
     * @param collisionMethos 碰撞检测方式，0 = 方框检测，1 = 圆形检测
     */
    public OverlappingManager(Main gameMain, Actor collisionObject, int collisionMethod) {
        // this.gameMain = gameMain;
        init(collisionObject, new ArrayList<>(), collisionMethod);
    }
    
    /**
     * 碰撞检测器构造
     * @param gameMain 全局句柄
     * @param collisionObject 主碰撞对象
     * @param collisionSecondaryObject 副碰撞对象
     * @param collisionMethos 碰撞检测方式，0 = 方框检测，1 = 圆形检测
     */
    public OverlappingManager(Main gameMain, Actor collisionObject, List<Actor> collisionSecondaryObject, int collisionMethod) {
        // this.gameMain = gameMain;
        init(collisionObject, collisionSecondaryObject, collisionMethod);
    }

    private void init(Actor collisionObject, List<Actor> collisionSecondaryObject, int collisionMethod) {
        this.collisionObject = collisionObject;
        this.collisionSecondaryObject = collisionSecondaryObject;
        this.collisionStatue = new HashMap<>();
        this.collisionMethod = collisionMethod;
        for (Actor secondaryObject : collisionSecondaryObject) collisionStatue.put(secondaryObject, OverlapStatue.Leave);
    }

    /**
     * 画面更新后，检查主对象碰撞情况，并只返回第一个碰撞结果
     * <br><br>
     * 如果确信不会发生多个碰撞，可以使用该方法进行状态更新
     * @return 首个碰撞情况发生改变对象，注意，如果没有碰撞将返回 null
     */
    public Actor overlapsCheckSingle() {
        Map<Actor, OverlapStatue> overlapResult = overlapsCheck();
        if (overlapResult.size() == 0) return null;
        return overlapResult.keySet().iterator().next();
    }

    /**
     * 画面更新后，检查主对象碰撞情况
     * @return 碰撞情况发生改变对象及其新状态的 Map
     */
    public Map<Actor, OverlapStatue> overlapsCheck() {
        Map<Actor, OverlapStatue> changedActorState = new HashMap<>();

        // 对每个对象进行碰撞检测
        for (Actor secondaryObject : collisionSecondaryObject) {
            // 检查对象存在情况
            if (secondaryObject == null) {
                Logger.warning("OverlappingManager", String.format("It exists an null object!"));
                continue;
            }

            // 检查碰撞
            boolean collided;
            switch (collisionMethod) {
                case 0:
                    collided = isOverlapRectangle(collisionObject, secondaryObject);
                    break;
                case 1:
                    collided = isOverlapCircle(collisionObject, secondaryObject);
                    break;
                default:
                    Logger.error("OverlappingManager", String.format("%d is not a valid checking method", collisionMethod));
                    return new HashMap<>();
            }

            // 碰撞自动机，获得上一次状态
            boolean changed = false;
            if (collided) {
                // 首次碰撞
                if (getActorOverlapState(secondaryObject) == OverlapStatue.FirstLeave || getActorOverlapState(secondaryObject) == OverlapStatue.Leave) {
                    collisionStatue.replace(secondaryObject, OverlapStatue.FirstOverlap);
                    changed = true;
                }
                // 非首次碰撞
                else if (getActorOverlapState(secondaryObject) == OverlapStatue.FirstOverlap) {
                    collisionStatue.replace(secondaryObject, OverlapStatue.Overlap);
                    changed = true;
                }
            } else {
                // 首次离开
                if (getActorOverlapState(secondaryObject) == OverlapStatue.FirstOverlap || getActorOverlapState(secondaryObject) == OverlapStatue.Overlap) {
                    collisionStatue.replace(secondaryObject, OverlapStatue.FirstLeave);
                    changed = true;
                }
                // 非首次离开
                else if (getActorOverlapState(secondaryObject) == OverlapStatue.FirstLeave) {
                    collisionStatue.replace(secondaryObject, OverlapStatue.Leave);
                    changed = true;
                }
            }

            // 将发生修改的对象及新状态加入 changedActorState
            if (changed) {
                changedActorState.put(secondaryObject, collisionStatue.get(secondaryObject));
                Logger.debug("OverlappingManager", String.format("Colliding statue: %s -> %s", secondaryObject, changedActorState.get(secondaryObject)));
            }
        }

        return changedActorState;
    }

    /**
     * 添加副对象
     * @param object 副对象
     */
    public void addSecondaryObject(Actor object) {
        collisionSecondaryObject.add(object);
        collisionStatue.put(object, OverlapStatue.Leave);
    }

    /**
     * 获取指定对象碰撞情况
     * @param actor
     * @return 碰撞状态
     */
    public OverlapStatue getActorOverlapState(Actor actor) {
        if (!collisionStatue.containsKey(actor)) {
            Logger.error("OverlappingManager", String.format("Object [%s] is not in checker.", actor.toString()));
            return OverlapStatue.Disable;
        }

        return collisionStatue.get(actor);
    }

    /** 禁止所有碰撞检测 */
    public void disableAllCollision() {
        for (Actor secondaryObject : collisionSecondaryObject) {
            collisionStatue.replace(secondaryObject, OverlapStatue.Disable);
        }
    }

    /** 解锁所有碰撞检测 */
    public void enableAllCollision() {
        for (Actor secondaryObject : collisionSecondaryObject) {
            collisionStatue.replace(secondaryObject, OverlapStatue.Leave);
        }
    }

    /**
     * 判定物体圆形是否碰撞
     * <br><br>
     * 物体中心为圆心位置，以最小边为直径
     * @param actor1
     * @param actor2
     * @return 是否碰撞
     */
    public boolean isOverlapCircle(Actor actor1, Actor actor2) {
        float radius1 = Math.min(actor1.getWidth(), actor1.getHeight()) / 2;
        float radius2 = Math.min(actor2.getWidth(), actor2.getHeight()) / 2;
        return isOverlapCircle(actor1, actor2, radius1, radius2);
    }

    /**
     * 判定物体圆形是否碰撞
     * <br><br>
     * 物体中心为圆心位置
     * @param actor1
     * @param actor2
     * @param radius1 actor1 检测半径
     * @param radius2 actor2 检测半径
     * @return 是否碰撞
     */
    public boolean isOverlapCircle(Actor actor1, Actor actor2, float radius1, float radius2) {
        float x1 = actor1.getX() + actor1.getWidth() / 2, y1 = actor1.getY() + actor1.getHeight() / 2;
        float x2 = actor2.getX() + actor2.getWidth() / 2, y2 = actor2.getY() + actor2.getHeight() / 2;

        double distance = MathUtilsEx.distance(x1, y1, x2, y2);
        return distance < radius1 + radius2;
    }

    /**
     * 判定物体矩形是否碰撞
     * @param actor1
     * @param actor2
     * @return 是否碰撞
     */
    public boolean isOverlapRectangle(Actor actor1, Actor actor2) {
        Rectangle rectangle1 = new Rectangle(actor1.getX(), actor1.getY(), actor1.getWidth(), actor1.getHeight());
        Rectangle rectangle2 = new Rectangle(actor2.getX(), actor2.getY(), actor2.getWidth(), actor2.getHeight());
        return rectangle1.overlaps(rectangle2);
    }

    public int getCollisionMethod() {
        return collisionMethod;
    }

    public void setCollisionMethod(int collisionMethod) {
        this.collisionMethod = collisionMethod;
    }

    /**
     * 获取主碰撞检测对象
     * @return 主碰撞检测对象
     */
    public Actor getCollisionObject() {
        return collisionObject;
    }

    /**
     * 设置主碰撞检测对象
     * @param collisionObject 主碰撞检测对象
     */
    public void setCollisionObject(Actor collisionObject) {
        this.collisionObject = collisionObject;
    }

    /**
     * 获取副碰撞检测对象
     * @return 副碰撞检测对象
     */
    public List<Actor> getCollisionSecondaryObject() {
        return collisionSecondaryObject;
    }

    /**
     * 设置副碰撞检测对象
     * @param collisionSecondaryObject 副碰撞检测对象
     */
    public void setCollisionSecondaryObject(List<Actor> collisionSecondaryObject) {
        this.collisionSecondaryObject = collisionSecondaryObject;
    }
    
}
