package com.nicolls.ghostevent.ghost.event.model;

import com.nicolls.ghostevent.ghost.core.IWebTarget;
import com.nicolls.ghostevent.ghost.core.RedirectHandler;
import com.nicolls.ghostevent.ghost.utils.Constants;
import com.nicolls.ghostevent.ghost.utils.LogUtil;

import java.util.concurrent.Semaphore;

public class LoadPageRedirectListener implements RedirectHandler.RedirectListener {
    private static final String TAG = "LoadPageRedirectListener";
    private static final int MAX_RECEIVE_SUCCESS_TIMES = 2;
    private final IWebTarget target;
    private final Semaphore semaphore;
    private int maxReceiveSuccessTimes = MAX_RECEIVE_SUCCESS_TIMES;
    private int receiveSuccessTimes;

    public LoadPageRedirectListener(IWebTarget target, Semaphore semaphore) {
        this.target = target;
        this.semaphore = semaphore;
    }

    public void setMaxReceiveSuccessTimes(int maxReceiveSuccessTimes) {
        LogUtil.d(TAG, "setMaxReceiveSuccessTimes " + maxReceiveSuccessTimes);
        this.maxReceiveSuccessTimes = maxReceiveSuccessTimes;
    }

    private final Runnable releaseTask = new Runnable() {
        @Override
        public void run() {
            LogUtil.d(TAG, "semaphore release");
            semaphore.release();
        }
    };

    @Override
    public void onStart() {
        receiveSuccessTimes = 0;
        LogUtil.d(TAG, "redirect load onStart");
    }

    @Override
    public void onSuccess() {
        LogUtil.d(TAG, "redirect load onSuccess");
        receiveSuccessTimes++;
        if (receiveSuccessTimes > maxReceiveSuccessTimes) {
            LogUtil.w(TAG, "receiveSuccessTimes > max (" + maxReceiveSuccessTimes + ") break!");
            return;
        }
        target.getMainHandler().removeCallbacks(releaseTask);
        target.getMainHandler().postDelayed(releaseTask, Constants.TIME_NOTIFY_PAGE_LOADED_DELAY);
    }

    public long getLoadPageRedirectTimeOut() {
        return Constants.TIME_NOTIFY_PAGE_LOADED_DELAY * maxReceiveSuccessTimes;
    }

    @Override
    public void onFail() {
        LogUtil.d(TAG, "redirect load onFail");
    }
}
