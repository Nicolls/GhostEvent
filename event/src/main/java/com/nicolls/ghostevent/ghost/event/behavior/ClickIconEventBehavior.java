package com.nicolls.ghostevent.ghost.event.behavior;

import com.nicolls.ghostevent.ghost.core.IWebTarget;
import com.nicolls.ghostevent.ghost.core.RedirectHandler;
import com.nicolls.ghostevent.ghost.parse.IWebParser;
import com.nicolls.ghostevent.ghost.parse.ParseManager;
import com.nicolls.ghostevent.ghost.utils.Constants;
import com.nicolls.ghostevent.ghost.utils.LogUtil;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class ClickIconEventBehavior implements IEventBehavior<Boolean> {
    private static final String TAG="ClickIconEventBehavior";
    private static final long CHECK_WAIT_TIME = 2000;
    private RedirectHandler redirectHandler;
    private IWebTarget target;
    private final Semaphore semaphore = new Semaphore(0);
    private final IWebParser webParser;

    public ClickIconEventBehavior(IWebTarget target, RedirectHandler redirectHandler, IWebParser parser) {
        this.target = target;
        this.redirectHandler = redirectHandler;
        this.webParser = parser;
    }

    @Override
    public Boolean onStart(AtomicBoolean cancel) {
        redirectHandler.registerRedirectListener(listener);
        ParseManager.getInstance().clearViewNodes();
        target.getMainHandler().post(new Runnable() {
            @Override
            public void run() {
                LogUtil.d(TAG,"onStart parse");
                webParser.parse(target, semaphore);
            }
        });
        boolean isOK=false;
        try {
            LogUtil.d(TAG,"semaphore tryAcquire");
            isOK=semaphore.tryAcquire(webParser.getParsedDelay() + 100, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        LogUtil.d(TAG,"parse completed semaphore Acquire "+isOK);
        return true;
    }

    @Override
    public void onEnd(AtomicBoolean cancel) {
        LogUtil.d(TAG,"onEnd wait page");
        boolean isOk=false;
        try {
            target.getMainHandler().postDelayed(checkLoadPage, CHECK_WAIT_TIME);
            isOk = semaphore.tryAcquire(getTimeOut(), TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        LogUtil.d(TAG,"onEnd get page load completed semaphore acquire "+isOk);
        redirectHandler.unRegisterRedirectListener(listener);
    }

    private final Runnable checkLoadPage = new Runnable() {
        @Override
        public void run() {
            semaphore.release();
        }
    };

    @Override
    public long getTimeOut() {
        return Constants.TIME_DEFAULT_LOAD_PAGE_WAIT_TIME + webParser.getParsedDelay();
    }

    private RedirectHandler.RedirectListener listener = new RedirectHandler.RedirectListener() {
        @Override
        public void onStart(String url) {
            LogUtil.d(TAG,"page load onStart");
            target.getMainHandler().removeCallbacks(checkLoadPage);
        }

        @Override
        public void onSuccess(String url) {
            LogUtil.d(TAG,"page load success");
            redirectHandler.unRegisterRedirectListener(this);
            semaphore.release();
        }

        @Override
        public void onFail() {
            LogUtil.d(TAG,"page load fail");
            semaphore.release();
        }
    };
}
