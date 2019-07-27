package com.nicolls.ghostevent.ghost.event;

import android.os.Handler;
import android.view.MotionEvent;

import com.nicolls.ghostevent.ghost.core.ITarget;
import com.nicolls.ghostevent.ghost.utils.LogUtil;

import java.util.concurrent.atomic.AtomicBoolean;

import io.reactivex.Completable;

public class CancelEvent extends BaseEvent {
    private static final String TAG = "CancelEvent";
    private static final long EXECUTE_TIME_OUT = 30 * 1000; // 毫秒
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
        super(target);
        setName(TAG);
    }

    @Override
    public Completable exe(AtomicBoolean cancel) {
        return Completable.fromRunnable(new Runnable() {
            @Override
            public void run() {
                cancel.set(true);
                LogUtil.d(TAG, "trigger cancel");
            }
        });
    }

    @Override
    public long getExecuteTimeOut() {
        return EXECUTE_TIME_OUT;
    }
}
