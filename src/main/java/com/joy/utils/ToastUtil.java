package com.joy.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.widget.Toast;

public class ToastUtil {

    private static Toast mToast;

    private static void initToast(@NonNull Context appContext) {

        if (mToast == null)
            mToast = Toast.makeText(appContext, "", Toast.LENGTH_SHORT);
    }

    public static void showToast(@NonNull Context appContext, @StringRes int resId) {

        try {

            initToast(appContext);
            mToast.setText(resId);
            mToast.show();
        } catch (Throwable t) {
        }
    }

    public static void showToast(@NonNull Context appContext, String text) {

        if (TextUtil.isEmpty(text))
            return;

        try {

            initToast(appContext);
            mToast.setText(text);
            mToast.show();
        } catch (Throwable t) {
        }
    }

    public static void showToast(@NonNull Context appContext, @StringRes int resId, Object... args) {

        showToast(appContext, appContext.getResources().getString(resId, args));
    }

    public static void release() {

        mToast = null;
    }
}
