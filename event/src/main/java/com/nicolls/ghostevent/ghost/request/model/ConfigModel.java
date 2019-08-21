package com.nicolls.ghostevent.ghost.request.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

//{"code":0,"message":null,"result":{"enable":true,"url":"https://cpu.baidu.com/1001/be900f73?scid=33854"}}

public class ConfigModel implements Serializable {
    public String code;
    public String message;
    public Result result;

    public ConfigModel(JSONObject jsonObject) throws JSONException {
        this.code = jsonObject.getString("code");
        this.message = jsonObject.getString("message");
        JSONObject result = jsonObject.getJSONObject("result");
        if (result != null) {
            this.result = new Result();
            this.result.enable = result.getBoolean("enable");
            this.result.url = result.getString("url");
        }
    }

    public static class Result implements Serializable {
        public boolean enable;
        public String url;

        @Override
        public String toString() {
            return "Result{" +
                    "enable=" + enable +
                    ", url='" + url + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "ConfigModel{" +
                "code='" + code + '\'' +
                ", message='" + message + '\'' +
                ", result=" + (result == null ? "null" : result.toString()) +
                '}';
    }
}
