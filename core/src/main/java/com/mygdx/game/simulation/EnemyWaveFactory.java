package com.mygdx.game.simulation;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class EnemyWaveFactory {

    public EnemyWave createDefaultWave(int questionIndex,
                                       float sectionStartX,
                                       float worldHeight,
                                       float scrollSpeed,
                                       int firstShipId) {
        EnemyWave wave = new EnemyWave(questionIndex);

        Array<EnemyShipSpec> specs = buildDefaultSpecs(scrollSpeed);

        int nextId = firstShipId;
        for (EnemyShipSpec spec : specs) {

            CircleBulletPattern pattern = new CircleBulletPattern(
                    15,    // bullet count
                    90f,  // bullet speed
                    125f    // bullet spread
            );

            EnemyShip ship = new EnemyShip(
                    nextId++,
                    new Vector2(sectionStartX + spec.getOffsetX(), worldHeight * spec.getYRatio()),
                    spec.getInactiveMoveSpeed(),
                    spec.getActiveMoveSpeed(),
                    spec.getFirstShotDelay(),
                    spec.getFireInterval(),
                    spec.getBobAmplitude(),
                    pattern
            );

            wave.addShip(ship);
        }

        if (questionIndex == 0) {
            wave.activate();
        }

        return wave;
    }

    private Array<EnemyShipSpec> buildDefaultSpecs(float scrollSpeed) {
        Array<EnemyShipSpec> specs = new Array<EnemyShipSpec>();

        specs.add(new EnemyShipSpec(
                -120f,
                0.70f,
                scrollSpeed,
                scrollSpeed + 35f,
                0.25f,
                2.5f,
                22f
        ));

        specs.add(new EnemyShipSpec(
                40f,
                0.30f,
                scrollSpeed,
                scrollSpeed + 28f,
                0.25f,
                2.5f,
                18f
        ));

        return specs;
    }
}