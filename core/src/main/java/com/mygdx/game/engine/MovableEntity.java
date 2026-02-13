package com.mygdx.game.engine;

import com.badlogic.gdx.math.Vector2;

//import com.mygdx.game.engine.Entity;

public abstract class MovableEntity extends Entity implements iMovable{
	
	//private Vector2 velocity;
	private Vector2 velocity = new Vector2(0f, 0f);
	
	public MovableEntity(int id, String name, Vector2 position) {
		
		super(id,name,position);
		
	}
	
	@Override
	public void update(float deltaTime) {
	    applyMovement(deltaTime);
	}

	
	@Override
	public Vector2 getVelocity() {
		return velocity;
	}
	
	@Override
	public void setVelocity(Vector2 velocity) {
		this.velocity = velocity;
	}
	
	@Override
	public void applyMovement(float deltaTime) {
		getOrientate().getPosition().add(velocity.cpy().scl(deltaTime));
	}
	
	public void rotate(float angleDegrees) {
		getOrientate().setRotationDegrees(getOrientate().getRotationDegrees() + angleDegrees);
	}
}
