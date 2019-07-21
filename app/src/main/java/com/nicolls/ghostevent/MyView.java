package com.nicolls.ghostevent;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.nicolls.ghostevent.ghost.LogUtil;

public class MyView extends View {
    private static final String TAG="MyView";

    public MyView(Context context) {
        super(context);
    }

    public MyView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
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
