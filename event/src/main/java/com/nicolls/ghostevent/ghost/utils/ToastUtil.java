package com.nicolls.ghostevent.ghost.utils;

import android.content.Context;
import android.widget.Toast;

import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;

public class ToastUtil {
    private static final boolean debug = false;

    public static void toast(final Context context, final String message) {
        if (!debug) {
            return;
        }
        Completable.fromRunnable(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, message, Toast.LENGTH_LONG).show();
            }
        }).subscribeOn(AndroidSchedulers.mainThread()).subscribe();
    }

    public static void toast(final Context context, final String message, final int duration) {
        if (!debug) {
            return;
        }
        Completable.fromRunnable(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, message, duration).show();
            }
        }).subscribeOn(AndroidSchedulers.mainThread()).subscribe();
    }
}
