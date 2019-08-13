package com.nicolls.ghostevent.ghost.view;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;

import com.nicolls.ghostevent.ghost.core.EventExecutor;
import com.nicolls.ghostevent.ghost.core.RedirectHandler;
import com.nicolls.ghostevent.ghost.event.model.TouchPoint;
import com.nicolls.ghostevent.ghost.event.BaseEvent;
import com.nicolls.ghostevent.ghost.event.GroupEvent;
import com.nicolls.ghostevent.ghost.utils.LogUtil;

import java.nio.file.FileAlreadyExistsException;

public class GhostWebViewTriggerEvent implements View.OnTouchListener {
    private static final String TAG = "GhostWebViewTriggerEvent";
    private static final boolean DEBUG= false;
    // touch
    private float downX;
    private float downY;
    private float upX;
    private float upY;
    private int lastScrollY = 0;
    private GhostWebView ghostWebView;
    private GroupEvent recordEvent;
    private RedirectHandler redirectHandler;

    // scroll
    private ScrollState state = ScrollState.STATE_NONE;
    private static final int MSG_SCROLL_IDLE = 101;
    private static final int MSG_TOUCH_IDLE = 102;
    private static final long CHECK_STATE_TIME = 200; // 毫秒
    private boolean isTouch = false;
    private Handler mainHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if ((msg.what == MSG_SCROLL_IDLE || msg.what == MSG_TOUCH_IDLE)
                    && state != ScrollState.STATE_IDLE) {
                onScrollStateChanged(ScrollState.STATE_IDLE);
            }
        }
    };

    public GhostWebViewTriggerEvent(GhostWebView ghostWebView, RedirectHandler redirectHandler,
                                    EventExecutor.ExecuteCallBack callBack) {
        this.ghostWebView = ghostWebView;
        this.redirectHandler = redirectHandler;
        this.recordEvent = new GroupEvent(ghostWebView, callBack);
        init();
    }

    private void init() {
        onScrollStateChanged(ScrollState.STATE_NONE);
    }

    @Override
    public boolean onTouch(View v, MotionEvent ev) {
        if(DEBUG){
            LogUtil.d(TAG, "onTouch ev:"
                    + MotionEvent.actionToString(ev.getAction()) + " x:" + ev.getX() + " y:" + ev.getY());
        }
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = ev.getX();
                downY = ev.getY();
                lastScrollY = ghostWebView.getScrollY();
                // scroll
                isTouch = true;
                mainHandler.removeMessages(MSG_SCROLL_IDLE);
                mainHandler.removeMessages(MSG_TOUCH_IDLE);
                onScrollStateChanged(ScrollState.STATE_NONE);
                break;
            case MotionEvent.ACTION_MOVE:

                break;
            case MotionEvent.ACTION_CANCEL:
                // scroll
                mainHandler.removeMessages(MSG_TOUCH_IDLE);
                mainHandler.sendEmptyMessageDelayed(MSG_TOUCH_IDLE, CHECK_STATE_TIME * 2);
                isTouch = false;
                break;
            case MotionEvent.ACTION_UP:
                upX = ev.getX();
                upY = ev.getY();
                if(DEBUG){
                    LogUtil.d(TAG, "dispatchTouchEvent scrollY " + ghostWebView.getScrollY());
                }
                // scroll
                mainHandler.removeMessages(MSG_TOUCH_IDLE);
                mainHandler.sendEmptyMessageDelayed(MSG_TOUCH_IDLE, CHECK_STATE_TIME * 2);
                isTouch = false;
                break;
        }
        return false;
    }

    private void onScrollStateChanged(ScrollState state) {
        LogUtil.d(TAG, "onScrollStateChanged " + state);
        this.state = state;
        ghostWebView.onScrollStateChanged(state);
    }


    public void onScrollChanged(int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
        if(DEBUG){
            LogUtil.d(TAG, "onScrollChanged scrollY-oldScrollY:" + scrollY + "-" + oldScrollY);
        }
        if (this.state != ScrollState.STATE_SCROLLING) {
            onScrollStateChanged(ScrollState.STATE_SCROLLING);
        }
        if (!isTouch) {
            mainHandler.removeMessages(MSG_TOUCH_IDLE);
            mainHandler.removeMessages(MSG_SCROLL_IDLE);
            mainHandler.sendEmptyMessageDelayed(MSG_SCROLL_IDLE, CHECK_STATE_TIME);
        }
    }

    public void startRecord() {
        recordEvent.removeAllEvents();
    }

    public BaseEvent getRecordEvent() {
        return recordEvent;
    }

}
