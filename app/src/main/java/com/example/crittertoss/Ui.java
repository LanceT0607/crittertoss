package com.example.crittertoss;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.widget.SeekBar;

/** Builds all button / panel looks in code, so no drawable XML files are needed. */
final class Ui {
    private Ui() {}

    private static int dp(Context c, float v) {
        return Math.round(v * c.getResources().getDisplayMetrics().density);
    }

    /** Width for popup dialogs: the full window on phones, capped at 460dp on tablets / wide windows. */
    static int dialogWidth(Context c) {
        DisplayMetrics dm = c.getResources().getDisplayMetrics();
        return Math.min(dm.widthPixels, dp(c, 460));
    }

    private static GradientDrawable box(Context c, String top, String bottom, String stroke, float strokeDp, float radiusDp) {
        GradientDrawable g = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{Color.parseColor(top), Color.parseColor(bottom)});
        g.setStroke(dp(c, strokeDp), Color.parseColor(stroke));
        g.setCornerRadius(dp(c, radiusDp));
        return g;
    }

    static Drawable yellow(Context c) { return box(c, "#FFD764", "#FFBA2A", "#C8861E", 4, 26); }
    static Drawable green(Context c)  { return box(c, "#93E276", "#5BBF47", "#3E8F36", 4, 26); }
    static Drawable chip(Context c)   { return box(c, "#F7E9C8", "#E6D0A0", "#C99A5B", 4, 20); }
    static Drawable red(Context c)    { return box(c, "#F25A3C", "#C8341E", "#FFC957", 4, 26); }
    /** Black lacquer with a gold edge, like the shrine's sign board. */
    static Drawable wood(Context c)   { return box(c, "#3E343C", "#211B21", "#FFC957", 4, 22); }
    static Drawable banner(Context c) { return box(c, "#3A3038", "#1C161C", "#FFC957", 5, 16); }
    static Drawable tab(Context c)    { return box(c, "#3E343C", "#211B21", "#FFC957", 4, 28); }

    static Drawable dot(Context c) {
        GradientDrawable g = new GradientDrawable();
        g.setShape(GradientDrawable.OVAL);
        g.setColor(Color.parseColor("#FFD66B"));
        g.setStroke(dp(c, 2), Color.parseColor("#B8801E"));
        return g;
    }

    /** Tan frame with a cream inside, like the settings panel in the reference. */
    static Drawable panel(Context c) {
        GradientDrawable outer = new GradientDrawable();
        outer.setColor(Color.parseColor("#BC8F50"));
        outer.setCornerRadius(dp(c, 34));
        GradientDrawable inner = new GradientDrawable();
        inner.setColor(Color.parseColor("#FFF8E3"));
        inner.setCornerRadius(dp(c, 14));
        LayerDrawable ld = new LayerDrawable(new Drawable[]{outer, inner});
        int m = dp(c, 14);
        ld.setLayerInset(1, m, m, m, m);
        return ld;
    }

    /** Yellow slider with a round cream knob. */
    static void styleSeek(Context c, SeekBar bar) {
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(Color.parseColor("#EAD9B0"));
        bg.setCornerRadius(dp(c, 8));
        bg.setSize(0, dp(c, 14));
        GradientDrawable fill = new GradientDrawable();
        fill.setColor(Color.parseColor("#FFC531"));
        fill.setCornerRadius(dp(c, 8));
        fill.setSize(0, dp(c, 14));
        ClipDrawable clip = new ClipDrawable(fill, Gravity.START, ClipDrawable.HORIZONTAL);
        LayerDrawable track = new LayerDrawable(new Drawable[]{bg, clip});
        track.setId(0, android.R.id.background);
        track.setId(1, android.R.id.progress);
        bar.setProgressDrawable(track);

        GradientDrawable knob = new GradientDrawable();
        knob.setShape(GradientDrawable.OVAL);
        knob.setColor(Color.parseColor("#FFF8E3"));
        knob.setStroke(dp(c, 4), Color.parseColor("#E0A21C"));
        knob.setSize(dp(c, 32), dp(c, 32));
        bar.setThumb(knob);
    }
}