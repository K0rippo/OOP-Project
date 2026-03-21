package com.mygdx.game.simulation;

import com.badlogic.gdx.utils.Array;

public interface BulletPattern {
    //returns one burst of bullets for the given ship state
    Array<EnemyBullet> fire(EnemyShip ship, int firstBulletId, int bulletLayer, int bulletMask);
}