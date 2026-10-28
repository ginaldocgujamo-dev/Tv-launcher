package com.meu.launcher;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.provider.Settings;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends Activity {

    private float s; // escala em relação ao desenho de 1280 px de largura

    private int p(float v) { return Math.round(v * s); }
    private int c(String hex) { return Color.parseColor(hex); }

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        s = getResources().getDisplayMetrics().widthPixels / 1280f;
        setContentView(buildUi());
    }

    @Override
    public void onBackPressed() {
        // Um launcher não deve fechar com o botão Voltar.
    }

    // ---------- Interface ----------

    private View buildUi() {
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.HORIZONTAL);
        root.setBackground(new GradientDrawable(GradientDrawable.Orientation.TL_BR,
                new int[]{c("#0d1a38"), c("#060913")}));
        root.addView(sidebar(), new LinearLayout.LayoutParams(p(216), ViewGroup.LayoutParams.MATCH_PARENT));
        root.addView(mainArea(), new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1f));
        return root;
    }

    private LinearLayout sidebar() {
        LinearLayout sb = new LinearLayout(this);
        sb.setOrientation(LinearLayout.VERTICAL);
        sb.setBackgroundColor(c("#070b18"));
        sb.setPadding(p(12), p(28), p(12), p(24));

        sb.addView(text("TV BOX", 24, "#ffffff", true));
        LinearLayout.LayoutParams subLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        subLp.bottomMargin = p(28);
        sb.addView(text("Mais do que TV", 12, "#9aa3bd", false), subLp);

        sb.addView(nav("Início", true, null), navLp());
        sb.addView(nav("Apps", false, v -> startActivity(new Intent(this, AppsActivity.class))), navLp());
        sb.addView(nav("Wi-Fi", false, v -> startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS))), navLp());
        sb.addView(nav("Definições", false, v -> startActivity(new Intent(Settings.ACTION_SETTINGS))), navLp());

        sb.addView(new View(this), new LinearLayout.LayoutParams(1, 0, 1f));
        sb.addView(text("Boa diversão!", 20, "#dfe3f2", false));
        return sb;
    }

    private LinearLayout.LayoutParams navLp() {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, p(50));
        lp.bottomMargin = p(6);
        return lp;
    }

    private TextView nav(String label, boolean active, View.OnClickListener l) {
        TextView v = text(label, 17, active ? "#ffffff" : "#c3c9dd", active);
        v.setGravity(Gravity.CENTER_VERTICAL);
        v.setPadding(p(22), 0, p(12), 0);
        v.setFocusable(true);
        if (active) {
            v.setBackground(box("#2f6bff", "#5b3df5", "#2f6bff", 25));
        } else {
            v.setBackground(box("#00000000", "#00000000", "#00000000", 25));
        }
        if (l != null) v.setOnClickListener(l);
        return v;
    }

    private LinearLayout mainArea() {
        LinearLayout m = new LinearLayout(this);
        m.setOrientation(LinearLayout.VERTICAL);
        m.setPadding(p(36), p(24), p(36), p(24));

        // Relógio e data
        LinearLayout clock = new LinearLayout(this);
        clock.setOrientation(LinearLayout.VERTICAL);
        clock.setGravity(Gravity.CENTER_HORIZONTAL);
        clock.addView(clockView("HH:mm", 28, "#ffffff", true));
        clock.addView(clockView("dd MMM yyyy", 13, "#c3c9dd", false));
        LinearLayout.LayoutParams clockLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        clockLp.gravity = Gravity.END;
        m.addView(clock, clockLp);

        // Saudação
        TextView hello = text("Olá!", 70, "#ffffff", true);
        hello.setTypeface(Typeface.create("cursive", Typeface.BOLD_ITALIC));
        m.addView(hello);
        m.addView(text("Tudo bem?", 22, "#ffffff", true));
        m.addView(text("O que vamos assistir hoje?", 18, "#dfe3f2", false));

        // Mosaicos grandes
        LinearLayout big = row();
        TextView yt = bigTile("YouTube", "#120507", "#4a0a10", "#ff3b45", "#ffffff",
                v -> launch("youtube", "com.google.android.youtube.tv", "com.google.android.youtube"));
        TextView sp = bigTile("Spotify", "#04120a", "#0a3a1c", "#22c55e", "#1ed760",
                v -> launch("spotify", "com.spotify.tv.android", "com.spotify.music"));
        TextView uni = bigTile("UniTV", "#0a0c2a", "#1b1d78", "#6c63ff", "#ffffff",
                v -> launch("unitv"));
        addTo(big, yt, p(160));
        addTo(big, sp, p(160));
        addTo(big, uni, p(160));
        LinearLayout.LayoutParams bigLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        bigLp.topMargin = p(28);
        m.addView(big, bigLp);

        // Atalhos pequenos
        LinearLayout small = row();
        addTo(small, smallTile("Todas as apps", v -> startActivity(new Intent(this, AppsActivity.class))), p(86));
        addTo(small, smallTile("Navegador", v -> launch("chrome", "com.android.chrome", "org.mozilla.firefox", "com.android.browser")), p(86));
        addTo(small, smallTile("Wi-Fi", v -> startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS))), p(86));
        addTo(small, smallTile("Definições", v -> startActivity(new Intent(Settings.ACTION_SETTINGS))), p(86));
        LinearLayout.LayoutParams smallLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        smallLp.topMargin = p(22);
        m.addView(small, smallLp);

        // Rodapé
        m.addView(new View(this), new LinearLayout.LayoutParams(1, 0, 1f));
        TextView foot = text("O melhor conteúdo, num só lugar!", 14, "#c3c9dd", false);
        foot.setGravity(Gravity.END);
        m.addView(foot, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        yt.requestFocus();
        return m;
    }

    // ---------- Peças de interface ----------

    private TextView text(String t, float size, String color, boolean bold) {
        TextView v = new TextView(this);
        v.setText(t);
        v.setTextColor(c(color));
        v.setTextSize(TypedValue.COMPLEX_UNIT_PX, p(size));
        if (bold) v.setTypeface(Typeface.DEFAULT_BOLD);
        return v;
    }

    private TextClock clockView(String format, float size, String color, boolean bold) {
        TextClock tc = new TextClock(this);
        tc.setFormat24Hour(format);
        tc.setFormat12Hour(format);
        tc.setTextColor(c(color));
        tc.setTextSize(TypedValue.COMPLEX_UNIT_PX, p(size));
        if (bold) tc.setTypeface(Typeface.DEFAULT_BOLD);
        return tc;
    }

    private LinearLayout row() {
        LinearLayout r = new LinearLayout(this);
        r.setOrientation(LinearLayout.HORIZONTAL);
        return r;
    }

    private void addTo(LinearLayout row, View v, int height) {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, height, 1f);
        lp.setMargins(p(10), 0, p(10), 0);
        row.addView(v, lp);
    }

    private TextView bigTile(String name, String c1, String c2, String stroke, String txt,
                             View.OnClickListener l) {
        TextView v = text(name, 34, txt, true);
        v.setGravity(Gravity.CENTER);
        v.setFocusable(true);
        v.setBackground(box(c1, c2, stroke, 16));
        v.setOnClickListener(l);
        return v;
    }

    private TextView smallTile(String name, View.OnClickListener l) {
        TextView v = text(name, 14, "#ffffff", false);
        v.setGravity(Gravity.CENTER);
        v.setFocusable(true);
        v.setBackground(box("#101a36", "#101a36", "#243158", 14));
        v.setOnClickListener(l);
        return v;
    }

    private StateListDrawable box(String c1, String c2, String stroke, float radius) {
        StateListDrawable st = new StateListDrawable();
        st.addState(new int[]{android.R.attr.state_focused}, shape(c1, c2, "#ffffff", 4, radius));
        st.addState(new int[]{}, shape(c1, c2, stroke, 2, radius));
        return st;
    }

    private GradientDrawable shape(String c1, String c2, String stroke, float sw, float radius) {
        GradientDrawable g = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{c(c1), c(c2)});
        g.setCornerRadius(p(radius));
        g.setStroke(p(sw), c(stroke));
        return g;
    }

    // ---------- Abrir apps ----------

    private void launch(String label, String... packages) {
        PackageManager pm = getPackageManager();
        for (String pkg : packages) {
            Intent i = pm.getLeanbackLaunchIntentForPackage(pkg);
            if (i == null) i = pm.getLaunchIntentForPackage(pkg);
            if (i != null) {
                startActivity(i);
                return;
            }
        }
        // Se o pacote não for conhecido, procura pelo nome da app.
        Intent main = new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> list = pm.queryIntentActivities(main, 0);
        for (ResolveInfo r : list) {
            String name = String.valueOf(r.loadLabel(pm)).toLowerCase();
            if (name.contains(label.toLowerCase())) {
                Intent i = pm.getLaunchIntentForPackage(r.activityInfo.packageName);
                if (i != null) {
                    startActivity(i);
                    return;
                }
            }
        }
        Toast.makeText(this, "A app " + label + " não está instalada", Toast.LENGTH_LONG).show();
    }
}
