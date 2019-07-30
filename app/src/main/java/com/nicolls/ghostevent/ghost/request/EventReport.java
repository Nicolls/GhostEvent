package com.nicolls.ghostevent.ghost.request;

import com.nicolls.ghostevent.ghost.request.model.UploadEventResponse;
import com.nicolls.ghostevent.ghost.request.network.NetRequest;
import com.nicolls.ghostevent.ghost.request.network.OkHttpRequest;
import com.nicolls.ghostevent.ghost.request.network.model.RequestParams;
import com.nicolls.ghostevent.ghost.utils.Constants;
import com.nicolls.ghostevent.ghost.utils.LogUtil;

import io.reactivex.Completable;
import io.reactivex.schedulers.Schedulers;

public class EventReport implements IEventReport {
    private static final String TAG = "EventReport";
    private final NetRequest request = new OkHttpRequest();

    @Override
    public void uploadEvent(String name, String target, String params) {
        Completable.fromRunnable(new Runnable() {
            @Override
            public void run() {
                RequestParams requestParams = new RequestParams.Builder()
                        .setUrl(Constants.DEFAULT_UPLOAD_EVENT_URL)
                        .addParams("name", name)
                        .addParams("target", target)
                        .addParams("params", params).create();
                UploadEventResponse response = request.executeRequest(requestParams, UploadEventResponse.class);
                LogUtil.d(TAG, "uploadEvent " + response);
            }
        }).subscribeOn(Schedulers.io()).subscribe();
    }
}
