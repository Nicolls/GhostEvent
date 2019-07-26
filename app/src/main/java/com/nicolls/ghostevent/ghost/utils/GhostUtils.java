package com.nicolls.ghostevent.ghost.utils;

import android.content.Context;
import android.util.DisplayMetrics;

public class GhostUtils {
    public static int displayWidth = 0;
    public static int displayHeight = 0;
    public static float density=1;
    private static boolean isInit = false;

    public static void init(Context context) {
        if (context == null || isInit) {
            return;
        }
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        displayWidth = dm.widthPixels;
        displayHeight = dm.heightPixels;
        density=dm.density;
        isInit = true;
    }
}
