package com.nicolls.ghostevent.ghost.event.behavior;

import android.webkit.WebView;

import com.nicolls.ghostevent.ghost.core.IWebTarget;
import com.nicolls.ghostevent.ghost.event.model.Line;
import com.nicolls.ghostevent.ghost.parse.IWebParser;
import com.nicolls.ghostevent.ghost.parse.ParseManager;
import com.nicolls.ghostevent.ghost.parse.model.ViewNode;
import com.nicolls.ghostevent.ghost.utils.LogUtil;

import java.util.Random;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class ScrollReadMoreEventBehavior implements IEventBehavior<Line> {
    private static final String TAG = "ScrollReadMoreEventBehavior";
    private IWebTarget target;
    private final Semaphore semaphore = new Semaphore(0);
    private final IWebParser webParser;

    public ScrollReadMoreEventBehavior(IWebTarget target, IWebParser parser) {
        this.target = target;
        this.webParser = parser;
    }

    @Override
    public Line onStart(AtomicBoolean cancel) {
        target.getMainHandler().post(new Runnable() {
            @Override
            public void run() {
                LogUtil.d(TAG, "onStart parse");
                webParser.parse(target, semaphore);
            }
        });
        boolean isOK = false;
        try {
            LogUtil.d(TAG, "semaphore tryAcquire");
            isOK = semaphore.tryAcquire(webParser.getParsedDelay() + 100, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        LogUtil.d(TAG, "parse completed semaphore Acquire " + isOK);

        ViewNode readMore = ParseManager.getInstance().getReadMore();
        if (readMore == null) {
            LogUtil.w(TAG, "not found read more node");
            return null;
        }
        LogUtil.d(TAG, "read more:" + readMore.toString());
        // get close advert
        final WebView webView = (WebView) target;

        final int webViewHeight = webView.getHeight();
        final int centerHeight = webViewHeight / 2;
        final int scrollY = webView.getScrollY();
        final int nodeTop = (int) readMore.top;
        LogUtil.d(TAG, "webViewHeight:" + webViewHeight + " scrollY:" + scrollY);
        int from = scrollY;
        int to = scrollY;
        if (nodeTop < 0) {
            to += nodeTop;
            to -= centerHeight;
        } else if (nodeTop >= 0 && nodeTop <= centerHeight) {
            to -= (centerHeight - nodeTop);
        } else if (nodeTop > centerHeight && nodeTop <= webViewHeight) {
            Random random=new Random();
            to += (nodeTop - centerHeight + random.nextInt(centerHeight/4));
        } else {
            Random random=new Random();
            int h=random.nextInt(centerHeight/2)+centerHeight/3;
            to += nodeTop - webViewHeight + h;
        }
        LogUtil.d(TAG,"line from "+from+" to "+to);
        return new Line(from, to);
    }

    @Override
    public Line onEnd(AtomicBoolean cancel) {
        LogUtil.d(TAG, "onEnd wait");
        return null;
    }

    @Override
    public long getTimeOut() {
        return 100 + webParser.getParsedDelay();
    }

}
