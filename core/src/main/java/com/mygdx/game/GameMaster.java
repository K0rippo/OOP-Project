package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.utils.ScreenUtils;

public class GameMaster extends ApplicationAdapter {
    
    private IOManager ioManager;

    @Override
    public void create() {
        ioManager = new IOManager();
    }

    @Override
    public void render() {
        ScreenUtils.clear(0, 0, 0, 1);
        
        if (ioManager != null) {
            ioManager.handleInput();
        }
    }
    
    @Override
    public void dispose() {
        if (ioManager != null) {
            ioManager.dispose();
        }
    }
}
