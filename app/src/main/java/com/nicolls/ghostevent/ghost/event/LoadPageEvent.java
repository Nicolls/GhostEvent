package com.nicolls.ghostevent.ghost.event;

import com.nicolls.ghostevent.ghost.core.EventExecutor;
import com.nicolls.ghostevent.ghost.core.IWebTarget;
import com.nicolls.ghostevent.ghost.event.provider.LoadPageEventProvider;

import org.json.JSONException;
import org.json.JSONObject;

public class LoadPageEvent extends GroupEvent {
    private static final String TAG = "LoadPageEvent";
    private LoadPageEventProvider provider;

    public LoadPageEvent(IWebTarget target, EventExecutor.ExecuteCallBack executeCallBack, LoadPageEventProvider provider) {
        super(target, executeCallBack);
        this.provider = provider;
        addEvent(provider.getParams());

    }

    @Override
    public String getName() {
        return TAG;
    }

    @Override
    public String getDetail() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("group", true);
            jsonObject.put("url", provider.getUrl());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

}
