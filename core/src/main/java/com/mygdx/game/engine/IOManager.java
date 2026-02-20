package com.mygdx.game.engine;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import java.util.HashMap;
import java.util.Map;

public class IOManager implements InputProcessor {

    // Map KeyCode (Integer) -> Function to run (Runnable)
    private Map<Integer, Runnable> justPressedBindings; // For things like Jump (press once)
    private Map<Integer, Runnable> continuousBindings;  // For things like Move (hold down)

    public IOManager() {
        this.justPressedBindings = new HashMap<>();
        this.continuousBindings = new HashMap<>();
        
    }

    // Triggers only once when the key is first pressed. (Single Action)
    public void bindKeyJustPressed(int keyCode, Runnable command) {
        justPressedBindings.put(keyCode, command);
    }

    // Triggers every frame while the key is held down. (Moving)
    public void bindKeyContinuous(int keyCode, Runnable command) {
        continuousBindings.put(keyCode, command);
    }

    public void handleInput() {

        if (!justPressedBindings.isEmpty()) {
            for (Map.Entry<Integer, Runnable> entry : justPressedBindings.entrySet()) {
                if (Gdx.input.isKeyJustPressed(entry.getKey())) {
                    entry.getValue().run();
                }
            }
        }
        
        if (!continuousBindings.isEmpty()) {
            for (Map.Entry<Integer, Runnable> entry : continuousBindings.entrySet()) {
                if (Gdx.input.isKeyPressed(entry.getKey())) {
                    entry.getValue().run();
                }
            }
        }
    }

    // --- InputProcessor Stubs (Required by LibGDX interface) ---
    // We handle input via polling in handleInput(), so these can return false/empty.
    @Override public boolean keyDown(int keycode) { return false; }
    @Override public boolean keyUp(int keycode) { return false; }
    @Override public boolean keyTyped(char character) { return false; }
    @Override public boolean touchDown(int screenX, int screenY, int pointer, int button) { return false; }
    @Override public boolean touchUp(int screenX, int screenY, int pointer, int button) { return false; }
    @Override public boolean touchCancelled(int screenX, int screenY, int pointer, int button) { return false; }
    @Override public boolean touchDragged(int screenX, int screenY, int pointer) { return false; }
    @Override public boolean mouseMoved(int screenX, int screenY) { return false; }
    @Override public boolean scrolled(float amountX, float amountY) { return false; }
}