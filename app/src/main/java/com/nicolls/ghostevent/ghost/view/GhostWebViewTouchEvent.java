package com.nicolls.ghostevent.ghost.view;

import android.view.MotionEvent;
import android.view.View;

import com.nicolls.ghostevent.ghost.event.BaseEvent;
import com.nicolls.ghostevent.ghost.event.ClickRedirectEvent;
import com.nicolls.ghostevent.ghost.event.GroupEvent;
import com.nicolls.ghostevent.ghost.core.RedirectHandler;
import com.nicolls.ghostevent.ghost.event.ScrollVerticalEvent;
import com.nicolls.ghostevent.ghost.event.TouchPoint;
import com.nicolls.ghostevent.ghost.utils.LogUtil;

public class GhostWebViewTouchEvent implements View.OnTouchListener {
    private static final String TAG = "GhostWebViewTouchEvent";
    private float downX;
    private float downY;
    private float upX;
    private float upY;
    private int lastScrollY = 0;
    private GhostWebView ghostWebView;
    private GroupEvent recordEvent;
    private RedirectHandler redirectHandler;

    public GhostWebViewTouchEvent(GhostWebView ghostWebView, RedirectHandler redirectHandler,
                                  GroupEvent recordEvent) {
        this.ghostWebView = ghostWebView;
        this.redirectHandler = redirectHandler;
        this.recordEvent = recordEvent;
    }

    @Override
    public boolean onTouch(View v, MotionEvent ev) {
        LogUtil.d(TAG, "onTouch ev:"
                + MotionEvent.actionToString(ev.getAction()) + " x:" + ev.getY() + " y:" + ev.getY());
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = ev.getX();
                downY = ev.getY();
                lastScrollY = ghostWebView.getScrollY();
                break;
            case MotionEvent.ACTION_MOVE:

                break;
            case MotionEvent.ACTION_UP:
                upX = ev.getX();
                upY = ev.getY();
                LogUtil.d(TAG, "dispatchTouchEvent scrollY " + ghostWebView.getScrollY());
                if (ghostWebView.isRecord()) {
                    final float moveXSize = Math.abs(upX - downX);
                    final float moveYSize = Math.abs(upY - downY);
                    if (moveXSize < 5 && moveYSize < 5) {
                        // click
                        BaseEvent clickRedirect = new ClickRedirectEvent(redirectHandler, ghostWebView, TouchPoint.obtainClick(upX, upY));
                        LogUtil.d(TAG, "dispatchTouchEvent add click:" + clickRedirect.toString());
                        recordEvent.addEvent(clickRedirect);
                    } else {
                        // scroll
                        ScrollVerticalEvent scroll = new ScrollVerticalEvent(ghostWebView, lastScrollY, ghostWebView.getScrollY());
                        recordEvent.addEvent(scroll);
                        LogUtil.d(TAG, "dispatchTouchEvent add scroll:" + scroll.toString());

                    }
                }
                break;
        }
        return false;
    }
}
