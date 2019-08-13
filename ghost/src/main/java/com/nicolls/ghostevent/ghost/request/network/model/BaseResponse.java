package com.nicolls.ghostevent.ghost.request.network.model;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;

/**
 * author:mengjiankang
 * date:2018/11/18
 * <p>
 * </p>
 */
public class BaseResponse implements Serializable {
    @JSONField(serialize = false)
    public String rawMessage;
}
