package com.nicolls.ghostevent;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.nicolls.ghost.R;

public class MainActivity extends AppCompatActivity {
    private static final String TAG="MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onStart(View view) {
        Advert.instance.attachToActivity(this);
    }

    public void onEnd(View view) {
        Advert.instance.detach();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Advert.instance.detach();
    }

    private void addListView(){
        ListView listView=findViewById(R.id.my_listView);
        listView.setAdapter(new MyAdapter());
    }

    public void onReload(View view) {
        Advert.instance.reload();
    }

    public void onRecord(View view) {
        Advert.instance.record();
    }

    public void onPlay(View view) {
        Advert.instance.play();
    }

    public void onGoHome(View view) {
        Advert.instance.goHome();
    }

    public void onParse(View view) {
        Advert.instance.onParse();
    }

    public void onPlayParse(View view) {
        Advert.instance.onPlayParse();
    }

    public void onGoBack(View view) {
        Advert.instance.goBack();
    }

    public void onRandom(View view) {
        Advert.instance.random();
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
