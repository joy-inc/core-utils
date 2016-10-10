package com.joy.utils;

import android.util.Log;

public class LogMgr {

    private static String TAG = "LogMgr";
    private static boolean mIsDebug = true;

    public static void turnOn() {

        mIsDebug = true;
    }

    public static void turnOff() {

        mIsDebug = false;
    }

    public static void setTag(String tagName) {

        TAG = tagName;
    }

    public static String getTag() {

        return TAG;
    }

    public static void v(String log) {

        v(TAG, log);
    }

    public static void v(String tag, String log) {

        if (mIsDebug)
            Log.v(tag, log);
    }

    public static void d(String log) {

        d(TAG, log);
    }

    public static void d(String tag, String log) {

        if (mIsDebug)
            Log.d(tag, log);
    }

    public static void i(String log) {

        i(TAG, log);
    }

    public static void i(String tag, String log) {

        if (mIsDebug)
            Log.i(tag, log);
    }

    public static void w(String log) {

        w(TAG, log);
    }

    public static void w(String tag, String log) {

        if (mIsDebug)
            Log.w(tag, log);
    }

    public static void e(String log) {

        e(TAG, log);
    }

    public static void e(String tag, String log) {

        if (mIsDebug)
            Log.e(tag, log);
    }
}
