package com.mygdx.game.simulation;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class CircleBulletPattern implements BulletPattern {

    private final int bulletCount;
    private final float bulletSpeed;
    private final float bulletSpread;

    public CircleBulletPattern(int bulletCount, float bulletSpeed, float bulletSpread) {
        this.bulletCount = bulletCount;
        this.bulletSpeed = bulletSpeed;
        this.bulletSpread = bulletSpread;
    }

    @Override
    public Array<EnemyBullet> fire(EnemyShip ship, int firstBulletId, int bulletLayer, int bulletMask) {
        Array<EnemyBullet> bullets = new Array<EnemyBullet>();

        float spawnX = ship.getPosition().x + ship.getWidth() / 2f - 6f;
        float spawnY = ship.getPosition().y + ship.getHeight() / 2f - 6f;

        float angleStep = 360f / bulletCount;

        for (int i = 0; i < bulletCount; i++) {
            float angleDeg = i * angleStep;
            float angleRad = angleDeg * MathUtils.degreesToRadians;

            float vx = -bulletSpeed + MathUtils.cos(angleRad) * bulletSpread;
            float vy = MathUtils.sin(angleRad) * bulletSpread;

            EnemyBullet bullet = new EnemyBullet(
                    firstBulletId + i,
                    new Vector2(spawnX, spawnY),
                    vx,
                    vy
            );
            bullet.setCollisionLayer(bulletLayer);
            bullet.setCollisionMask(bulletMask);
            bullets.add(bullet);
        }

        return bullets;
    }
}