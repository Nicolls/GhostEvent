package com.nicolls.ghostevent.ghost.request.network.model;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * author:mengjiankang
 * date:2018/11/14
 * email:851778509@qq.com
 * <p>
 * </p>
 */
public class RequestParams {

    public static final String METHOD_POST = "POST";
    public static final String METHOD_GET = "GET";
    public static final String METHOD_JSON = "JSON";
    public static final int READ_BUFFER_SIZE = 1024; // Bytes 字节
    public static final int WRITE_BUFFER_SIZE = 1024; // Bytes 字节

    private String url;

    private String method;

    private Map<String, String> headers;

    private Map<String, String> params;

    private JSONObject jsonParams;

    private boolean isSynchronized = false;

    private int readBufferSize; // 单位：字节 (Bytes)

    private int writeBufferSize; // 单位：字节 (Bytes)

    private RequestParams() {
        this("");
    }

    public RequestParams(String url) {
        this(url, new HashMap<String,String>());
    }

    public RequestParams(String url, Map<String, String> params) {
        this(url, params, new HashMap<String,String>());
    }

    public RequestParams(String url, Map<String, String> params, Map<String, String> headers) {
        this.url = url;
        this.params = params;
        this.readBufferSize = READ_BUFFER_SIZE;
        this.writeBufferSize = WRITE_BUFFER_SIZE;
        this.method = METHOD_GET;
        this.headers = headers;
    }

    public boolean isSynchronized() {
        return isSynchronized;
    }

    public String getUrl() {
        return url;
    }

    public String getMethod() {
        return method;
    }

    public int getReadBufferSize() {
        return readBufferSize;
    }

    public int getWriteBufferSize() {
        return writeBufferSize;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public Map<String, String> getParams() {
        return new HashMap<>(params);
    }

    public JSONObject getJsonParams(){
        return jsonParams;
    }

    public static class Builder {
        private RequestParams urlParams;

        public Builder() {
            urlParams = new RequestParams();
        }

        public Builder setUrl(String url) {
            urlParams.url = url;
            return this;
        }

        public Builder setMethod(String method) {
            urlParams.method = method;
            return this;
        }

        public Builder setHeaders(Map<String, String> header) {
            urlParams.headers = header;
            return this;
        }

        public Builder setSynchronized(boolean aSynchronized) {
            urlParams.isSynchronized = aSynchronized;
            return this;
        }


        public Builder addHeader(String key, String value) {
            urlParams.headers.put(key, value);
            return this;
        }

        public Builder setParams(Map<String, String> params) {
            urlParams.params = params;
            return this;
        }

        public Builder addParams(String key, String value) {
            urlParams.params.put(key, value);
            return this;
        }

        public Builder setJsonParams(JSONObject jsonParams) {
            urlParams.jsonParams = jsonParams;
            return this;
        }

        public Builder setReadBufferSize(int readBufferSize) {
            urlParams.readBufferSize = readBufferSize;
            return this;
        }

        public Builder setWriteBufferSize(int writeBufferSize) {
            urlParams.writeBufferSize = writeBufferSize;
            return this;
        }

        public RequestParams create() {
            return urlParams;
        }
    }


}
