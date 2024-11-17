package com.sokoban.manager;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;

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
    private float reactPositionX = 0f, reactPositionY = 0f;
    private Rectangle bound = null;

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
    }

    public void updateActorMove(Direction direction) {
        // 根据方向更新速度
        switch (direction) {
            case Right:  // 向右
                velocityX = Math.min(maxSpeedX, velocityX + accelerationX);
                break;
            case Down:  // 向下
                velocityY = Math.max(-maxSpeedY, velocityY - accelerationY);
                break;
            case Left:  // 向左
                velocityX = Math.max(-maxSpeedX, velocityX - accelerationX);
                break;
            case Up:  // 向上
                velocityY = Math.min(maxSpeedY, velocityY + accelerationY);
                break;
            default:
                break;
        }

        // 在没有按键输入时应用摩擦力
        if (direction == Direction.None) {
            applyFriction();
        }

        // 检查 bound 并更新 actor 位置
        if (bound != null) {
            if (reactPositionX + velocityX < bound.getX()) velocityX = 0;
            if (reactPositionX + velocityX > bound.getX() + bound.getWidth()) velocityX = 0;
            if (reactPositionY + velocityY < bound.getY()) velocityY = 0;
            if (reactPositionY + velocityY > bound.getY() + bound.getHeight()) velocityY = 0;
        }
        
        actor.moveBy(velocityX, velocityY);

        reactPositionX += velocityX;
        reactPositionY += velocityY;
    }

    private void applyFriction() {
        velocityX *= friction;
        velocityY *= friction;

        // 当速度非常小时，将其归零以避免无限小
        if (Math.abs(velocityX) < 0.001f) {
            velocityX = 0;
        }
        if (Math.abs(velocityY) < 0.001f) {
            velocityY = 0;
        }
    }

    /**
     * 设置位移边界框，如果超出边界则禁止该方向位移
     * @param left 左边界
     * @param right 右边界
     * @param down 下边界
     * @param up 上边界
     */
    public void setBound(float left, float right, float down, float up) {
        bound = new Rectangle(left, down, right - left, up - down);
    }

    /**
     * 移出边界框
     */
    public void removeBound() {
        bound = null;
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