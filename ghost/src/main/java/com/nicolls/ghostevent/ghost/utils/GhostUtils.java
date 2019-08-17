package com.nicolls.ghostevent.ghost.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class GhostUtils {
    private static final String TAG = "GhostUtils";
    public static int displayWidth = 0;
    public static int displayHeight = 0;
    public static float density = 1;
    public static String imei = "";
    public static String imeiMd5 = "";
    public static String androidId = "";
    public static String packageName = "";
    public static String imsi = "";
    public static String mac = "";
    private static boolean isInit = false;

    public static void init(Context context) {
        if (context == null || isInit) {
            return;
        }
        packageName = context.getPackageName();
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        displayWidth = dm.widthPixels;
        displayHeight = dm.heightPixels;
        density = dm.density;
        // android id
        androidId = getAndroidId(context);
        imsi = getSimOperator(context);
        mac = getMac(context);
        imei = getImei(context);
        if (!TextUtils.isEmpty(imei)) {
            imeiMd5 = md5(imei);
        }
        LogUtil.d(TAG, "init packageName:" + packageName
                + " width*height:" + displayWidth + "*" + displayHeight
                + " androidId:" + androidId
                + " imsi:" + imsi
                + " mac:" + mac
                + " imei:" + imei
                + " imeiMd5:" + imeiMd5
                + " model:" + Build.MODEL
                + " brand:" + Build.BRAND);
        isInit = true;
    }

    @SuppressLint("MissingPermission")
    public static String getImei(Context context) {
        String imei = "";
        try {
            //实例化TelephonyManager对象
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            //获取IMEI号
            imei = telephonyManager.getDeviceId();

        } catch (Exception e) {
            LogUtil.w(TAG, "get imei fail " + (e == null ? "" : e.getMessage()));
        }
        return imei;
    }

    public static String getParamsAdvertUrl(String url) {
        if (TextUtils.isEmpty(url)) {
            url = Constants.DEFAULT_UNION_URL;
        }
        if (url.contains("?")) {
            return url + "&im=" + imei + "&imMd5=" + imeiMd5 + "&androidId=" + androidId;
        } else {
            return url + "?im=" + imei + "&imMd5=" + imeiMd5 + "&androidId=" + androidId;
        }
    }

    public static String md5(String string) {
        if (TextUtils.isEmpty(string)) {
            return "";
        }
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
            byte[] bytes = md5.digest(string.getBytes());
            String result = "";
            for (byte b : bytes) {
                String temp = Integer.toHexString(b & 0xff);
                if (temp.length() == 1) {
                    temp = "0" + temp;
                }
                result += temp;
            }
            return result;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getSimOperator(Context context) {
        String imsi = "";
        try {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            return telephonyManager.getSimOperator();
        } catch (Exception e) {
            LogUtil.e(TAG, "getSimOperator", e);
        }
        return imsi;
    }

    public static String getMac(Context context) {
        String mac = "";
        try {
            mac = MacUtil.getMac(context);
            return mac;
        } catch (Exception e) {
            LogUtil.e(TAG, "getMac", e);
        }
        return mac;
    }


    public static String getAndroidId(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    public enum Page {
        HOME, SECOND_NEWS, SECOND_ADVERT, OTHER
    }

    public static Page currentPage(String url) {
        if (TextUtils.isEmpty(url)) {
            return Page.OTHER;
        }
        if (TextUtils.equals(url, Constants.DEFAULT_UNION_URL)
                || (url.contains(Constants.DEFAULT_UNION_DOMAIN) && !url.contains("detail"))) {
            return Page.HOME;
        }
        if (TextUtils.equals(url, Constants.DEFAULT_UNION_URL)
                || (url.contains(Constants.DEFAULT_UNION_DOMAIN) && url.contains("detail"))) {
            return Page.SECOND_NEWS;
        }
        if (url.contains(Constants.DEFAULT_UNION_DOMAIN_ADVERIT)) {
            return Page.SECOND_ADVERT;
        }

        return Page.OTHER;
    }

}
