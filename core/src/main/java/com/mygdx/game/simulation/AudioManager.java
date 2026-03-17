package com.mygdx.game.simulation;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

public class AudioManager {
    private Music bgMusic;
    private Sound laserSound;
    private Sound breakSound;

    public void loadAssets() {
        bgMusic = Gdx.audio.newMusic(Gdx.files.internal("Game Music.mp3"));
        bgMusic.setLooping(true);
        
        laserSound = Gdx.audio.newSound(Gdx.files.internal("laser.mp3"));
        breakSound = Gdx.audio.newSound(Gdx.files.internal("break.mp3")); 
    }

    public void playMusic() {
        if (!GameMaster.isMuted() && !bgMusic.isPlaying()) {
            bgMusic.play();
        }
    }

    public void pauseMusic() {
        if (bgMusic.isPlaying()) {
            bgMusic.pause();
        }
    }

    public void playLaserSound() {
        if (!GameMaster.isMuted()) {
            laserSound.play(0.5f);
        }
    }

    public void playBreakSound() {
        if (!GameMaster.isMuted()) {
            breakSound.play(0.8f);
        }
    }

    public void dispose() {
        if (bgMusic != null) bgMusic.dispose();
        if (laserSound != null) laserSound.dispose();
        if (breakSound != null) breakSound.dispose();
    }
}