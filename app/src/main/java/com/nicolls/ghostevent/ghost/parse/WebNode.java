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
    public float position;
    @JSONField
    public float left;
    @JSONField
    public float top;
    @JSONField
    public float right;
    @JSONField
    public float bottom;
  

    @Override
    public String toString() {
        return "WebNode{" +
                "position=" + position +
                "left=" + left +
                ", top=" + top +
                ", right=" + right +
                ", bottom=" + bottom +
                '}';
    }
}
