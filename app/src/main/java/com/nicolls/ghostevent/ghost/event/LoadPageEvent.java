package com.nicolls.ghostevent.ghost.event;

import com.nicolls.ghostevent.ghost.core.EventExecutor;
import com.nicolls.ghostevent.ghost.core.IWebTarget;
import com.nicolls.ghostevent.ghost.event.provider.LoadPageEventProvider;

public class LoadPageEvent extends GroupEvent {
    private static final String TAG = "LoadPageEvent";

    public LoadPageEvent(IWebTarget target, EventExecutor.ExecuteCallBack executeCallBack, LoadPageEventProvider provider) {
        super(target, executeCallBack);
        addEvent(provider.getParams());

    }

}
