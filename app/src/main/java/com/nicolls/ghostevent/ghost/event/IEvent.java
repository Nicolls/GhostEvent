package com.nicolls.ghostevent.ghost.event;

import android.graphics.PointF;

public interface IEvent {
    enum Direction{
        LEFT,RIGHT,TOP,BOTTOM
    }
    void click(float x,float y);
    void clickRatio(float ratioX,float ratioY);
    void longClick(float x,float y);
    void slide(Direction direct);
    void slide(PointF from,PointF to);
}
