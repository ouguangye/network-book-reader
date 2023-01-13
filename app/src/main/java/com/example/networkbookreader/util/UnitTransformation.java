package com.example.networkbookreader.util;

import android.content.Context;

public class UnitTransformation {
    private Context context;

    public UnitTransformation(Context context) {
        this.context = context;
    }

    public int dip2px(float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public int px2sp(float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }
}
