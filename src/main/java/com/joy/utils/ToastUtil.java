package com.joy.utils;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.widget.Toast;

public class ToastUtil {

    private static Toast mToast;

    public static void showToast(@NonNull Context appContext, String text) {
        if (TextUtil.isEmpty(text)) {
            return;
        }
        try {
            if (mToast == null) {
                if (!(appContext instanceof Application)) {
                    appContext = appContext.getApplicationContext();
                }
                mToast = Toast.makeText(appContext, "", Toast.LENGTH_SHORT);
            }
            mToast.setText(text);
            mToast.show();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public static void showToast(@NonNull Context appContext, @StringRes int resId) {
        showToast(appContext, appContext.getResources().getString(resId));
    }

    public static void showToast(@NonNull Context appContext, @StringRes int resId, Object... args) {
        showToast(appContext, appContext.getResources().getString(resId, args));
    }

    public static void release() {
        mToast = null;
    }
}
