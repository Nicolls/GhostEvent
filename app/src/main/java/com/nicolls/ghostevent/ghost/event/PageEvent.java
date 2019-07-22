package com.nicolls.ghostevent.ghost.event;

import com.nicolls.ghostevent.ghost.utils.LogUtil;

import java.util.concurrent.atomic.AtomicBoolean;

import io.reactivex.Completable;

public class PageEvent extends BaseEvent {
    private static final String TAG = "PageEvent";

    public PageEvent() {
        this.name = TAG;
    }

    @Override
    public Completable exe(AtomicBoolean cancel) {
        return null;
    }

}
