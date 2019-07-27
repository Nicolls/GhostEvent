package com.nicolls.ghostevent.ghost.parse;

import java.io.Serializable;

public class ViewNode implements Serializable {
    public enum Type {
        ADVERT("advert"), NEWS("news"), VIDEO("video"), OTHER("other");
        private String tag;

        private Type(String tag) {
            this.tag = tag;
        }

    }

    public int position;
    public int childIndex;
    public float left;
    public float top;
    public float right;
    public float bottom;
    public String title;
    public Type type;
    public float centerX;
    public float centerY;
    public float clientWidth;
    public float clientHeight;

    public ViewNode(DomNode domNode, Type type) {
        this.type = type;
        this.position = domNode.position;
        this.childIndex = domNode.childIndex;
        this.left = domNode.left;
        this.top = domNode.top;
        this.right = domNode.right;
        this.bottom = domNode.bottom;
        this.clientWidth=domNode.clientWidth;
        this.clientHeight=domNode.clientHeight;
        this.title=domNode.title;
        this.centerX = this.left + (this.right - this.left) / 2;
        this.centerY = this.top + (this.bottom - this.top) / 2;
    }

    @Override
    public String toString() {
        return "ViewNode{" +
                "position=" + position +
                ", childIndex=" + childIndex +
                ", left=" + left +
                ", top=" + top +
                ", right=" + right +
                ", bottom=" + bottom +
                ", type=" + type +
                ", centerX=" + centerX +
                ", centerY=" + centerY +
                ", clientWidth=" + clientWidth +
                ", clientHeight=" + clientHeight +
                ", title=" + title +
                '}';
    }
}
