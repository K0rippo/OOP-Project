package com.mygdx.game.engine;

public enum InputAction {
    // Primary Movement (WASD / Ball)
    MOVE_LEFT,
    MOVE_RIGHT,
    MOVE_UP,
    MOVE_DOWN,
    JUMP,
    
    // Secondary Movement (Arrows / Trampoline)
    SECONDARY_LEFT,
    SECONDARY_RIGHT,

    // System Actions
    SPAWN_ENTITY,
    SELECT_ITEM,
    START_GAME,
    PAUSE_GAME,
    QUIT_GAME
}