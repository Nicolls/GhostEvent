package com.nicolls.ghostevent.ghost.request.network;

import com.alibaba.fastjson.JSON;
import com.nicolls.ghostevent.ghost.request.network.model.BaseResponse;
import com.nicolls.ghostevent.ghost.request.network.model.HttpParams;
import com.nicolls.ghostevent.ghost.request.network.model.RequestParams;
import com.nicolls.ghostevent.ghost.utils.LogUtil;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by nicolls on 18/11/16.
 */

public class OkHttpRequest implements NetRequest {

    private static final String TAG = "OkHttpRequest";
    private OkHttpClient okHttpClient;
    private HttpParams httpParams;

    public OkHttpRequest() {
        init();
    }

    public OkHttpRequest(OkHttpClient okHttpClient) {
        this.okHttpClient = okHttpClient;
    }

    public OkHttpRequest(HttpParams httpParams) {
        this.httpParams = httpParams;
        init();
    }

    private void init() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        if (httpParams != null) {
            builder.readTimeout(httpParams.getReadTimeOut(), TimeUnit.SECONDS)
                    .writeTimeout(httpParams.getWriteTimeOut(), TimeUnit.SECONDS)
                    .connectTimeout(httpParams.getConnectTimeOut(), TimeUnit.SECONDS);
        }
        okHttpClient = builder.build();

    }


    @Override
    public <T extends BaseResponse> T executeRequest(RequestParams requestParams, Class<T> cls) {
        try {
            Response response = okHttpClient.newCall(getRequest(requestParams)).execute();
            String str = response.body().string();
            LogUtil.d(TAG, "executeRequest completed "+str);
            T object = JSON.parseObject(str, cls);
            object.rawMessage = str;
            return object;
        } catch (IOException e) {
            e.printStackTrace();
            LogUtil.e(TAG, "executeRequest error" + (e == null ? "null" : e.getMessage()));
        }
        return null;
    }

    @Override
    public void enqueueRequest(RequestParams requestParams) {
        okHttpClient.newCall(getRequest(requestParams))
                .enqueue(new OkHttpRequestCallBack(requestParams, null, null));
    }

    @Override
    public <T extends BaseResponse> void enqueueRequest(RequestParams requestParams, Class<T> cls, RequestCallBack<T> callBack) {
        okHttpClient.newCall(getRequest(requestParams))
                .enqueue(new OkHttpRequestCallBack<>(requestParams, callBack, cls));
    }

    private Request getRequest(RequestParams requestParams) {
        LogUtil.d(TAG, "request url:" + requestParams.getUrl());
        LogUtil.d(TAG, "params:" + requestParams.getParams().toString());
        Request.Builder builder = new Request.Builder();
        // headers
        Map<String, String> headers = requestParams.getHeaders();
        for (String key : headers.keySet()) {
            builder.addHeader(key, headers.get(key));
        }

        //params
        Map<String, String> params = requestParams.getParams();
        if (RequestParams.METHOD_GET.equals(requestParams.getMethod())) {
            // 发送get请求
            HttpUrl.Builder urlBuilder = HttpUrl.parse(requestParams.getUrl())
                    .newBuilder();
            for (String key : params.keySet()) {
                urlBuilder.addQueryParameter(key, params.get(key));
            }
            builder.url(urlBuilder.build());
        } else {
            // 发送post请求
            builder.url(requestParams.getUrl());
            FormBody.Builder formBuild = new FormBody.Builder();
            for (String key : params.keySet()) {
                formBuild.add(key, params.get(key));
            }
            builder.post(formBuild.build());
        }

        return builder.build();
    }

    private class OkHttpRequestCallBack<Res extends BaseResponse> implements Callback {
        private RequestParams requestParams;
        private RequestCallBack<Res> requestCallBack;
        private Class<Res> cls;

        public OkHttpRequestCallBack(RequestParams params, RequestCallBack<Res> callBack, Class<Res> cls) {
            this.requestParams = params;
            this.requestCallBack = callBack;
            this.cls = cls;
        }

        @Override
        public void onFailure(Call call, IOException e) {
            if (requestCallBack == null) {
                return;
            }
            requestCallBack.onFail(e.toString());
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            if (requestCallBack == null) {
                return;
            }
            String str = response.body().string();
            Res object = JSON.parseObject(str, cls);
            object.rawMessage = str;
            requestCallBack.onSuccess(object);
        }
    }


}
