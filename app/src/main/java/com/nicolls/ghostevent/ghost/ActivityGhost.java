package com.nicolls.ghostevent.ghost;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.nicolls.ghostevent.ghost.view.GhostWebView;

import java.lang.ref.WeakReference;

import static com.nicolls.ghostevent.ghost.utils.Constants.DEFAULT_URL_ZAKER;

public class ActivityGhost extends Ghost {
    private static final String TAG = "ActivityGhost";
    private final WeakReference<Activity> activityRef;
    private GhostWebView ghostWebView;
    private String url = DEFAULT_URL_ZAKER;

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
            ghostWebView = new GhostWebView(activity.getApplicationContext());
            ghostWebView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
            // setAlpha(0.001f);//不能设置成0，设置成0，将收不到事件，只要还有值 就可以接收到事件

//            ghostWebView.setTranslationX(2500);
            viewGroup.addView(ghostWebView, 0);
            ghostWebView.start(url);
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
            ghostWebView.reload();
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
        if (ghostWebView != null) {
            ghostWebView.goHome();
        }
    }

    @Override
    public void goBack() {
        if (ghostWebView != null) {
            ghostWebView.runGoBack();
        }
    }

    @Override
    public void parse() {
        if (ghostWebView != null) {
            ghostWebView.onParse();
        }
    }

    @Override
    public void playParse() {
        if (ghostWebView != null) {
            ghostWebView.onPlayParse();
        }
    }

    @Override
    public void random() {
        if (ghostWebView != null) {
            ghostWebView.random();
        }
    }


}
