package com.nicolls.ghostevent.ghost.view;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.nicolls.ghostevent.ghost.utils.GhostUtils;
import com.nicolls.ghostevent.ghost.utils.LogUtil;

public class WebViewService extends Service {

    private static final String TAG = "WebViewService";
    private GhostWebView ghostWebView;
    private WindowManager windowManager;
    private Handler mainHandler = new Handler(Looper.getMainLooper());

    public WebViewService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        LogUtil.d(TAG, "onCreate");
    }

    private final GhostWebView.GhostEventCallBack ghostEventCallBack = new GhostWebView.GhostEventCallBack() {
        @Override
        public void onDone() {
            LogUtil.d(TAG, "ghost web onDone");
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    exit();
                }
            });
        }
    };

    private void exit() {
        try {
            if (ghostWebView != null) {
                ghostWebView.setGhostEventCallBack(null);
                ghostWebView.stop();
                windowManager.removeViewImmediate(ghostWebView);
                ghostWebView = null;
            }
        } catch (Exception e) {
            LogUtil.e(TAG, "exit error ", e);
        }
        stopSelf();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtil.d(TAG, "onStartCommand");
        addGhostWebView();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtil.d(TAG, "onDestroy");
    }


    private void addGhostWebView() {
        if (ghostWebView != null) {
            LogUtil.i(TAG, "ghostWebView exist");
            return;
        }
        ghostWebView = new GhostWebView(getApplicationContext());
        ghostWebView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
//        ghostWebView.setAlpha(0.001f);//不能设置成0，设置成0，将收不到事件，只要还有值 就可以接收到事件
        ghostWebView.setGhostEventCallBack(ghostEventCallBack);

        // 窗体的布局样式
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        // 设置窗体显示类型
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            layoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        }
        // 设置窗体焦点及触摸：
        // FLAG_NOT_FOCUSABLE(不能获得按键输入焦点)
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        // 设置显示的模式
        layoutParams.format = PixelFormat.RGBA_8888;
        // 设置对齐的方法
        layoutParams.gravity = Gravity.TOP;
        // 设置窗体宽度和高度
        layoutParams.width = GhostUtils.displayWidth;
        layoutParams.height = GhostUtils.displayHeight;

        try {
            windowManager.addView(ghostWebView, layoutParams);
            ghostWebView.start();
        } catch (Exception e) {
            LogUtil.e(TAG, "global wakeup add view error:", e);
        }
    }

}
