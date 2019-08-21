package com.nicolls.ghostevent.ghost.request.network;

import com.nicolls.ghostevent.ghost.request.network.model.RequestParams;

import org.json.JSONObject;

/**
 * Created by nicolls on 18/11/16.
 */

public interface NetRequest {
    void executeRequest(RequestParams requestParams);
    interface RequestCallBack {
        void onSuccess(JSONObject response);

        void onFail(String message);
    }
}
