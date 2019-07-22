package com.nicolls.ghostevent.ghost.event;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class RedirectHandler {

    private final List<RedirectListener> redirectListeners=new CopyOnWriteArrayList<>();

    public interface RedirectListener {
        void onStart();

        void onSuccess();

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

    public void notifyStart(){
        for (RedirectListener listener:redirectListeners){
            listener.onStart();
        }
    }

    public void notifySuccess(){
        for (RedirectListener listener:redirectListeners){
            listener.onSuccess();
        }
    }

    public void notifyFail(){
        for (RedirectListener listener:redirectListeners){
            listener.onFail();
        }
    }

}
