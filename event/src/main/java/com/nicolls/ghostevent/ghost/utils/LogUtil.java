package com.nicolls.ghostevent.ghost.utils;

import android.util.Log;

/**
 * author:mengjk
 * date:18-11-14
 * email:851778509@qq.com
 * <p>
 * </p>
 */
public class LogUtil {
    private static final boolean debug = false;

    public static void d(String tag, String message) {
        if (!debug) {
            return;
        }
        Log.d(tag, message);
    }

    public static void d(String tag, String message, boolean print) {
        if (!print) {
            return;
        }
        Log.d(tag, message);
    }

    public static void w(String tag, String message) {
        if (!debug) {
            return;
        }
        Log.w(tag, message);
    }

    public static void v(String tag, String message) {
        if (!debug) {
            return;
        }
        Log.v(tag, message);
    }

    public static void e(String tag, String message) {
        if (!debug) {
            return;
        }
        Log.e(tag, message);
    }

    public static void e(String tag, String message, Throwable e) {
        if (!debug) {
            return;
        }
        Log.e(tag, message + ":" + (e == null ? "" : e.getMessage()));
    }

    public static void i(String tag, String message) {
        if (!debug) {
            return;
        }
        Log.i(tag, message);
    }
}
