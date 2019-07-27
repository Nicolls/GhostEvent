package com.nicolls.ghostevent.ghost.parse;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;

public class DomNode implements Serializable {
    public enum Type {
        AD("ad"), NEWS("news"), VIDEO("video");
        private String tag;

        private Type(String tag) {
            this.tag = tag;
        }

    }

    @JSONField
    public int position;
    @JSONField
    public int childIndex;
    @JSONField
    public float left;
    @JSONField
    public float top;
    @JSONField
    public float right;
    @JSONField
    public float bottom;
    @JSONField
    public float clientWidth;
    @JSONField
    public float clientHeight;
    @JSONField
    public String className;
    @JSONField
    public String title;

    @Override
    public String toString() {
        return "DomNode{" +
                "position=" + position +
                ", childIndex=" + childIndex +
                ", left=" + left +
                ", top=" + top +
                ", right=" + right +
                ", bottom=" + bottom +
                ", clientWidth=" + clientWidth +
                ", clientHeight=" + clientHeight +
                ", className='" + className + '\'' +
                ", title='" + title + '\'' +
                '}';
    }
}
