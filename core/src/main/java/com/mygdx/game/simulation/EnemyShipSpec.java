package com.mygdx.game.simulation;

public class EnemyShipSpec {
    private final float offsetX;
    private final float yRatio;
    private final float inactiveMoveSpeed;
    private final float activeMoveSpeed;
    private final float fireInterval;
    private final float bobAmplitude;
    private final float bulletSpeed;
    private final int bulletsPerShot;
    private final float angleStepDegrees;

    public EnemyShipSpec(float offsetX,
                         float yRatio,
                         float inactiveMoveSpeed,
                         float activeMoveSpeed,
                         float fireInterval,
                         float bobAmplitude,
                         float bulletSpeed,
                         int bulletsPerShot,
                         float angleStepDegrees) {
        this.offsetX = offsetX;
        this.yRatio = yRatio;
        this.inactiveMoveSpeed = inactiveMoveSpeed;
        this.activeMoveSpeed = activeMoveSpeed;
        this.fireInterval = fireInterval;
        this.bobAmplitude = bobAmplitude;
        this.bulletSpeed = bulletSpeed;
        this.bulletsPerShot = bulletsPerShot;
        this.angleStepDegrees = angleStepDegrees;
    }

    public float getOffsetX() {
        return offsetX;
    }

    public float getYRatio() {
        return yRatio;
    }

    public float getInactiveMoveSpeed() {
        return inactiveMoveSpeed;
    }

    public float getActiveMoveSpeed() {
        return activeMoveSpeed;
    }

    public float getFireInterval() {
        return fireInterval;
    }

    public float getBobAmplitude() {
        return bobAmplitude;
    }

    public float getBulletSpeed() {
        return bulletSpeed;
    }

    public int getBulletsPerShot() {
        return bulletsPerShot;
    }

    public float getAngleStepDegrees() {
        return angleStepDegrees;
    }
}