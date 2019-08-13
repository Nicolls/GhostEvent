package com.nicolls.ghostevent.ghost.request.network.model;

/**
 * author:mengjiankang
 * date:2018/11/14
 * <p>
 * http连接服务器头设置
 * </p>
 */
public class HttpParams {

    private static final int DEFAULT_CONNECT_TIMEOUT = 60; // 秒
    private static final int DEFAULT_READ_TIMEOUT = 30; // 秒
    private static final int DEFAULT_WRITE_TIMEOUT = 30; // 秒
    private int readTimeOut;

    private int writeTimeOut;

    private int connectTimeOut;

    public HttpParams() {
        this.readTimeOut = DEFAULT_READ_TIMEOUT;
        this.writeTimeOut = DEFAULT_WRITE_TIMEOUT;
        this.connectTimeOut = DEFAULT_CONNECT_TIMEOUT;
    }

    public int getReadTimeOut() {
        return readTimeOut;
    }

    public int getWriteTimeOut() {
        return writeTimeOut;
    }

    public int getConnectTimeOut() {
        return connectTimeOut;
    }

    public static class Builder {
        private HttpParams urlParams;

        public Builder() {
            urlParams = new HttpParams();
        }

        public Builder setReadTimeOut(int timeOut) {
            urlParams.readTimeOut = timeOut;
            return this;
        }

        public Builder setWriteTimeOut(int timeOut) {
            urlParams.writeTimeOut = timeOut;
            return this;
        }

        public Builder setConnectTimeOut(int timeOut) {
            urlParams.connectTimeOut = timeOut;
            return this;
        }

        public HttpParams create() {
            return urlParams;
        }
    }

    @Override
    public String toString() {
        return "HttpParams{" +
                ", readTimeOut=" + readTimeOut +
                ", writeTimeOut=" + writeTimeOut +
                ", connectTimeOut=" + connectTimeOut +
                '}';
    }
}
