package com.nicolls.ghostevent.ghost.core;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class RedirectHandler {

    private final List<RedirectListener> redirectListeners=new CopyOnWriteArrayList<>();

    public interface RedirectListener {
        void onStart(String url);

        void onSuccess(String url);

        void onFail();
    }

    public void registerRedirectListener(RedirectListener listener){
        if(!redirectListeners.contains(listener)){
            redirectListeners.add(listener);
        }
    }

    public void unRegisterRedirectListener(RedirectListener listener){
        redirectListeners.remove(listener);
    }

    public void notifyStart(String url){
        for (RedirectListener listener:redirectListeners){
            listener.onStart(url);
        }
    }

    public void notifySuccess(String url){
        for (RedirectListener listener:redirectListeners){
            listener.onSuccess(url);
        }
    }

    public void notifyFail(){
        for (RedirectListener listener:redirectListeners){
            listener.onFail();
        }
    }

}
