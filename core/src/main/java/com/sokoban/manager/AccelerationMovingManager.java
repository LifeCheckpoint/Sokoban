package com.sokoban.manager;

import java.util.HashMap;
import java.util.Map;

import com.sokoban.core.Logger;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.sokoban.utils.MathUtilsEx;

// TODO 自定义边界框功能

/**
 * 加速度管理器，用于位移的滑行效果
 * @author Life_Checkpoint
 * @author ChatGPT
 */
public class AccelerationMovingManager {
    private Actor actor;
    private float accelerationX, accelerationY;
    private float friction;
    private float maxSpeedX, maxSpeedY;
    private float velocityX, velocityY;
    private boolean noMoving = true;
    private float reactPositionX = 0f, reactPositionY = 0f;
    private Map<Object, Rectangle> taggedBound;

    public enum Direction {
        None("none"), Right("right"), Down("down"), Left("left"), Up("up");
        private String value;
        Direction(String value) {this.value = value;}
        public String getDirection() {return value;}
    }
    
    public AccelerationMovingManager(Actor actor, float acceleration, float maxSpeed, float friction) {
        init(actor, acceleration, acceleration, maxSpeed, maxSpeed, friction);
    }

    public AccelerationMovingManager(Actor actor, float accelerationX, float accelerationY, float maxSpeedX, float maxSpeedY, float friction) {
        init(actor, accelerationX, accelerationY, maxSpeedX, maxSpeedY, friction);
    }

    public void init(Actor actor, float accelerationX, float accelerationY, float maxSpeedX, float maxSpeedY, float friction) {
        this.actor = actor;
        this.accelerationX = accelerationX;
        this.accelerationY = accelerationY;
        this.maxSpeedX = maxSpeedX;
        this.maxSpeedY = maxSpeedY;
        this.friction = friction;
        this.velocityX = 0;
        this.velocityY = 0;
        this.taggedBound = new HashMap<>();
    }

    /**
     * 更新速度，可多次更新
     * @param direction 方向
     */
    public void updateVelocity(Direction direction) {
        // 根据方向更新速度
        switch (direction) {
            case Right:  // 向右
                velocityX = Math.min(maxSpeedX, velocityX + accelerationX);
                noMoving = false;
                break;
            case Down:  // 向下
                velocityY = Math.max(-maxSpeedY, velocityY - accelerationY);
                noMoving = false;
                break;
            case Left:  // 向左
                velocityX = Math.max(-maxSpeedX, velocityX - accelerationX);
                noMoving = false;
                break;
            case Up:  // 向上
                velocityY = Math.min(maxSpeedY, velocityY + accelerationY);
                noMoving = false;
                break;
            default:
                noMoving = true;
                break;
        }
    }

    /**
     * 更新速度后，真正更新位移，仅更新一次
     */
    public void updateActorMove() {

        // 在没有按键输入时应用摩擦力
        if (noMoving) {
            applyFriction();
        }

        // 检查阻挡情况，如有阻挡则设置速度为 0
        taggedBound.values().forEach(bound -> {
            if (bound != null) {
                if (moveBlockTestX(bound)) velocityX = 0;
                if (moveBlockTestY(bound)) velocityY = 0;
            }
        });
        
        // 移动 actor，更新相对位置
        actor.moveBy(velocityX, velocityY);
        reactPositionX += velocityX;
        reactPositionY += velocityY;
    }

    private void applyFriction() {
        velocityX *= friction;
        velocityY *= friction;

        // 当速度非常小时，将其归零以避免无限小
        if (Math.abs(velocityX) < 0.00001f) {
            velocityX = 0;
        }
        if (Math.abs(velocityY) < 0.00001f) {
            velocityY = 0;
        }
    }

    /**
     * 设置位移边界框，如果超出边界则禁止该方向位移
     * <br><br>
     * 边界框应当是<b>相对原点坐标</b>，即相对起始位置的坐标
     * @param tagObject 边界标签，不可重复
     * @param left 左边界
     * @param right 右边界
     * @param down 下边界
     * @param up 上边界
     */
    public void addBound(Object tagObject, float left, float right, float down, float up) {
        if (taggedBound.containsKey(tagObject)) {
            Logger.error("AccelerationMovingManager", String.format("The Tag Object %s is exists.", tagObject.toString()));
            return;
        }
        taggedBound.put(tagObject, new Rectangle(left, down, right - left, up - down));
    }

    /**
     * 取消位移边界框
     * @param tagObject 边界标签
     */
    public void removeBound(Object tagObject) {
        if (!taggedBound.containsKey(tagObject)) {
            Logger.error("AccelerationMovingManager", String.format("The Tag Object %s is not exists.", tagObject.toString()));
            return;
        }
        taggedBound.remove(tagObject);
    }

    /**
     * 重置位移边界框
     * @param tagObject 边界标签
     * @param left 左边界
     * @param right 右边界
     * @param down 下边界
     * @param up 上边界
     */
    public void resetBound(Object tagObject, float left, float right, float down, float up) {
        if (!taggedBound.containsKey(tagObject)) {
            Logger.error("AccelerationMovingManager", String.format("The Tag Object %s is not exists.", tagObject.toString()));
            return;
        }
        taggedBound.replace(tagObject, new Rectangle(left, down, right - left, up - down));
    }

    /**
     * 测试移动是否会被阻挡
     * @param rectangle 阻挡矩形框
     * @param direction 移动方向
     * @return 阻挡方向
     */
    public Direction moveBlockTest(Rectangle rectangle) {
        return MathUtilsEx.crossEdgeTest(rectangle, reactPositionX, reactPositionY, reactPositionX + velocityX, reactPositionY + velocityY);
    }

    /**
     * 测试 X 方向阻挡是否发生
     * @param rectangle
     * @return 阻挡是否发生
     */
    public boolean moveBlockTestX(Rectangle rectangle) {
        Direction collideDirection = moveBlockTest(rectangle);
        return collideDirection == Direction.Left || collideDirection == Direction.Right;
    }

    /**
     * 测试 Y 方向阻挡是否发生
     * @param rectangle
     * @return 阻挡是否发生
     */
    public boolean moveBlockTestY(Rectangle rectangle) {
        Direction collideDirection = moveBlockTest(rectangle);
        return collideDirection == Direction.Up || collideDirection == Direction.Down;
    }

    public Actor getActor() {
        return actor;
    }

    public void setActor(Actor actor) {
        this.actor = actor;
    }

    public float getAccelerationX() {
        return accelerationX;
    }

    public void setAccelerationX(float accelerationX) {
        this.accelerationX = accelerationX;
    }

    public float getAccelerationY() {
        return accelerationY;
    }

    public void setAccelerationY(float accelerationY) {
        this.accelerationY = accelerationY;
    }

    public float getMaxSpeedX() {
        return maxSpeedX;
    }

    public void setMaxSpeedX(float maxSpeedX) {
        this.maxSpeedX = maxSpeedX;
    }

    public float getMaxSpeedY() {
        return maxSpeedY;
    }

    public void setMaxSpeedY(float maxSpeedY) {
        this.maxSpeedY = maxSpeedY;
    }

    public float getFriction() {
        return friction;
    }

    public void setFriction(float friction) {
        this.friction = friction;
    }

    public float getVelocityX() {
        return velocityX;
    }

    public void setVelocityX(float velocityX) {
        this.velocityX = velocityX;
    }

    public float getVelocityY() {
        return velocityY;
    }

    public void setVelocityY(float velocityY) {
        this.velocityY = velocityY;
    }

}