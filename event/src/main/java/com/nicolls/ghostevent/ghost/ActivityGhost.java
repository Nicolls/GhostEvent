package com.nicolls.ghostevent.ghost;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.nicolls.ghostevent.ghost.utils.LogUtil;
import com.nicolls.ghostevent.ghost.view.GhostWebView;

import java.lang.ref.WeakReference;

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
//                ghostWebView.setAlpha(0.001f);//不能设置成0，设置成0，将收不到事件，只要还有值 就可以接收到事件
//                int translationX = GhostUtils.displayWidth < 1000 ? 2500 : (GhostUtils.displayWidth + 300);
//                LogUtil.d(TAG, "translationX " + translationX);
//                ghostWebView.setTranslationX(translationX);
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
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    exit();
                }
            });
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
        LogUtil.d(TAG, "test");
        if (ghostWebView != null) {
            ghostWebView.test();
        }
    }

    @Override
    public void back() {
        LogUtil.d(TAG, "back");
        if (ghostWebView != null) {
            ghostWebView.back();
        }
    }

}
