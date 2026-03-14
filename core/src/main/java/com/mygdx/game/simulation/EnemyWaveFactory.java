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
            EnemyShip ship = new EnemyShip(
                    nextId++,
                    new Vector2(sectionStartX + spec.getOffsetX(), worldHeight * spec.getYRatio()),
                    spec.getInactiveMoveSpeed(),
                    spec.getActiveMoveSpeed(),
                    spec.getFireInterval(),
                    spec.getBobAmplitude(),
                    spec.getBulletSpeed(),
                    spec.getBulletsPerShot(),
                    spec.getAngleStepDegrees()
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

        // Same rough behavior you already have now
        specs.add(new EnemyShipSpec(
                -120f,              // offsetX
                0.70f,              // yRatio
                scrollSpeed,        // inactiveMoveSpeed
                scrollSpeed + 35f,  // activeMoveSpeed
                0.30f,              // fireInterval
                22f,                // bobAmplitude
                175f,               // bulletSpeed
                1,                  // bulletsPerShot
                20f                 // angleStepDegrees
        ));

        specs.add(new EnemyShipSpec(
                40f,
                0.30f,
                scrollSpeed,
                scrollSpeed + 28f,
                0.34f,
                18f,
                170f,
                1,
                -20f
        ));

        return specs;
    }
}