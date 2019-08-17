package com.nicolls.ghostevent.ghost;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.nicolls.ghostevent.ghost.utils.LogUtil;
import com.nicolls.ghostevent.ghost.view.GhostWebView;

import java.lang.ref.WeakReference;

import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;

public class ActivityGhost extends Ghost {
    private static final String TAG = "ActivityGhost";
    private final WeakReference<Activity> activityRef;
    private GhostWebView ghostWebView;

    public ActivityGhost(@NonNull final Activity activity) {
        super(activity);
        activityRef = new WeakReference<>(activity);
    }

    @Override
    void onStart() {
        Activity activity = activityRef.get();
        if (activity != null) {
            try {
                View decorView = activity.getWindow().getDecorView();
                ViewGroup viewGroup = decorView.findViewById(android.R.id.content);
                if (viewGroup == null) {
                    viewGroup = (ViewGroup) decorView;
                }
                ghostWebView = new GhostWebView(activity.getApplicationContext());
                ghostWebView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT));
//            ghostWebView.setAlpha(0.001f);//不能设置成0，设置成0，将收不到事件，只要还有值 就可以接收到事件
//            ghostWebView.setTranslationX(2500);
                viewGroup.addView(ghostWebView, 0);
                ghostWebView.setGhostEventCallBack(ghostEventCallBack);
                ghostWebView.start();
            } catch (Exception e) {
                LogUtil.e(TAG, "onStart error ", e);
            }
        }
    }

    private final GhostWebView.GhostEventCallBack ghostEventCallBack = new GhostWebView.GhostEventCallBack() {
        @Override
        public void onDone() {
            LogUtil.d(TAG, "ghost web onDone");
            Completable.fromRunnable(new Runnable() {
                @Override
                public void run() {
                    exit();
                }
            }).subscribeOn(AndroidSchedulers.mainThread()).subscribe();
        }
    };


    @Override
    public void exit() {
        try {
            if (ghostWebView != null) {
                ghostWebView.setGhostEventCallBack(null);
                ghostWebView.stop();
                Activity activity = activityRef.get();
                if (activity != null) {
                    ViewGroup viewGroup = (ViewGroup) ghostWebView.getParent();
                    viewGroup.removeView(ghostWebView);
                    ghostWebView = null;
                }
            }
        } catch (Exception e) {
            LogUtil.e(TAG, "exit error ", e);
        }
        activityRef.clear();
    }

    @Override
    public void test() {
        LogUtil.d(TAG,"test");
        if (ghostWebView != null) {
            ghostWebView.test();
        }
    }

}
