package com.nicolls.ghostevent;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Point;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.nicolls.ghostevent.ghost.DisplayUtils;
import com.nicolls.ghostevent.ghost.LogUtil;
import com.nicolls.ghostevent.ghost.event.IEvent;
import com.nicolls.ghostevent.ghost.event.ViewEvent;

import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private static final String TAG="MainActivity";

    private IEvent event;
    private ListView listView;
    private Handler mainHandler=new Handler(Looper.getMainLooper());
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView=findViewById(R.id.my_listView);
//        listView.setAdapter(new MyAdapter());



//        ViewGroup viewGroup= (ViewGroup) getWindow().getDecorView();
//        viewGroup.addView(webView,0);
//        event=new ViewEvent(webView);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        LogUtil.d(TAG,"onTouchEvent ev:"+MotionEvent.actionToString(event.getAction()));
        return super.onTouchEvent(event);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        LogUtil.d(TAG,"dispatchTouchEvent ev:"+MotionEvent.actionToString(ev.getAction()));
        return super.dispatchTouchEvent(ev);
    }


    public void onLeft(View view) {
        event.slide(IEvent.Direction.LEFT);

    }

    public void onRight(View view) {
        event.slide(IEvent.Direction.RIGHT);

    }

    public void onTop(View view) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                event.slide(IEvent.Direction.TOP);
            }
        }).start();


    }

    public void onBottom(View view) {
        event.slide(IEvent.Direction.BOTTOM);

    }

    public void onClickXY(View view){
        event.click(50,50);
    }

    public void onClickRatio(View view){
        event.clickRatio(0.5f,0.5f);
    }

    public void onP2P(View view) {
        Point display=DisplayUtils.getDisplaySize(getApplicationContext());

        // 直y
        PointF from=new PointF(display.x/2.0f,display.y-display.y/4.0f);
        PointF to=new PointF(display.x/2.0f,display.y/4.0f);

        //直x
//        PointF from=new PointF(display.x-display.x/4.0f,display.y/3.0f);
//        PointF to=new PointF(display.x/4.0f,display.y/3.0f);

        // 斜着
//        PointF from=new PointF(display.x-display.x/4.0f,display.y-display.y/4.0f);
//        PointF to=new PointF(display.x/4.0f,display.y/4.0f);

        event.slide(from,to);
    }

    public void onLongClickXY(View view) {

    }

    public void onDone(View view) {
        Advert.attachToActivity(this);
    }

    private final class MyAdapter extends BaseAdapter {
        String[] names={"a","b","b","b","b","b","b","r","b","b","b","b","b","q","b","b","b","b","b","r"};

        @Override
        public int getCount() {
            return names.length;
        }

        @Override
        public Object getItem(int i) {
            return names[i];
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if(view==null){
                view= LayoutInflater.from(getApplicationContext()).inflate(R.layout.list_item,null);
            }
            TextView textView=view.findViewById(R.id.my_tv);
            textView.setText(names[i]);
            return view;
        }
    }
}
