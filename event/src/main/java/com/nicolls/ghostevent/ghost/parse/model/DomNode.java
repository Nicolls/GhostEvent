package com.nicolls.ghostevent.ghost.parse.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class DomNode implements Serializable {
    private static final String TAG = "DomNode";

    public DomNode(JSONObject jsonObject) throws JSONException {
        index = jsonObject.getInt("index");
        childIndex = jsonObject.getInt("childIndex");
        left = (float) jsonObject.getDouble("left");
        top = (float) jsonObject.getDouble("top");
        right = (float) jsonObject.getDouble("right");
        bottom = (float) jsonObject.getDouble("bottom");
        className = jsonObject.getString("className");
        idName = jsonObject.getString("idName");
        title = jsonObject.getString("title");
    }

    public int index;
    public int childIndex;
    public float left;
    public float top;
    public float right;
    public float bottom;
    public String className;
    public String idName;
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
