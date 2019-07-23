package com.nicolls.ghostevent.ghost.event;

import android.graphics.Rect;

public class WebNode {
    public enum Type {
        AD, VIDEO, NEWS
    }

    public String id;
    public Rect position;
    public Type type;

    @Override
    public String toString() {
        return "WebNode{" +
                "id='" + id + '\'' +
                ", position=" + position +
                ", type=" + type +
                '}';
    }
}
