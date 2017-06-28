package com.joy.utils;

import android.content.Context;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by KEVIN.DAI on 15/7/10.
 */
public class DeviceUtil {

    public static String getIMEI(@NonNull Context appContext) {
        String imei = "";
        try {
            TelephonyManager telephonyManager = (TelephonyManager) appContext.getSystemService(Context.TELEPHONY_SERVICE);
            imei = telephonyManager.getDeviceId();
            if (TextUtils.isEmpty(imei))
                imei = Settings.Secure.getString(appContext.getContentResolver(), Settings.Secure.ANDROID_ID);
            if (imei == null)
                imei = "";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return imei;
    }

    /**
     * @return 状态栏的高度
     */
    public static int getStatusBarHeight(@NonNull Context appContext) {
        Resources resources = appContext.getResources();
        int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        return resources.getDimensionPixelSize(resourceId);
    }

    /**
     * @return 导航栏的高度
     */
    public static int getNavigationBarHeight(@NonNull Context appContext) {
        Resources resources = appContext.getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        return resources.getDimensionPixelSize(resourceId);
    }

    /**
     * @return 是否有导航栏
     */
    public static boolean hasNavigationBar(@NonNull Context appContext) {
        Resources resources = appContext.getResources();
        int resourceId = resources.getIdentifier("config_showNavigationBar", "bool", "android");
        return resourceId > 0 && resources.getBoolean(resourceId);
    }

    public static int getScreenWidth(@NonNull Context appContext) {
        return appContext.getResources().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight(@NonNull Context appContext) {
        return appContext.getResources().getDisplayMetrics().heightPixels;
    }

    public static boolean isNetworkEnable(@NonNull Context appContext) {
        ConnectivityManager conManager = (ConnectivityManager) appContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = conManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isAvailable();
    }

    public static boolean isNetworkDisable(@NonNull Context appContext) {
        return !isNetworkEnable(appContext);
    }

    public static boolean sdcardIsEnable() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)
                && !Environment.getExternalStorageState().equals(Environment.MEDIA_SHARED);
    }

    public static void hideSoftInput(Context context, View attachView) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(attachView.getWindowToken(), 0);
    }
}
