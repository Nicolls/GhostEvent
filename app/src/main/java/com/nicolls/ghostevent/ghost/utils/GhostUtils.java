package com.nicolls.ghostevent.ghost.utils;

import android.annotation.SuppressLint;
import android.content.Context;
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
    public static String imei;
    public static String imeiMd5;
    public static String androidId;
    private static boolean isInit = false;

    @SuppressLint("MissingPermission")
    public static void init(Context context) {
        if (context == null || isInit) {
            return;
        }
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        displayWidth = dm.widthPixels;
        displayHeight = dm.heightPixels;
        density = dm.density;
        // android id
        androidId = getAndroidId(context);
        // imei
        try {
            //实例化TelephonyManager对象
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            //获取IMEI号
            imei = telephonyManager.getDeviceId();
            //在次做个验证，也不是什么时候都能获取到的啊
            if (TextUtils.isEmpty(imei)) {
                imei = "";
                imeiMd5 = "";
            } else {
                imeiMd5 = md5(imei);
            }

        } catch (Exception e) {
            LogUtil.w(TAG, "get imei fail " + (e == null ? "" : e.getMessage()));
        }

        isInit = true;
    }

    public static String getParamsAdvertUrl(String url) {
        if (TextUtils.isEmpty(url)) {
            url = Constants.DEFAULT_ADVERT_URL;
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

    public static String getAndroidId(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

}
