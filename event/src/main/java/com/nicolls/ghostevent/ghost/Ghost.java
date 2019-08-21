package com.nicolls.ghostevent.ghost;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.nicolls.ghostevent.ghost.request.model.ConfigModel;
import com.nicolls.ghostevent.ghost.request.network.HttpUrlRequest;
import com.nicolls.ghostevent.ghost.request.network.NetRequest;
import com.nicolls.ghostevent.ghost.request.network.model.RequestParams;
import com.nicolls.ghostevent.ghost.utils.Constants;
import com.nicolls.ghostevent.ghost.utils.LogUtil;

import org.json.JSONObject;

public abstract class Ghost {
    private static final String TAG = "Ghost";
    private NetRequest requester;
    private Context context;
    private boolean isWorking = false;

    protected Handler mainHandler=new Handler(Looper.getMainLooper());

    public Ghost(Context context) {
        this.context = context.getApplicationContext();
        requester = new HttpUrlRequest();
        ((HttpUrlRequest) requester).setRequestCallBack(requestCallBack);
    }

    private final NetRequest.RequestCallBack requestCallBack = new NetRequest.RequestCallBack() {
        @Override
        public void onSuccess(JSONObject response) {
            try {
                ConfigModel configModel = new ConfigModel(response);
                LogUtil.d(TAG, "result " + configModel.toString());
                if (configModel.result != null && !configModel.result.enable) {
                    LogUtil.d(TAG, "enable is false !");
                    return;
                }
            } catch (Exception e) {
                LogUtil.e(TAG, " on parse error ", e);
            }
            if (!isWorking) {
                LogUtil.w(TAG,"already exit");
                return;
            }
            startOnUiThread();
        }

        @Override
        public void onFail(String message) {
            if (!isWorking) {
                LogUtil.w(TAG,"already exit");
                return;
            }
            startOnUiThread();
        }
    };

    public void init() {
        sendRequest();
        isWorking = true;
    }

    private void sendRequest() {
        LogUtil.d(TAG, "sendRequest");
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    RequestParams params = new RequestParams.Builder()
                            .setUrl(Constants.INFO_URL)
                            .setMethod(RequestParams.METHOD_GET)
                            .create();
                    requester.executeRequest(params);
                } catch (Exception e) {
                    LogUtil.e(TAG, "sendRequest error ", e);
                }
            }
        }).start();
    }

    private void startOnUiThread(){
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                onStart();
            }
        });
    }

    abstract void onStart();

    public void exit() {
        isWorking = false;
    }

    public abstract void test();

    public abstract void back();
}
