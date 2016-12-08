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
        setContentView(R.layout.activity_traffic_manager);
        RecyclerView rv = (RecyclerView) findViewById(R.id.rv_content);

    }
    class Adapter extends SimpleAdapter<String>{

        public Adapter(Context context, List<String> data, int layoutResId) {
            super(context, data, R.layout.item_traffic_manager);
        }

        @Override
        public void setViews(BaseViewHolder holder, String s) {
//            holder.getImageView(R.id.img_app_icon).setImageDrawable(resolveInfo.loadIcon(pm));
//            holder.getTextView(R.id.tv_app_name).setText(resolveInfo.loadLabel(pm));
//            holder.getTextView(R.id.tv_traffic_tx).setText(TextFormater.getDataSize( TrafficStats.getUidTxBytes(uid)));
//            holder.getTextView(R.id.tv_traffic_rx).setText(TextFormater.getDataSize( TrafficStats.getUidRxBytes(uid)));
        }
    }
}
