package com.nicolls.ghostevent.ghost.parse.model;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;

public class DomNode implements Serializable {

    @JSONField
    public int index;
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
    public String className;
    @JSONField
    public String idName;
    @JSONField
    public String title;

    @Override
    public String toString() {
        return "DomNode{" +
                "index=" + index +
                ", childIndex=" + childIndex +
                ", left=" + left +
                ", top=" + top +
                ", right=" + right +
                ", bottom=" + bottom +
                ", className='" + className + '\'' +
                ", idName='" + idName + '\'' +
                ", title='" + title + '\'' +
                '}';
    }
}
