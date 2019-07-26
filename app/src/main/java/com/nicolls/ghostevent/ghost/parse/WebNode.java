package com.nicolls.ghostevent.ghost.parse;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;

public class WebNode implements Serializable {
    public enum Type {
        AD("ad"), NEWS("news"), VIDEO("video");
        private String tag;

        private Type(String tag) {
            this.tag = tag;
        }

    }
    @JSONField
    public int left;
    @JSONField
    public int top;
    @JSONField
    public int right;
    @JSONField
    public int bottom;
  

    @Override
    public String toString() {
        return "WebNode{" +
                "left=" + left +
                ", top=" + top +
                ", right=" + right +
                ", bottom=" + bottom +
                '}';
    }
}
