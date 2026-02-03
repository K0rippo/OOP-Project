package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;


//Elvan's test commit
//Test commit 2
/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class GameMaster extends ApplicationAdapter {
	
    private static GameMaster instance;
    
    // Components
    private SpriteBatch batch;
    private SceneManager sceneManager;
    
    EntityManager entityManager = new EntityManager();
    
    private boolean isRunning;
    
    public static GameMaster getInstance() {
        if (instance == null) {
            throw new RuntimeException("GameMaster has not been initialized. Call create() first.");
        }
        return instance;
    }

    @Override
    public void create() {
        instance = this;
        batch = new SpriteBatch();
        sceneManager = new SceneManager();
        isRunning = true;

        // initialize game content
        initialize();
    }

    public void initialize() {
        // Create a Scene
        Scene mainScene = new Scene("MainScene");

        // 2. Setup your Entities
        Circle circle = new Circle(1, new Vector2(100, 300), 30f);
        circle.setVelocity(new Vector2(0, -50));
        
        mainScene.addEntity(circle);

        sceneManager.addScene(mainScene.getId(), mainScene);
        loadScene("MainScene");
    }

    public void loadScene(String sceneId) {
        sceneManager.setActiveScene(sceneId);
    }
    
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
            entityManager.updateAll(1/60f); // assume ~60 FPS for now
            entityManager.renderAll(batch);
        }
        
        batch.end();

    }
    
    @Override
    public void pause() {
        isRunning = false;
    }

    @Override
    public void resume() {
        isRunning = true;
    }

    @Override
    public void dispose()
    {
        batch.dispose();
        entityManager.clear();
        isRunning = false;

    }
    
    public SceneManager getSceneManager() {
        return sceneManager;
    }
    
    // If you need to access other managers globally
    public SpriteBatch getBatch() {
        return batch;
    }
}
