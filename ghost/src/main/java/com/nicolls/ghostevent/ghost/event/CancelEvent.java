package com.nicolls.ghostevent.ghost.event;

import android.os.Handler;
import android.view.MotionEvent;

import com.nicolls.ghostevent.ghost.core.ITarget;
import com.nicolls.ghostevent.ghost.utils.Constants;
import com.nicolls.ghostevent.ghost.utils.LogUtil;

import org.json.JSONObject;

import java.util.concurrent.atomic.AtomicBoolean;

import io.reactivex.Completable;

public class CancelEvent extends BaseEvent {
    private static final String TAG = "CancelEvent";
    private static final ITarget paramTarget = new ITarget() {
        @Override
        public Handler getMainHandler() {
            return null;
        }

        @Override
        public Handler getEventHandler() {
            return null;
        }

        @Override
        public void doEvent(MotionEvent event) {

        }
    };
    public static final CancelEvent instance = new CancelEvent(paramTarget);

    private CancelEvent(ITarget target) {

    }

    @Override
    public Completable exe(AtomicBoolean cancel) {
        return Completable.fromRunnable(new Runnable() {
            @Override
            public void run() {
                LogUtil.d(TAG, "trigger cancel");
            }
        });
    }

    @Override
    public long getExecuteTimeOut() {
        return Constants.TIME_DEFAULT_CANCEL_WAIT_TIME;
    }

    @Override
    public String getName() {
        return TAG;
    }

    @Override
    public String getDescribe() {
        JSONObject jsonObject=new JSONObject();
        return jsonObject.toString();
    }
}
