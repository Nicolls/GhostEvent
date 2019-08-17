package com.nicolls.ghostevent.ghost;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.NonNull;

import com.nicolls.ghostevent.ghost.utils.LogUtil;
import com.nicolls.ghostevent.ghost.view.WebViewService;

public class ServiceGhost extends Ghost {
    private static final String TAG = "ServiceGhost";
    private final Context context;

    public ServiceGhost(@NonNull final Context context) {
        super(context);
        this.context = context.getApplicationContext();
    }

    @Override
    void onStart() {
        LogUtil.d(TAG, "onStart");
        Intent intent = new Intent(context, WebViewService.class);
        context.startService(intent);
    }

    @Override
    public void exit() {
        LogUtil.d(TAG, "exit");
//        Intent intent = new Intent(context, WebViewService.class);
//        context.stopService(intent);
    }

    @Override
    public void test() {
        LogUtil.d(TAG, "test");

    }
}
