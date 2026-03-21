package com.mygdx.game.engine;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import java.util.List;

public interface IGameEngine {
    void update(float deltaTime);
    void render(SpriteBatch batch);
    void dispose();

    void addEntity(Entity e);

    void removeEntity(Entity e);

    List<Entity> getEntitiesByLayer(int layer);

    void bindKeyContinuous(int keycode, Runnable action);

    void bindKeyJustPressed(int keycode, Runnable action);

    void setSpeedMultiplier(float multiplier);
}