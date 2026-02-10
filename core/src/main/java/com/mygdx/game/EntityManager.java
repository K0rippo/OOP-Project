package com.mygdx.game;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import java.util.ArrayList;
import java.util.Collections; // Added for safety
import java.util.List;

public class EntityManager {
    
    private final List<Entity> entities;

    public EntityManager()
    {
        this.entities = new ArrayList<>();
    }

    public void addEntity(Entity e)
    {
        entities.add(e);
    }

    public void removeEntity(Entity e)
    {
        entities.remove(e);
    }

    //getter to not violate encap
    public List<Entity> getEntities()
    {
        return Collections.unmodifiableList(entities);
    }

    public void updateAll(float deltaTime) {
        for (int i = 0; i < entities.size(); i++)
        {    
            Entity e = entities.get(i);
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
    }
}