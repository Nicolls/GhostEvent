package com.nicolls.ghostevent.ghost.event.behavior;

import com.nicolls.ghostevent.ghost.core.IWebTarget;
import com.nicolls.ghostevent.ghost.core.RedirectHandler;
import com.nicolls.ghostevent.ghost.utils.Constants;
import com.nicolls.ghostevent.ghost.utils.LogUtil;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class LoadWebEventBehavior implements IEventBehavior<Boolean> {
    private static final String TAG="LoadWebEventBehavior";

    private static final long CHECK_WAIT_TIME=2000;
    private RedirectHandler redirectHandler;
    private IWebTarget target;
    private final Semaphore semaphore=new Semaphore(0);
    public LoadWebEventBehavior(IWebTarget target, RedirectHandler redirectHandler){
        this.target=target;
        this.redirectHandler=redirectHandler;
    }
    @Override
    public Boolean onStart(AtomicBoolean cancel) {
        redirectHandler.unRegisterRedirectListener(listener);
        redirectHandler.registerRedirectListener(listener);
        return true;
    }

    @Override
    public void onEnd(AtomicBoolean cancel) {
        LogUtil.d(TAG,"event end start listen page");

        try {
            target.getEventHandler().postDelayed(checkLoadPage,CHECK_WAIT_TIME);
            boolean isOk=semaphore.tryAcquire(getTimeOut(),TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        redirectHandler.unRegisterRedirectListener(listener);
    }

    private final Runnable checkLoadPage=new Runnable() {
        @Override
        public void run() {
            semaphore.release();
        }
    };

    @Override
    public long getTimeOut() {
        return Constants.TIME_DEFAULT_LOAD_PAGE_WAIT_TIME;
    }

    private RedirectHandler.RedirectListener listener=new RedirectHandler.RedirectListener() {
        @Override
        public void onStart(String url) {
            LogUtil.d(TAG,"page load onStart");
            target.getEventHandler().removeCallbacks(checkLoadPage);
        }

        @Override
        public void onSuccess(String url) {
            LogUtil.d(TAG,"page load success");
            redirectHandler.unRegisterRedirectListener(this);
            semaphore.release();
        }

        @Override
        public void onFail() {
            LogUtil.d(TAG,"page load onFail");
            semaphore.release();
        }
    };
}
