package com.nicolls.ghostevent.ghost;

import android.content.Context;

import com.nicolls.ghostevent.ghost.request.model.ConfigModel;
import com.nicolls.ghostevent.ghost.request.network.NetRequest;
import com.nicolls.ghostevent.ghost.request.network.OkHttpRequest;
import com.nicolls.ghostevent.ghost.request.network.model.RequestParams;
import com.nicolls.ghostevent.ghost.utils.Constants;
import com.nicolls.ghostevent.ghost.utils.LogUtil;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public abstract class Ghost {
    private static final String TAG = "Ghost";
    private NetRequest requester;
    private Context context;

    public Ghost(Context context) {
        this.context = context.getApplicationContext();
        requester = new OkHttpRequest();

    }

    public void init() {
        sendRequest();
    }

    private void sendRequest() {
        LogUtil.d(TAG, "sendRequest");
        Observable.create(new ObservableOnSubscribe<ConfigModel>() {
            @Override
            public void subscribe(ObservableEmitter<ConfigModel> emitter) throws Exception {
                LogUtil.d(TAG, "subscribe thread " + Thread.currentThread().getName());

                RequestParams params = new RequestParams.Builder()
                        .setUrl(Constants.INFO_URL)
                        .setMethod(RequestParams.METHOD_GET)
                        .create();

                try {
                    ConfigModel configModel = requester.executeRequest(params, ConfigModel.class);
                    emitter.onNext(configModel);
                    emitter.onComplete();
                } catch (Exception e) {
                    LogUtil.e(TAG, "sendRequest error ", e);
                }
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<ConfigModel>() {
            @Override
            public void onSubscribe(Disposable d) {
                LogUtil.d(TAG, "onSubscribe thread " + Thread.currentThread().getName());

            }

            @Override
            public void onNext(ConfigModel s) {
                LogUtil.d(TAG, "onNext thread " + Thread.currentThread().getName());
                if (s != null && s.result != null && s.result.enable) {
                    LogUtil.d(TAG, "onNext " + s.toString());
                    onStart();
                }
            }

            @Override
            public void onError(Throwable e) {
                LogUtil.d(TAG, "onError thread " + Thread.currentThread().getName());
                onStart();
            }

            @Override
            public void onComplete() {
                LogUtil.d(TAG, "onComplete thread " + Thread.currentThread().getName());
            }
        });
    }

    abstract void onStart();

    public abstract void exit();

    public abstract void test();
}
