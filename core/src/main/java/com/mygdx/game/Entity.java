package com.mygdx.game;

public class Entity {

    private final Transform transform;

    public Entity() {
        this.transform = new Transform();
    }

    public Transform getTransform() {
    	return transform;
    }

}

