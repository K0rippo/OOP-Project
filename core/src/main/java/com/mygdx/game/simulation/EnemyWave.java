package com.mygdx.game.simulation;

import com.badlogic.gdx.utils.Array;

public class EnemyWave {
    private final int triggerQuestionIndex;
    private final Array<EnemyShip> ships;
    private boolean activated;

    public EnemyWave(int triggerQuestionIndex) {
        this.triggerQuestionIndex = triggerQuestionIndex;
        this.ships = new Array<EnemyShip>();
        this.activated = false;
    }

    public void addShip(EnemyShip ship) {
        ships.add(ship);
    }

    public Array<EnemyShip> getShips() {
        return ships;
    }

    public int getTriggerQuestionIndex() {
        return triggerQuestionIndex;
    }

    public boolean isActivated() {
        return activated;
    }

    public void activate() {
        if (activated) return;
        activated = true;

        for (EnemyShip ship : ships) {
            if (ship.isActive()) {
                ship.setWaveActive(true);
            }
        }
    }
}
