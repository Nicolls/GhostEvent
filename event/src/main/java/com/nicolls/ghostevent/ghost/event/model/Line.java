package com.nicolls.ghostevent.ghost.event.model;

import java.io.Serializable;

public class Line implements Serializable {
    public int from;
    public int to;

    public Line(int from, int to) {
        this.from = from;
        this.to = to;
    }

    @Override
    public String toString() {
        return "Line{" +
                "from=" + from +
                ", to=" + to +
                '}';
    }
}
