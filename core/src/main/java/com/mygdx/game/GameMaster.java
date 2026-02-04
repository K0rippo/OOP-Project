package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;


//Elvan's test commit
//Test commit 2
//kendrickk tessstingg 31/1/26
/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class GameMaster extends ApplicationAdapter {
	
    private static GameMaster instance;
    
    // Components
    private SpriteBatch batch;
    private SceneManager sceneManager;
    private IOManager ioManager;
    private EntityManager em;
    
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
        private IOManager ioManager;
        
        Circle circle = new Circle(1, new Vector2(100, 300), 30f);
        circle.setVelocity(new Vector2(0, -50)); // falling down
        entityManager.addEntity(circle);

        //addison
        em = new EntityManager();
        // Create your objects and add them to the manager
        em.addEntity(new Circle(100, 100, 50, Color.RED, 200));
        em.addEntity(new TextureObject(bucketTex, 300, 20, 300, false));
        // Add raindrops, triangles, etc.

        
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
        
        //addison
        // 1. Update state
        em.movement(); 
        em.update(); // Prints info to console [cite: 34]

        // 2. Draw state
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        shape.begin(ShapeType.Filled);
        
        em.draw(batch, shape); // Manager handles all drawing [cite: 49, 50]
        
        shape.end();
        batch.end();

    }
    
    @Override
    public void pause() {
        isRunning = false;
        ScreenUtils.clear(0, 0, 0, 1);
        
        if (ioManager != null) {
            ioManager.handleInput();
        }
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
    public void dispose() {
        if (ioManager != null) {
            ioManager.dispose();
        }
    }
    }
}