package com.nicolls.ghostevent.ghost.request;

import com.nicolls.ghostevent.ghost.request.model.UploadEventResponse;
import com.nicolls.ghostevent.ghost.request.network.NetRequest;
import com.nicolls.ghostevent.ghost.request.network.OkHttpRequest;
import com.nicolls.ghostevent.ghost.request.network.model.RequestParams;
import com.nicolls.ghostevent.ghost.utils.Constants;
import com.nicolls.ghostevent.ghost.utils.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

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
    public void uploadEvent(final int type, final int target, final String params) {
        LogUtil.d(TAG, "uploadEvent type:" + type);
        try {
            postRxJavaRequest("uploadEvent " + type, new Runnable() {
                @Override
                public void run() {
                    LogUtil.d(TAG, "uploadEvent");

                    JSONObject object = new JSONObject();
                    try {
                        object.put("type", type);
                        object.put("target", target);
                        object.put("params", params);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    RequestParams requestParams = new RequestParams.Builder()
                            .setUrl(Constants.UPLOAD_EVENT_URL)
                            .setJsonParams(object)
                            .setMethod(RequestParams.METHOD_JSON)
                            .create();
                    UploadEventResponse response = request.executeRequest(requestParams, UploadEventResponse.class);
                    LogUtil.d(TAG, "uploadEvent completed " + response);
                }
            });
        } catch (Exception e) {
            LogUtil.w(TAG, "uploadEvent exception");
        }
    }

    private void postRxJavaRequest(final String tag, final Runnable runnable) {
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
