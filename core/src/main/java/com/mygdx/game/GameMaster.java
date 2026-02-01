package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;


//Elvan's test commit
//Test commit 2
/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class GameMaster extends ApplicationAdapter {
    private SpriteBatch batch;
    
    EntityManager entityManager = new EntityManager();
    private boolean isRunning;

    
    @Override
    public void create()
    {
        batch = new SpriteBatch();
        isRunning = true;
        
        Circle circle = new Circle(1, new Vector2(100, 300), 30f);
        circle.setVelocity(new Vector2(0, -50)); // falling down
        entityManager.addEntity(circle);

        
    }

    @Override
    public void render() {
    	ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);
    	
        batch.begin();
        
        if (isRunning) 
        {
            entityManager.update(1/60f); // assume ~60 FPS for now
            entityManager.render(batch);
        }
        
        batch.end();

    }

    @Override
    public void dispose()
    {
        batch.dispose();
        entityManager.clear();
        isRunning = false;

    }
}
