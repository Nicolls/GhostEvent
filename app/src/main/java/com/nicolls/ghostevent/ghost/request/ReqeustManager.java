package com.nicolls.ghostevent.ghost.request;

public class ReqeustManager {
    private static final ReqeustManager instance = new ReqeustManager();

    public static final ReqeustManager getInstance() {
        return instance;
    }

    public interface CallBack{
        void onInitSuccess();
        void onInitFail();
        void onReceiveEvent();
    }

}
