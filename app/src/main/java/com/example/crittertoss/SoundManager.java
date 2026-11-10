package com.example.crittertoss;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.SoundPool;

import java.util.Random;

/** Owns the background music + sound effects and the mute settings. */
public final class SoundManager {

    public static final int THROW = 0, HIT = 1, MISS = 2, CLICK = 3, OVER = 4,
            POWER = 5, COMBO = 6, BOOM = 7;

    private static SoundManager instance;

    public static SoundManager get(Context c) {
        if (instance == null) instance = new SoundManager(c.getApplicationContext());
        return instance;
    }

    private final Context ctx;
    private final SharedPreferences prefs;
    private final SoundPool pool;
    private final int[] ids = new int[8];
    private final Random rng = new Random();
    private MediaPlayer music;
    private boolean musicOn, sfxOn;
    private float musicVol, sfxVol;     // 0..1 slider values
    private boolean wantMusic = false;   // true while an activity is in the foreground

    private SoundManager(Context c) {
        ctx = c;
        prefs = c.getSharedPreferences("critter", Context.MODE_PRIVATE);
        musicOn = prefs.getBoolean("music", true);
        sfxOn = prefs.getBoolean("sfx", true);
        musicVol = prefs.getFloat("musicVol", 1f);
        sfxVol = prefs.getFloat("sfxVol", 1f);

        AudioAttributes attrs = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();
        pool = new SoundPool.Builder().setMaxStreams(6).setAudioAttributes(attrs).build();
        ids[THROW] = pool.load(c, R.raw.sfx_throw, 1);
        ids[HIT] = pool.load(c, R.raw.sfx_hit, 1);
        ids[MISS] = pool.load(c, R.raw.sfx_miss, 1);
        ids[CLICK] = pool.load(c, R.raw.sfx_click, 1);
        ids[OVER] = pool.load(c, R.raw.sfx_over, 1);
        ids[POWER] = pool.load(c, R.raw.sfx_power, 1);
        ids[COMBO] = pool.load(c, R.raw.sfx_combo, 1);
        ids[BOOM] = pool.load(c, R.raw.sfx_boom, 1);
    }

    // ---------- sfx ----------
    public void play(int sfx) {
        if (!sfxOn) return;
        float rate = (sfx == HIT || sfx == MISS) ? .94f + rng.nextFloat() * .14f : 1f;
        if (sfxVol <= 0f) return;
        pool.play(ids[sfx], sfxVol, sfxVol, 1, 0, rate);
    }

    /** Plays a sound at a chosen pitch (1 = normal). Used to make hits sound higher as the combo grows. */
    public void playPitched(int sfx, float rate) {
        if (!sfxOn || sfxVol <= 0f) return;
        pool.play(ids[sfx], sfxVol, sfxVol, 1, 0, Math.max(.5f, Math.min(2f, rate)));
    }

    public boolean isSfxOn() { return sfxOn; }

    public float getSfxVolume() { return sfxVol; }

    public void setSfxVolume(float v) {
        sfxVol = Math.max(0f, Math.min(1f, v));
        prefs.edit().putFloat("sfxVol", sfxVol).apply();
    }

    public float getMusicVolume() { return musicVol; }

    public void setMusicVolume(float v) {
        musicVol = Math.max(0f, Math.min(1f, v));
        prefs.edit().putFloat("musicVol", musicVol).apply();
        if (music != null) music.setVolume(.45f * musicVol, .45f * musicVol);
    }

    public void setSfxOn(boolean on) {
        sfxOn = on;
        prefs.edit().putBoolean("sfx", on).apply();
    }

    // ---------- music ----------
    public boolean isMusicOn() { return musicOn; }

    /** Call from onResume of each activity. */
    public void startMusic() {
        wantMusic = true;
        if (!musicOn) return;
        if (music == null) {
            music = MediaPlayer.create(ctx, R.raw.music_loop);
            if (music == null) return;
            music.setLooping(true);
            music.setVolume(.45f * musicVol, .45f * musicVol);
        }
        if (!music.isPlaying()) music.start();
    }

    /** Call from onPause of each activity. */
    public void pauseMusic() {
        wantMusic = false;
        if (music != null && music.isPlaying()) music.pause();
    }

    public void setMusicOn(boolean on) {
        musicOn = on;
        prefs.edit().putBoolean("music", on).apply();
        if (on) { if (wantMusic) startMusic(); }
        else if (music != null && music.isPlaying()) music.pause();
    }
}