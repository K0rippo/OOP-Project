package com.mygdx.game.engine;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import java.util.List;

public interface IGameEngine {
    void update(float deltaTime);
    void render(SpriteBatch batch);
    void dispose();
    
    // Adds an entity to the simulation
    void addEntity(Entity e);

    // Removes an entity from the simulation
    void removeEntity(Entity e);

    // Retrieves entities belonging to a specific layer
    List<Entity> getEntitiesByLayer(int layer);
    
    // Binds an action to a continuously pressed key
    void bindKeyContinuous(int keycode, Runnable action);

    // Binds an action to a key press event
    void bindKeyJustPressed(int keycode, Runnable action);
    
    // Sets the global movement speed multiplier
    void setSpeedMultiplier(float multiplier);
}