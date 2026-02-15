package com.mygdx.game.simulation;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;




public class Trampoline extends RectangleEntity {
	
	private final Ball target;
	private float maxSpeed = 340f;
	private float stopDist = 10f;
	
	public Trampoline(int id, Vector2 position, float width, float height, Color color, Ball target) {
		
		super(id, "Trampoline", position, width, height, color);
		this.target = target;
		
	}
	
	@Override
	public void update(float dt) {
		if (target != null && target.isActive()) {
			float targetX = target.getPosition().x;
			float myCenter = getPosition().x + getWidth() / 2f;
			float dx = targetX - myCenter;
			
			if (Math.abs(dx) <= stopDist) {
				getVelocity().x = 0f;
			} else {
				getVelocity().x = Math.signum(dx) * maxSpeed;
			}
		} else {
			getVelocity().x = 0f;
		}
		
		super.update(dt);
		
		float minX = 0f;
		float maxX = Gdx.graphics.getWidth() - getWidth();
		getPosition().x = MathUtils.clamp(getPosition().x, minX, maxX);
		
	}
	
	public void setMaxSpped(float maxSpeed) {
		this.maxSpeed = Math.max(0f, maxSpeed);
	}
	
	public void setStopDist(float stopDist) {
		 this.stopDist = Math.max(0f,  stopDist);
	}
	
}
