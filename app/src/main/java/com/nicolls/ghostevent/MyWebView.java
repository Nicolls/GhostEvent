package com.nicolls.ghostevent;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.webkit.WebView;

import com.nicolls.ghostevent.ghost.DisplayUtils;
import com.nicolls.ghostevent.ghost.LogUtil;

public class MyWebView extends WebView {
    private static final String TAG="MyWebView";
    private static final String DEFAULT_URL = "http://www.lskm520.com";

    public MyWebView(Context context) {
        super(context);
        init();
    }

    public MyWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MyWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        LogUtil.d(TAG,"onInterceptTouchEvent ev:"+MotionEvent.actionToString(ev.getAction()));
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        LogUtil.d(TAG,"onTouchEvent ev:"+MotionEvent.actionToString(event.getAction()));
        return super.onTouchEvent(event);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        LogUtil.d(TAG,"dispatchTouchEvent ev:"+MotionEvent.actionToString(ev.getAction()));
        LogUtil.d(TAG,"dispatchTouchEvent x-y :"+ev.getX()+"-"+ev.getY() +" rawX-rawY:"+ev.getRawX()+"-"+ev.getRawY());
        return super.dispatchTouchEvent(ev);
    }

    private void init(){
        LogUtil.d(TAG,"width:"+ DisplayUtils.getDisplaySize(getContext()).x+"-height:"+DisplayUtils.getDisplaySize(getContext()).y);
        loadUrl(DEFAULT_URL);

    }
}
