package com.mygdx.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class GameScene extends Scene {

    public GameScene(String id) {
        super(id);
        initializeEntities();
    }

    private void initializeEntities() {
        // 1. Ball (Circle)
        Entity ball = new Circle(
            1,
            "Ball",
            new Vector2(200, 200),
            15,
            Color.BROWN
        );
        addEntity(ball); // Add to the Scene list so it gets rendered automatically

        // 2. Trampoline (RectangleEntity)
        Entity trampoline = new RectangleEntity(
            2,
            "Trampoline",
            new Vector2(50, 50),
            150,
            20,
            Color.GREEN
        );
        addEntity(trampoline);

        // 3. Wall (RectangleEntity)
        Entity wall = new RectangleEntity(
            3,
            "Wall",
            new Vector2(600, 50),
            40,
            200,
            Color.BLACK
        );
        addEntity(wall);

        // 4. Coins
        for (int i = 0; i < 2; i++) {
            Entity coin = new Circle(
                100 + i,
                "Coin" + i,
                new Vector2(300 + i * 40, 250),
                5,
                Color.GOLD
            );
            addEntity(coin);
        }
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        
        // This is where you will add Collision Detection later!
        // Example: checkCollision(ball, wall);
    }

    @Override
    public void render(SpriteBatch batch) {
        super.render(batch);
        // The super.render() call already loops through all added entities and draws them.
    }
}