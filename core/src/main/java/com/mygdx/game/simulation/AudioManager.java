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
        if (!bgMusic.isPlaying()) {
            bgMusic.setVolume(GameMaster.getMusicVolume());
            bgMusic.play();
        }
    }

    public void pauseMusic() {
        if (bgMusic.isPlaying()) {
            bgMusic.pause();
        }
    }
    
    // Live-updates the music volume from the Settings slider
    public void setMusicVolume(float volume) {
        if (bgMusic != null) {
            bgMusic.setVolume(volume);
        }
    }

    public void playLaserSound() {
        if (GameMaster.getSfxVolume() > 0f) {
            laserSound.play(GameMaster.getSfxVolume());
        }
    }

    public void playBreakSound() {
        if (GameMaster.getSfxVolume() > 0f) {
            breakSound.play(GameMaster.getSfxVolume());
        }
    }

    public void dispose() {
        if (bgMusic != null) bgMusic.dispose();
        if (laserSound != null) laserSound.dispose();
        if (breakSound != null) breakSound.dispose();
    }
}