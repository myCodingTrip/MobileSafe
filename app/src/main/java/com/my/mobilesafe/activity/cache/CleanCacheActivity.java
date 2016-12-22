package com.my.mobilesafe.activity.cache;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.my.mobilesafe.R;
import com.my.mobilesafe.activity.BaseActivity;
import com.my.mobilesafe.adapter.BaseViewHolder;
import com.my.mobilesafe.adapter.SimpleAdapter;
import com.my.mobilesafe.bean.AppInfo;

import java.util.List;

public class CleanCacheActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clean_cache);
    }

    class MyAdapter extends SimpleAdapter<AppInfo>{

        public MyAdapter(Context context, List<AppInfo> data) {
            super(context, data, R.layout.item_cache_list);
        }

        @Override
        public void setViews(BaseViewHolder holder, AppInfo appInfo) {
            holder.getImageView(R.id.iv_app_icon).setImageDrawable(appInfo.getIcon());
            holder.getTextView(R.id.tv_app_name).setText(appInfo.getName());
            holder.getTextView(R.id.tv_cache_size).setText("占用内存" + appInfo.getCacheSize());
            holder.getImageView(R.id.iv_clean).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    
                }
            });
        }
    }
}
