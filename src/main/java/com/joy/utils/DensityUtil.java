package com.joy.utils;

import android.content.Context;
import android.support.annotation.DimenRes;
import android.support.annotation.NonNull;

public class DensityUtil {

    public static int dip2px(@NonNull Context appContext, float dp) {

        final float scale = appContext.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    public static int getDimensionPixelSize(@NonNull Context appContext, @DimenRes int id) {

        return appContext.getResources().getDimensionPixelSize(id);
    }
}
