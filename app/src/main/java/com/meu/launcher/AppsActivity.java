package com.meu.launcher;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

public class AppsActivity extends Activity {

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        final float s = getResources().getDisplayMetrics().widthPixels / 1280f;
        final PackageManager pm = getPackageManager();
        Intent main = new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER);
        final List<ResolveInfo> apps = pm.queryIntentActivities(main, 0);
        Collections.sort(apps, new ResolveInfo.DisplayNameComparator(pm));

        GridView grid = new GridView(this);
        grid.setNumColumns(6);
        grid.setBackgroundColor(Color.parseColor("#0a1226"));
        int pad = Math.round(40 * s);
        grid.setPadding(pad, pad, pad, pad);
        grid.setVerticalSpacing(Math.round(16 * s));
        grid.setHorizontalSpacing(Math.round(16 * s));
        grid.setClipToPadding(false);

        GradientDrawable sel = new GradientDrawable();
        sel.setColor(Color.parseColor("#332f6bff"));
        sel.setStroke(Math.round(3 * s), Color.WHITE);
        sel.setCornerRadius(Math.round(14 * s));
        grid.setSelector(sel);

        grid.setAdapter(new BaseAdapter() {
            @Override public int getCount() { return apps.size(); }
            @Override public Object getItem(int i) { return apps.get(i); }
            @Override public long getItemId(int i) { return i; }

            @Override
            public View getView(int i, View convertView, ViewGroup parent) {
                ResolveInfo r = apps.get(i);
                LinearLayout l = new LinearLayout(AppsActivity.this);
                l.setOrientation(LinearLayout.VERTICAL);
                l.setGravity(Gravity.CENTER_HORIZONTAL);
                int q = Math.round(10 * s);
                l.setPadding(q, q, q, q);

                int size = Math.round(72 * s);
                ImageView iv = new ImageView(AppsActivity.this);
                iv.setImageDrawable(r.loadIcon(pm));
                l.addView(iv, new LinearLayout.LayoutParams(size, size));

                TextView tv = new TextView(AppsActivity.this);
                tv.setText(r.loadLabel(pm));
                tv.setTextColor(Color.WHITE);
                tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, 14 * s);
                tv.setSingleLine(true);
                tv.setEllipsize(TextUtils.TruncateAt.END);
                tv.setGravity(Gravity.CENTER);
                l.addView(tv, new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                return l;
            }
        });

        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int pos, long id) {
                Intent i = pm.getLaunchIntentForPackage(apps.get(pos).activityInfo.packageName);
                if (i != null) startActivity(i);
            }
        });

        setContentView(grid);
    }
}
