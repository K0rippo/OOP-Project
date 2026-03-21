package com.mygdx.game.simulation;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.engine.Entity;
import com.mygdx.game.engine.IGameEngine;

class CombatDirector {
    private final IGameEngine engine;
    private final AudioManager audioManager;
    private final float shootInterval;
    private final int playerLayer;
    private final int gateLayer;
    private final int enemyLayer;
    private final int enemyBulletLayer;

    CombatDirector(IGameEngine engine,
                   AudioManager audioManager,
                   float shootInterval,
                   int playerLayer,
                   int gateLayer,
                   int enemyLayer,
                   int enemyBulletLayer) {
        this.engine = engine;
        this.audioManager = audioManager;
        this.shootInterval = shootInterval;
        this.playerLayer = playerLayer;
        this.gateLayer = gateLayer;
        this.enemyLayer = enemyLayer;
        this.enemyBulletLayer = enemyBulletLayer;
    }

    CombatState update(PlayerCharacter player,
                       float shootCooldown,
                       float deltaTime,
                       int nextPlayerBulletId,
                       int nextEnemyBulletId) {
        float updatedCooldown = shootCooldown;
        int updatedPlayerBulletId = nextPlayerBulletId;
        int updatedEnemyBulletId = nextEnemyBulletId;

        //spawn player bullet only when requested and cooldown is ready
        if (player != null && player.isShootRequested() && updatedCooldown <= 0f) {
            PlayerBullet bullet = new PlayerBullet(
                    updatedPlayerBulletId++,
                    new Vector2(player.getX() + 22f, player.getY() - 3f)
            );
            bullet.setCollisionLayer(playerLayer);
            bullet.setCollisionMask(gateLayer | enemyLayer);

            engine.addEntity(bullet);
            player.consumeShoot();
            updatedCooldown = shootInterval;

            if (audioManager != null) {
                audioManager.playLaserSound();
            }
        }

        //let active enemy ships fire based on their internal timers
        for (Entity entity : engine.getEntitiesByLayer(enemyLayer)) {
            if (!(entity instanceof EnemyShip)) continue;

            EnemyShip ship = (EnemyShip) entity;
            if (!ship.isActive()) continue;

            if (ship.shouldFire()) {
                Array<EnemyBullet> burst = ship.fire(
                        updatedEnemyBulletId,
                        enemyBulletLayer,
                        playerLayer
                );

                updatedEnemyBulletId += burst.size;

                for (EnemyBullet bullet : burst) {
                    engine.addEntity(bullet);
                }
            }
        }

        if (updatedCooldown > 0f) {
            updatedCooldown -= deltaTime;
        }

        return new CombatState(updatedCooldown, updatedPlayerBulletId, updatedEnemyBulletId);
    }

    static class CombatState {
        private final float shootCooldown;
        private final int nextPlayerBulletId;
        private final int nextEnemyBulletId;

        CombatState(float shootCooldown, int nextPlayerBulletId, int nextEnemyBulletId) {
            this.shootCooldown = shootCooldown;
            this.nextPlayerBulletId = nextPlayerBulletId;
            this.nextEnemyBulletId = nextEnemyBulletId;
        }

        float getShootCooldown() {
            return shootCooldown;
        }

        int getNextPlayerBulletId() {
            return nextPlayerBulletId;
        }

        int getNextEnemyBulletId() {
            return nextEnemyBulletId;
        }
    }
}
