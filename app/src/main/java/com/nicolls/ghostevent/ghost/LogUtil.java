package com.nicolls.ghostevent.ghost;

import android.util.Log;

/**
 * author:mengjk
 * date:18-11-14
 * email:851778509@qq.com
 * <p>
 * </p>
 */
public class LogUtil {
    public static void d(String tag, String message){
        Log.d(tag,message);
    }
    public static void e(String tag, String message){
        Log.e(tag,message);
    }
    public static void i(String tag, String message){
        Log.i(tag,message);
    }
}
