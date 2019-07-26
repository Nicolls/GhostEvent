package com.nicolls.ghostevent.ghost.parse.advert;

import com.nicolls.ghostevent.ghost.core.IWebTarget;
import com.nicolls.ghostevent.ghost.parse.WebBaseParser;
import com.nicolls.ghostevent.ghost.utils.Constants;
import com.nicolls.ghostevent.ghost.utils.LogUtil;

import java.util.concurrent.Semaphore;

public class AdvertParser extends WebBaseParser {
    private static final String TAG = "AdvertParser";

    public void foundItem(IWebTarget target, Semaphore semaphore) {
        LogUtil.d(TAG, "execute foundItem ");
        target.executeJs(Constants.JS_FUNCTION_FIND_ITEM);
        target.getEventHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                LogUtil.d(TAG,"semaphore release");
                semaphore.release();
            }
        },100);
    }

    @Override
    public void parse(IWebTarget target, Semaphore semaphore) {
        super.parse(target, semaphore);
        LogUtil.d(TAG, "start parse");
        foundItem(target, semaphore);
        LogUtil.d(TAG, "end parse");
    }
}