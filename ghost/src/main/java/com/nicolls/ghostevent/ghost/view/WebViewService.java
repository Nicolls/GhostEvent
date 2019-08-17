package com.nicolls.ghostevent.ghost.view;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class WebViewService extends Service {

    private static final String TAG = "WebViewService";

    public WebViewService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
