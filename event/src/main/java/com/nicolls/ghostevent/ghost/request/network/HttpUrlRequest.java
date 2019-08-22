package com.nicolls.ghostevent.ghost.request.network;

import com.nicolls.ghostevent.ghost.request.network.model.HttpParams;
import com.nicolls.ghostevent.ghost.request.network.model.RequestParams;
import com.nicolls.ghostevent.ghost.utils.LogUtil;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by nicolls on 18/11/16.
 */

public class HttpUrlRequest implements NetRequest {

    private static final String TAG = "HttpUrlRequest";
    private HttpParams httpParams;
    private RequestCallBack requestCallBack;

    public HttpUrlRequest() {
    }

    public HttpUrlRequest(HttpParams httpParams) {
        this.httpParams = httpParams;
    }

    @Override
    public void executeRequest(RequestParams requestParams) {
        try {
            URL url = new URL(requestParams.getUrl());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(requestParams.getMethod());
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setUseCaches(false);
            connection.setRequestProperty("Content-Type", "application/json;charset=utf-8");//设置参数类型是json格式
            connection.connect();
            if (requestParams.getJsonParams() != null) {
                String body = requestParams.getJsonParams().toString();
                LogUtil.d(TAG, "request body " + body);
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream(), "UTF-8"));
                writer.write(body);
                writer.close();
            }
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                InputStream inputStream = connection.getInputStream();
                String result = getString(inputStream);
                LogUtil.d(TAG, "response:" + result);
                inputStream.close();
                JSONObject jsonObject = new JSONObject(result);
                if (requestCallBack != null) {
                    requestCallBack.onSuccess(jsonObject);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.e(TAG, "executeRequest error ", e);
            if (requestCallBack != null) {
                requestCallBack.onFail(e == null ? "null" : e.getMessage());
            }
        }
    }


    private String getString(InputStream inputStream) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8")); // 实例化输入流，并获取网页代码
            String s;
            StringBuilder sb = new StringBuilder();
            while ((s = reader.readLine()) != null) {
                sb.append(s);
            }
            reader.close();
            return sb.toString();
        } catch (Exception e) {
            LogUtil.e(TAG, "getString error ", e);
        }
        return "";
    }

    public void setRequestCallBack(RequestCallBack requestCallBack) {
        this.requestCallBack = requestCallBack;
    }
}
