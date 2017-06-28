package com.joy.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;

/**
 * Created by KEVIN.DAI on 15/7/10.
 */
public class AppUtil {

    /**
     * 获取应用第一次安装时间
     */
    public static long getInstallTime(@NonNull Context appContext) {
        long firstInstallTime = 0;
        try {
            PackageInfo packageInfo = appContext.getPackageManager().getPackageInfo(appContext.getPackageName(), 0);
            firstInstallTime = packageInfo.firstInstallTime;// 应用第一次安装的时间
        } catch (Exception e) {
            e.printStackTrace();
        }
        return firstInstallTime;
    }

    /**
     * 检查app是否有安装
     */
    public static boolean checkHasApp(@NonNull Context appContext, String packageName) {
        try {
            PackageInfo packageInfo = appContext.getPackageManager().getPackageInfo(packageName, 0);
            int highBit = packageInfo.versionName.charAt(0);
            return highBit > 50 ? true : false;// 50 = 2
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 获取VersionCode，默认返回1
     */
    public static int getVersionCode(Context appContext) {
        try {
            PackageInfo packInfo = appContext.getPackageManager().getPackageInfo(appContext.getPackageName(), 0);
            return packInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return 1;
        }
    }

    /**
     * 获取VersionName
     */
    public static String getVersionName(Context appContext) {
        try {
            PackageInfo packInfo = appContext.getPackageManager().getPackageInfo(appContext.getPackageName(), 0);
            return TextUtil.filterNull(packInfo.versionName);
        } catch (Exception e) {
            e.printStackTrace();
            return TextUtil.TEXT_EMPTY;
        }
    }
}
