package com.nicolls.ghostevent.ghost;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.nicolls.ghostevent.ghost.view.TestGhostWebView1;

import java.lang.ref.WeakReference;

public class ActivityGhost extends Ghost {
    private static final String TAG = "ActivityGhost";
    private final WeakReference<Activity> activityRef;
    private TestGhostWebView1 ghostWebView;

    public ActivityGhost(@NonNull final Activity activity) {
        activityRef = new WeakReference<>(activity);
    }

    @Override
    public void init() {
        Activity activity = activityRef.get();
        if (activity != null) {
            View decorView = activity.getWindow().getDecorView();
            ViewGroup viewGroup = decorView.findViewById(android.R.id.content);
            if (viewGroup == null) {
                viewGroup = (ViewGroup) decorView;
            }
            ghostWebView = new TestGhostWebView1(activity.getApplicationContext());
            ghostWebView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
            // setAlpha(0.001f);//不能设置成0，设置成0，将收不到事件，只要还有值 就可以接收到事件

//            ghostWebView.setTranslationX(500);
            viewGroup.addView(ghostWebView, 0);
            ghostWebView.start(DEFAULT_URL);
        }
    }

    @Override
    public void exit() {
        if (ghostWebView != null) {
            ghostWebView.stop();
            Activity activity = activityRef.get();
            if (activity != null) {
                ViewGroup viewGroup = (ViewGroup) ghostWebView.getParent();
                viewGroup.removeView(ghostWebView);
                ghostWebView = null;
            }
        }
        activityRef.clear();
    }

    @Override
    public void reload() {
        if (ghostWebView != null) {
            ghostWebView.reload(DEFAULT_URL);
        }
    }

    @Override
    public void record() {
        if (ghostWebView != null) {
            ghostWebView.record();
        }
    }

    @Override
    public void play() {
        if (ghostWebView != null) {
            ghostWebView.play();
        }
    }

    @Override
    public void goHome() {
        if(ghostWebView!=null){
            ghostWebView.goHome();
        }
    }


}
