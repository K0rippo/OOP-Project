package com.mygdx.game;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import java.util.ArrayList;
import java.util.List;

public class EntityManager {
	
    private List<Entity> entities;

    public EntityManager()
    {
        entities = new ArrayList<>();
    }

    public void addEntity(Entity e)
    {
        entities.add(e);
    }

    public void removeEntity(Entity e)
    {
        entities.remove(e);
    }

    public List<Entity> getEntities()
    {
        return entities;
    }

    public void updateAll(float deltaTime)
    {
        for (Entity e : entities)
        {
            if (e.isActive())
            {
                e.update(deltaTime);
            }
        }
    }

    public void renderAll(SpriteBatch batch)
    {
        for (Entity e : entities)
        {
            if (e.isActive())
            {
                e.render(batch);
            }
        }
    }

    public void clear()
    {
        entities.clear();

        //addison
        private List<Entity> entityList = new ArrayList<>(); 
        public void addEntity(Entity e) {
            entityList.add(e); 
        }

        public void movement() {
            for (Entity e : entityList) {
                e.movement(); 
            }
        }

        public void draw(SpriteBatch batch, ShapeRenderer shape) {
            for (Entity e : entityList) {
                e.draw(batch); 
                e.draw(shape);
            }
        }

        public void update() {
            for (Entity e : entityList) {
                e.update();
            }
        }
    }
}