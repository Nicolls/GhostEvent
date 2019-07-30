package com.nicolls.ghostevent.ghost.request.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.nicolls.ghostevent.ghost.request.network.model.BaseResponse;

import java.io.Serializable;

public class ConfigModel extends BaseResponse {
    @JSONField
    public String url;
    @JSONField
    public boolean enable;

}
