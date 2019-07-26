package com.nicolls.ghostevent.ghost.parse;

import com.nicolls.ghostevent.ghost.utils.Constants;
import com.nicolls.ghostevent.ghost.core.IWebTarget;

import java.util.concurrent.Semaphore;

public abstract class WebBaseParser implements IWebParser {

    private static final String TAG="WebBaseParser";
    public void parse(IWebTarget target, Semaphore semaphore) {
        currentPageHtml(target);
    }

    public void currentPageHtml(IWebTarget target) {
        // fetch html
        target.executeJs(Constants.JS_CURRENT_PAGE_HTML);
    }
}
