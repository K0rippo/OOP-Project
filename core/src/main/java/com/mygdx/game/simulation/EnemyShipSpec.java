package com.mygdx.game.simulation;

public class EnemyShipSpec {
    private final float offsetX;
    private final float yRatio;
    private final float inactiveMoveSpeed;
    private final float activeMoveSpeed;
    private final float firstShotDelay;
    private final float fireInterval;
    private final float bobAmplitude;

    public EnemyShipSpec(float offsetX,
                         float yRatio,
                         float inactiveMoveSpeed,
                         float activeMoveSpeed,
                         float firstShotDelay,
                         float fireInterval,
                         float bobAmplitude) {
        this.offsetX = offsetX;
        this.yRatio = yRatio;
        this.inactiveMoveSpeed = inactiveMoveSpeed;
        this.activeMoveSpeed = activeMoveSpeed;
        this.firstShotDelay = firstShotDelay;
        this.fireInterval = fireInterval;
        this.bobAmplitude = bobAmplitude;
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

    public float getFirstShotDelay() {
        return firstShotDelay;
    }

    public float getFireInterval() {
        return fireInterval;
    }

    public float getBobAmplitude() {
        return bobAmplitude;
    }
}