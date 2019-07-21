package com.nicolls.ghostevent.ghost;

import android.content.Context;
import android.graphics.Point;
import android.util.DisplayMetrics;

/**
 * author:mengjk
 * date:18-11-20
 * email:nicolls@qq.com
 * <p>
 * </p>
 */
public class DisplayUtils {
    public static Point getDisplaySize(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return new Point(dm.widthPixels, dm.heightPixels);
    }
}
