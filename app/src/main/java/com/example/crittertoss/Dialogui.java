package com.example.crittertoss;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.OvershootInterpolator;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

/** Shared helpers for the in-game dialogs (pause menu, sound & music). */
final class Dialogui {
    private Dialogui() {}

    /** Inflates a dialog that has @id/dialogPanel and @id/dialogTitle and styles it. */
    static Dialog make(Activity a, int layout) {
        final Dialog d = new Dialog(a);
        d.requestWindowFeature(Window.FEATURE_NO_TITLE);
        d.setContentView(layout);
        Window win = d.getWindow();
        if (win != null) {
            win.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            win.setDimAmount(.6f);
            win.setLayout(Ui.dialogWidth(a), ViewGroup.LayoutParams.WRAP_CONTENT);   // capped width on tablets
        }
        d.findViewById(R.id.dialogPanel).setBackground(Ui.panel(a));
        d.findViewById(R.id.dialogTitle).setBackground(Ui.tab(a));
        return d;
    }

    /** Shows the dialog with a pop-in animation. immersive = keep the game's hidden system bars hidden. */
    static void show(Dialog d, boolean immersive) {
        Window w = d.getWindow();
        final View panel = d.findViewById(R.id.dialogPanel);
        panel.setScaleX(.8f);
        panel.setScaleY(.8f);
        panel.setAlpha(0f);
        if (immersive && w != null) w.setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        d.show();
        if (immersive && w != null) {
            new WindowInsetsControllerCompat(w, w.getDecorView()).hide(WindowInsetsCompat.Type.systemBars());
            w.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        }
        panel.animate().scaleX(1f).scaleY(1f).alpha(1f).setDuration(260)
                .setInterpolator(new OvershootInterpolator(1.4f)).start();
    }

    @SuppressLint("ClickableViewAccessibility")
    static void press(View v) {
        v.setOnTouchListener((view, e) -> {
            switch (e.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    view.animate().scaleX(.94f).scaleY(.94f).setDuration(80).start();
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    view.animate().scaleX(1f).scaleY(1f).setDuration(80).start();
                    break;
            }
            return false;   // let the click still go through
        });
    }

    static void styleToggle(Context c, TextView t, boolean on) {
        t.setText(on ? "ON" : "OFF");
        t.setBackground(on ? Ui.green(c) : Ui.wood(c));
        t.setTextColor(on ? Color.WHITE : Color.parseColor("#FFF3D6"));
    }

    /** The "Sound & Music" dialog: Music / Sound toggles + volume sliders. */
    static Dialog makeSoundDialog(Activity a) {
        final SoundManager sm = SoundManager.get(a);
        final Dialog d = make(a, R.layout.dialog_sound);

        final TextView music = d.findViewById(R.id.btnMusic);
        final TextView sfx = d.findViewById(R.id.btnSfx);
        final SeekBar seekMusic = d.findViewById(R.id.seekMusic);
        final SeekBar seekSfx = d.findViewById(R.id.seekSfx);

        Ui.styleSeek(a, seekMusic);
        Ui.styleSeek(a, seekSfx);
        seekMusic.setProgress(Math.round(sm.getMusicVolume() * 100f));
        seekSfx.setProgress(Math.round(sm.getSfxVolume() * 100f));

        final Runnable refresh = () -> {
            styleToggle(a, music, sm.isMusicOn());
            styleToggle(a, sfx, sm.isSfxOn());
        };
        refresh.run();

        press(music);
        press(sfx);
        music.setOnClickListener(v -> { sm.setMusicOn(!sm.isMusicOn()); sm.play(SoundManager.CLICK); refresh.run(); });
        sfx.setOnClickListener(v -> { sm.setSfxOn(!sm.isSfxOn()); sm.play(SoundManager.CLICK); refresh.run(); });

        seekMusic.setOnSeekBarChangeListener(new SimpleSeek() {
            @Override public void onProgressChanged(SeekBar s, int p, boolean user) {
                if (user) sm.setMusicVolume(p / 100f);   // music keeps playing, so you hear it change
            }
        });
        seekSfx.setOnSeekBarChangeListener(new SimpleSeek() {
            @Override public void onProgressChanged(SeekBar s, int p, boolean user) {
                if (user) sm.setSfxVolume(p / 100f);
            }
            @Override public void onStopTrackingTouch(SeekBar s) { sm.play(SoundManager.CLICK); }   // preview
        });

        View done = d.findViewById(R.id.btnDone);
        done.setBackground(Ui.yellow(a));
        press(done);
        done.setOnClickListener(v -> { sm.play(SoundManager.CLICK); d.dismiss(); });
        return d;
    }

    private abstract static class SimpleSeek implements SeekBar.OnSeekBarChangeListener {
        @Override public void onStartTrackingTouch(SeekBar s) { }
        @Override public void onStopTrackingTouch(SeekBar s) { }
    }
}