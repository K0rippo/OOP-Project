package com.mygdx.game.simulation;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

public class AudioManager {
    private Music bgMusic;
    private Music menuMusic;
    
    private Sound laserSound;
    private Sound breakSound;
    private Sound damageSound;
    private Sound clickSound;
    private Sound correctSound;

    public void loadAssets() {
        try {
            bgMusic = Gdx.audio.newMusic(Gdx.files.internal("Game Music.mp3"));
            bgMusic.setLooping(true);
        } catch (Exception e) {
            System.err.println("Warning: Game Music.mp3 missing.");
        }

        try {
            menuMusic = Gdx.audio.newMusic(Gdx.files.internal("menumusic.mp3"));
            menuMusic.setLooping(true);
        } catch (Exception e) {
            System.err.println("Warning: menumusic.mp3 missing.");
        }

        try { laserSound = Gdx.audio.newSound(Gdx.files.internal("laser.mp3")); } catch (Exception e) {}
        try { breakSound = Gdx.audio.newSound(Gdx.files.internal("break.mp3")); } catch (Exception e) {}
        try { damageSound = Gdx.audio.newSound(Gdx.files.internal("damage.mp3")); } catch (Exception e) {}
        try { clickSound = Gdx.audio.newSound(Gdx.files.internal("click.mp3")); } catch (Exception e) {}
        try { correctSound = Gdx.audio.newSound(Gdx.files.internal("correct.mp3")); } catch (Exception e) {}
    }

    // --- MUSIC CONTROLS ---
    public void playGameMusic() {
        if (menuMusic != null && menuMusic.isPlaying()) {
            menuMusic.pause();
        }
        if (bgMusic != null && !bgMusic.isPlaying()) {
            bgMusic.setVolume(GameMaster.getMusicVolume());
            bgMusic.play();
        }
    }

    public void pauseGameMusic() {
        if (bgMusic != null && bgMusic.isPlaying()) {
            bgMusic.pause();
        }
    }

    public void playMenuMusic() {
        if (bgMusic != null && bgMusic.isPlaying()) {
            bgMusic.pause();
        }
        if (menuMusic != null && !menuMusic.isPlaying()) {
            menuMusic.setVolume(GameMaster.getMusicVolume());
            menuMusic.play();
        }
    }

    public void setMusicVolume(float volume) {
        if (bgMusic != null) bgMusic.setVolume(volume);
        if (menuMusic != null) menuMusic.setVolume(volume);
    }

    public void playLaserSound() {
        if (GameMaster.getSfxVolume() > 0f && laserSound != null) laserSound.play(GameMaster.getSfxVolume());
    }

    public void playBreakSound() {
        if (GameMaster.getSfxVolume() > 0f && breakSound != null) breakSound.play(GameMaster.getSfxVolume());
    }

    public void playShipDamageSound() {
        if (GameMaster.getSfxVolume() > 0f && damageSound != null) damageSound.play(GameMaster.getSfxVolume()); 
    }

    public void playCorrectGateSound() {
        if (GameMaster.getSfxVolume() > 0f && correctSound != null) correctSound.play(GameMaster.getSfxVolume()); 
    }

    public void playMenuButtonSound() {
        if (GameMaster.getSfxVolume() > 0f && clickSound != null) clickSound.play(GameMaster.getSfxVolume()); 
    }

    public void dispose() {
        if (bgMusic != null) bgMusic.dispose();
        if (menuMusic != null) menuMusic.dispose();
        if (laserSound != null) laserSound.dispose();
        if (breakSound != null) breakSound.dispose();
        if (damageSound != null) damageSound.dispose();
        if (clickSound != null) clickSound.dispose();
        if (correctSound != null) correctSound.dispose();
    }
}