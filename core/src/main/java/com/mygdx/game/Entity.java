package com.mygdx.game;

import com.badlogic.gdx.math.Vector2;

public class Entity implements Movable {

    private final Transform transform;
    private Vector2 velocity;

    public Entity() {
        transform = new Transform();
        velocity = new Vector2(0f, 0f);
    }

    @Override
    public Transform getTransform() {
        return transform;
    }

    @Override
    public Vector2 getVelocity() {
        return velocity;
    }

    @Override
    public void setVelocity(Vector2 velocity) {
        this.velocity = velocity;
    }

    @Override
    public void applyMovement(float deltaTime) {
        Vector2 delta = velocity.cpy().scl(deltaTime);
        transform.getPosition().add(delta);
    }

    public void rotate(float angleDegrees) {
        transform.setRotationDegrees(
            transform.getRotationDegrees() + angleDegrees
        );
    }

    public void lookAt(Vector2 target) {
        Vector2 dir = target.cpy().sub(transform.getPosition());
        float angle = (float) Math.toDegrees(Math.atan2(dir.y, dir.x));
        transform.setRotationDegrees(angle);
    }

    public void moveTowards(Vector2 target) {
        Vector2 dir = target.cpy()
                            .sub(transform.getPosition())
                            .nor();

        float speed = velocity.len();
        velocity = dir.scl(speed);
    }
}

