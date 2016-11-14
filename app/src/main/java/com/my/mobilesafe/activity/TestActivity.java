package com.my.mobilesafe.activity;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.my.mobilesafe.R;
import com.my.mobilesafe.adapter.BaseViewHolder;
import com.my.mobilesafe.adapter.SimpleAdapter;
import com.my.mobilesafe.utils.LocationUtil;

import java.util.ArrayList;
import java.util.List;

public class TestActivity extends AppCompatActivity{
    String TAG = "TestActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        List<String> list = new ArrayList<>();
        for (int i=0; i<100; i++){
            list.add(i+"");
        }
        MyAdapter adapter = new MyAdapter(this, list);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rv_test);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

    }
    private class MyAdapter extends SimpleAdapter<String>{

        public MyAdapter(Context context, List<String> data) {
            super(context, data, R.layout.item_test);
        }

        @Override
        public void setViews(BaseViewHolder holder, String s) {
            holder.getTextView(R.id.tv_test).setText(s);
        }
    }
}
