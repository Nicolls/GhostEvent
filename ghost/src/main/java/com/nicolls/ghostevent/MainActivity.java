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
        Advert.instance.attach(this);
    }

    public void onEnd(View view) {
        Advert.instance.detach();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Advert.instance.detach();
    }

    public void onTest(View view) {
        Advert.instance.test();
    }

    public void onBack(View view) {
        Advert.instance.back();
    }
}
