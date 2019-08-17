package com.nicolls.ghostevent.ghost.utils;

import android.content.Context;
import android.widget.Toast;

import com.nicolls.ghost.BuildConfig;

import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;

public class ToastUtil {
    public static void toast(final Context context, final String message) {
        if(!BuildConfig.DEBUG){
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
        if(!BuildConfig.DEBUG){
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
