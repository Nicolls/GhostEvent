package com.nicolls.ghostevent.ghost.parse.model;

import com.nicolls.ghostevent.ghost.parse.model.DomNode;

import java.io.Serializable;

public class ViewNode implements Serializable {
    public enum Type {
        ADVERT("advert"), NEWS("news"), VIDEO("video"),
        ARROW_TOP("arrow_top"), OTHER("other");
        private String tag;

        private Type(String tag) {
            this.tag = tag;
        }

    }

    public int index;
    public int childIndex;
    public float left;
    public float top;
    public float right;
    public float bottom;
    public String title;
    public Type type;
    public float centerX;
    public float centerY;

    public ViewNode(DomNode domNode, Type type) {
        this.type = type;
        this.index = domNode.index;
        this.childIndex = domNode.childIndex;
        this.left = domNode.left;
        this.top = domNode.top;
        this.right = domNode.right;
        this.bottom = domNode.bottom;
        this.title = domNode.title;
        this.centerX = this.left + (this.right - this.left) / 2;
        this.centerY = this.top + (this.bottom - this.top) / 2;
    }

    @Override
    public String toString() {
        return "ViewNode{" +
                "index=" + index +
                ", childIndex=" + childIndex +
                ", left=" + left +
                ", top=" + top +
                ", right=" + right +
                ", bottom=" + bottom +
                ", type=" + type +
                ", centerX=" + centerX +
                ", centerY=" + centerY +
                ", title=" + title +
                '}';
    }
}
