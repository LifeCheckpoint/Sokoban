package com.sokoban.manager;

import com.badlogic.gdx.scenes.scene2d.Actor;

public class AccelerationMovingManager {
    private Actor actor;
    private float accelerationX, accelerationY;
    private float maxSpeedX, maxSpeedY;
    private float friction;
    private float velocityX;
    private float velocityY;

    public enum Direction {
        None(0), Right(1), Down(2), Left(3), Up(4);
        private int value;
        Direction(int value) {this.value = value;}
        public int getValue() {return value;}
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

        // 更新 actor 位置
        actor.moveBy(velocityX, velocityY);
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