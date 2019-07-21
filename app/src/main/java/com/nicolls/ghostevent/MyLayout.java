package com.nicolls.ghostevent;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

import com.nicolls.ghostevent.ghost.LogUtil;

public class MyLayout extends LinearLayout {
    private static final String TAG="MyLayout";
    public MyLayout(Context context) {
        super(context);
    }

    public MyLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        LogUtil.d(TAG,"onInterceptTouchEvent ev:"+MotionEvent.actionToString(ev.getAction()));
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        LogUtil.d(TAG,"onTouchEvent ev:"+MotionEvent.actionToString(event.getAction()));
        return super.onTouchEvent(event);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        LogUtil.d(TAG,"dispatchTouchEvent ev:"+MotionEvent.actionToString(ev.getAction()));
        return super.dispatchTouchEvent(ev);
    }
}
