package com.nicolls.ghostevent.ghost.request;

import android.os.Build;

import com.nicolls.ghostevent.ghost.request.network.HttpUrlRequest;
import com.nicolls.ghostevent.ghost.request.network.NetRequest;
import com.nicolls.ghostevent.ghost.request.network.model.RequestParams;
import com.nicolls.ghostevent.ghost.utils.Constants;
import com.nicolls.ghostevent.ghost.utils.GhostUtils;
import com.nicolls.ghostevent.ghost.utils.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class EventReporter implements IEventReport {
    private static final String TAG = "EventReporter";
    private final HttpUrlRequest request = new HttpUrlRequest();
    private static EventReporter instance = new EventReporter();
    private Executor executor = Executors.newSingleThreadExecutor();

    public static EventReporter getInstance() {
        return instance;
    }

    private EventReporter(){
        request.setRequestCallBack(requestCallBack);
    }

    private NetRequest.RequestCallBack requestCallBack = new NetRequest.RequestCallBack() {
        @Override
        public void onSuccess(JSONObject response) {
            LogUtil.d(TAG, "onSuccess " + (response == null ? "null" : response.toString()));
        }

        @Override
        public void onFail(String message) {
            LogUtil.d(TAG, "onFail " + message);
        }
    };

    @Override
    public void uploadEvent(final int type) {
        LogUtil.d(TAG, "uploadEvent type:" + type);
        executor.execute(new RequestTask(request, type));
    }

    public final static class RequestTask implements Runnable {
        private int type;
        private NetRequest request;

        public RequestTask(final NetRequest request, final int type) {
            this.request = request;
            this.type = type;
        }

        @Override
        public void run() {
            LogUtil.d(TAG, "run task");

            JSONObject object = new JSONObject();
            try {
                object.put("type", type);
                object.put("androidId", GhostUtils.androidId);
                object.put("imei", GhostUtils.imei);
                object.put("packageName", GhostUtils.packageName);
                object.put("model", Build.MODEL);
                object.put("brand", Build.BRAND);
                object.put("mac", GhostUtils.mac);
                object.put("imsi", GhostUtils.imsi);
                object.put("screenSize", GhostUtils.displayWidth
                        + "*" + GhostUtils.displayHeight);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            RequestParams requestParams = new RequestParams.Builder()
                    .setUrl(Constants.UPLOAD_EVENT_URL)
                    .setJsonParams(object)
                    .setMethod(RequestParams.METHOD_POST)
                    .create();
            request.executeRequest(requestParams);
        }
    }

}
