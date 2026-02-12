package com.mygdx.game;

import com.badlogic.gdx.math.Vector2;

public class MovableEntity extends Entity implements Movable{
	
	private Vector2 velocity;
	
	public MovableEntity() {
		super();
		this.velocity= new Vector2(0f,0f);
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
		getTransform().getPosition().add(velocity.cpy().scl(deltaTime));
	}
	
	public void rotate(float angleDegrees) {
		getTransform().setRotationDegrees(getTransform().getRotationDegrees() + angleDegrees);
	}
}
