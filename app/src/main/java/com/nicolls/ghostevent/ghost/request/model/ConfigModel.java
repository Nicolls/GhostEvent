package com.nicolls.ghostevent.ghost.request.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.nicolls.ghostevent.ghost.request.network.model.BaseResponse;

import java.io.Serializable;

//{"code":0,"message":null,"result":{"enable":true,"url":"https://cpu.baidu.com/1001/be900f73?scid=33854"}}

public class ConfigModel extends BaseResponse {
    @JSONField
    public String code;
    @JSONField
    public String message;
    @JSONField
    public Result result;

    public static class Result implements Serializable {
        @JSONField
        public boolean enable;
        @JSONField
        public String url;
    }

    @Override
    public String toString() {
        return "ConfigModel{" +
                "code='" + code + '\'' +
                ", message='" + message + '\'' +
                ", result=" + result +
                '}';
    }
}
