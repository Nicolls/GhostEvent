package com.nicolls.ghostevent.ghost.request.network;

import com.nicolls.ghostevent.ghost.request.network.model.BaseResponse;
import com.nicolls.ghostevent.ghost.request.network.model.RequestParams;

/**
 * Created by nicolls on 18/11/16.
 */

public interface NetRequest {
    <T extends BaseResponse> T executeRequest(RequestParams requestParams, Class<T> cls);

    void enqueueRequest(RequestParams requestParams);

    <T extends BaseResponse> void enqueueRequest(RequestParams requestParams, Class<T> cls, RequestCallBack<T> callBack);

    interface RequestCallBack<Res extends BaseResponse> {
        void onSuccess(Res response);

        void onFail(String message);
    }
}
