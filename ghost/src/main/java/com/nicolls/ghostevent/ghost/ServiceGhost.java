package com.nicolls.ghostevent.ghost;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;

import com.nicolls.ghostevent.ghost.view.WebViewService;

public class ServiceGhost extends Ghost {
    private static final String TAG="ServiceGhost";
    private final Context context;

    public ServiceGhost(@NonNull final Context context) {
        this.context = context.getApplicationContext();
    }

    @Override
    void onStart() {
        Intent intent = new Intent(context, WebViewService.class);
        context.startService(intent);
    }

    @Override
    public void exit() {
        Intent intent = new Intent(context, WebViewService.class);
        context.stopService(intent);
    }
}
