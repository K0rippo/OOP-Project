package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public class IOManager implements InputProcessor {

    private EnumMap<InputAction, Runnable> actionHandlers;
    private HashMap<Integer, InputAction> keyBindings;
    private Vector2 mousePos;
    private Camera gameCamera; 
    private int scrollAmount = 0;

    public IOManager() {
        this.actionHandlers = new EnumMap<>(InputAction.class);
        this.keyBindings = new HashMap<>();
        this.mousePos = new Vector2();
        
        initializeDefaultBindings();
        Gdx.input.setInputProcessor(this);
    }
    
    private void initializeDefaultBindings() {
        // Ball Controls (WASD)
        bindKey(Input.Keys.W, InputAction.MOVE_UP);
        bindKey(Input.Keys.S, InputAction.MOVE_DOWN);
        bindKey(Input.Keys.A, InputAction.MOVE_LEFT);
        bindKey(Input.Keys.D, InputAction.MOVE_RIGHT);
        
        // NEW: Trampoline Controls (Arrow Keys)
        bindKey(Input.Keys.LEFT, InputAction.TRAMPOLINE_LEFT);
        bindKey(Input.Keys.RIGHT, InputAction.TRAMPOLINE_RIGHT);
        
        bindKey(Input.Keys.SPACE, InputAction.JUMP);
        bindKey(Input.Keys.ESCAPE, InputAction.PAUSE_GAME);
    }

    public void registerAction(InputAction action, Runnable handler) {
        actionHandlers.put(action, handler);
    }

    public void invokeAction(InputAction action) {
        if (actionHandlers.containsKey(action)) {
            actionHandlers.get(action).run();
        }
    }

    public void handleInput() {
        for (Map.Entry<Integer, InputAction> entry : keyBindings.entrySet()) {
            int key = entry.getKey();
            InputAction action = entry.getValue();
            
            boolean triggered = false;

            // Check if key is pressed
            // "MOVE" and "TRAMPOLINE" actions should work while holding the key
            if (action.toString().startsWith("MOVE") || action.toString().startsWith("TRAMPOLINE")) {
                if (Gdx.input.isKeyPressed(key)) triggered = true;
            } else {
                if (Gdx.input.isKeyJustPressed(key)) triggered = true;
            }

            if (triggered) {
                invokeAction(action);
            }
        }
    }

    public void bindKey(int keyCode, InputAction action) {
        keyBindings.put(keyCode, action);
    }

    public Vector2 getMousePosition() {
        mousePos.set(Gdx.input.getX(), Gdx.input.getY());
        return mousePos;
    }

    public Vector2 screenToWorld(float x, float y) {
        if (gameCamera == null) return new Vector2(x, y);
        Vector3 worldPos = gameCamera.unproject(new Vector3(x, y, 0));
        return new Vector2(worldPos.x, worldPos.y);
    }
    
    public void setCamera(Camera camera) {
        this.gameCamera = camera;
    }

    public void dispose() {
        actionHandlers.clear();
        keyBindings.clear();
        Gdx.input.setInputProcessor(null);
    }
    
    public int getScrollAmount() { 
        int i = scrollAmount; 
        scrollAmount = 0; 
        return i; 
    }

    // Required InputProcessor methods
    @Override public boolean scrolled(float amountX, float amountY) { this.scrollAmount = (int)amountY; return true; }
    @Override public boolean keyDown(int keycode) { return false; }
    @Override public boolean keyUp(int keycode) { return false; }
    @Override public boolean keyTyped(char character) { return false; }
    @Override public boolean touchDown(int screenX, int screenY, int pointer, int button) { return false; }
    @Override public boolean touchUp(int screenX, int screenY, int pointer, int button) { return false; }
    @Override public boolean touchCancelled(int screenX, int screenY, int pointer, int button) { return false; }
    @Override public boolean touchDragged(int screenX, int screenY, int pointer) { return false; }
    @Override public boolean mouseMoved(int screenX, int screenY) { return false; }
}