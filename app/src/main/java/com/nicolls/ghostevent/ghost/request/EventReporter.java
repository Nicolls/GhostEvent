package com.nicolls.ghostevent.ghost.request;

import com.nicolls.ghostevent.ghost.request.model.UploadEventResponse;
import com.nicolls.ghostevent.ghost.request.network.NetRequest;
import com.nicolls.ghostevent.ghost.request.network.OkHttpRequest;
import com.nicolls.ghostevent.ghost.request.network.model.RequestParams;
import com.nicolls.ghostevent.ghost.utils.Constants;
import com.nicolls.ghostevent.ghost.utils.LogUtil;

import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class EventReporter implements IEventReport {
    private static final String TAG = "EventReporter";
    private final NetRequest request = new OkHttpRequest();
    private static EventReporter instance = new EventReporter();

    public static EventReporter getInstance() {
        return instance;
    }

    @Override
    public void uploadEvent(String type, String target, String params) {
        try {
            postRxJavaRequest("uploadEvent " + type, new Runnable() {
                @Override
                public void run() {
                    LogUtil.d(TAG, "uploadEvent");
                    RequestParams requestParams = new RequestParams.Builder()
                            .setUrl(Constants.DEFAULT_UPLOAD_EVENT_URL)
                            .addParams("type", type)
                            .addParams("target", target)
                            .addParams("params", params)
                            .setMethod(RequestParams.METHOD_GET)
                            .create();
                    UploadEventResponse response = request.executeRequest(requestParams, UploadEventResponse.class);
                    LogUtil.d(TAG, "uploadEvent completed " + response);
                }
            });
        } catch (Exception e) {
            LogUtil.w(TAG, "uploadEvent exception");
        }
    }

    private void postRxJavaRequest(String tag, Runnable runnable) {
        Completable.fromRunnable(runnable).subscribeOn(Schedulers.io()).subscribe(new CompletableObserver() {
            @Override
            public void onSubscribe(Disposable d) {
                LogUtil.d(TAG, "onSubscribe " + tag);
            }

            @Override
            public void onComplete() {
                LogUtil.d(TAG, "onComplete " + tag);
            }

            @Override
            public void onError(Throwable e) {
                LogUtil.e(TAG, "onError " + tag, e);
            }
        });
    }
}
