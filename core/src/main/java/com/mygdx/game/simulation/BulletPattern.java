package com.mygdx.game.simulation;

import com.badlogic.gdx.utils.Array;

public interface BulletPattern {
    Array<EnemyBullet> fire(EnemyShip ship, int firstBulletId, int bulletLayer, int bulletMask);
}