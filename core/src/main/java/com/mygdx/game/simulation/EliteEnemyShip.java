package com.mygdx.game.simulation;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.engine.Entity;
import com.mygdx.game.engine.RectangleEntity;

public class EliteEnemyShip extends RectangleEntity {

    private final float baseY;
    private final float bobAmplitude = 40f;
    private final float bobSpeed = 3f;
    private float bobTime = 0f;

    private int hitPoints = 5;
    private float fireTimer = 0f;
    private final float fireInterval = 2f; // Fires every 2.5 seconds

    private final Texture texture;
    private final PlayerCharacter playerTarget;
    
    private Runnable onDeathCallback;
    private Runnable onDamageCallback;

    public EliteEnemyShip(int id, Vector2 position, PlayerCharacter playerTarget) {
        super(id, "EliteEnemyShip", position, 60f, 60f, Color.CLEAR);
        this.baseY = position.y;
        this.playerTarget = playerTarget;
        
        // You can use a different texture here if you have one, 
        // otherwise we reuse the enemy ship but tint it red in the render method!
        this.texture = new Texture("enemyspaceship.png"); 
        
        // Moves left faster than normal enemies
        setVelocityX(-150f); 
    } 

    public void setOnDeathCallback(Runnable callback) { this.onDeathCallback = callback; }
    public void setOnDamageCallback(Runnable callback) { this.onDamageCallback = callback; }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        // Bob up and down
        bobTime += deltaTime;
        setY(baseY + MathUtils.sin(bobTime * bobSpeed) * bobAmplitude);

        fireTimer += deltaTime;

        if (getX() + getWidth() < -80f) {
            setActive(false);
        }
    }

    // The GameScene will call this to check if it should spawn a bullet
    public boolean shouldFire() {
        if (fireTimer >= fireInterval && getX() < 1280f) {
            fireTimer = 0f;
            return true;
        }
        return false;
    }

    @Override
    public void onCollision(Entity other) {
        if (!isActive()) return;

        if (other instanceof PlayerBullet && other.isActive()) {
            hitPoints--;
            other.setActive(false); 
            
            if (onDamageCallback != null) {
                onDamageCallback.run();
            }

            if (hitPoints <= 0) {
                setActive(false); 
                
                if (onDeathCallback != null) {
                    onDeathCallback.run();
                    onDeathCallback = null; 
                }
                // -----------------------------------------------------------------
            }
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        if (!isActive()) return;
        
        // Tint this elite ship slightly red to distinguish it
        batch.setColor(1f, 0.5f, 0.5f, 1f); 
        batch.draw(texture, getX(), getY(), getWidth(), getHeight());
        batch.setColor(1f, 1f, 1f, 1f);
    }

    @Override
    public void renderShape(ShapeRenderer shapeRenderer) {}

    public void dispose() {
        texture.dispose();
    }
}