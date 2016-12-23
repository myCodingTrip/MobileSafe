package com.my.mobilesafe.activity.cache;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.my.mobilesafe.R;
import com.my.mobilesafe.activity.BaseActivity;
import com.my.mobilesafe.adapter.BaseViewHolder;
import com.my.mobilesafe.adapter.SimpleAdapter;
import com.my.mobilesafe.bean.AppInfo;
import com.my.mobilesafe.engine.AppInfoEngine;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

import static android.service.notification.Condition.SCHEME;

public class CleanCacheActivity extends BaseActivity implements AppInfoEngine.CacheLoadFinish {
    AppInfoEngine engine;
    @InjectView(R.id.rv_app_list)
    RecyclerView rvAppList;
    @InjectView(R.id.ll_task_manager_loading)
    LinearLayout llTaskManagerLoading;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clean_cache);
        ButterKnife.inject(this);
        engine = new AppInfoEngine(this);
        engine.getAllAppCacheSize();
        llTaskManagerLoading.setVisibility(View.VISIBLE);
//        MyAdapter adapter = new MyAdapter()
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            llTaskManagerLoading.setVisibility(View.INVISIBLE);
            List<AppInfo> appInfoList = engine.appInfoList;
            MyAdapter adapter = new MyAdapter(getApplicationContext(), appInfoList);
            rvAppList.setAdapter(adapter);
            rvAppList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            rvAppList.setItemAnimator(new DefaultItemAnimator());
        }
    };

    //异步执行
    @Override
    public void cacheLoadFinish() {
        handler.sendEmptyMessage(0);
    }

    class MyAdapter extends SimpleAdapter<AppInfo> {

        public MyAdapter(Context context, List<AppInfo> data) {
            super(context, data, R.layout.item_cache_list);
        }

        @Override
        public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            //如果parent处传null进去会造成weight属性失效
            View view = layoutInflater.inflate(layoutResId, parent, false);
            return new BaseViewHolder(view, onItemClickListener);
        }

        @Override
        public void setViews(BaseViewHolder holder, final AppInfo appInfo) {
            holder.getImageView(R.id.iv_app_icon).setImageDrawable(appInfo.getIcon());
            holder.getTextView(R.id.tv_app_name).setText(appInfo.getName());
            holder.getTextView(R.id.tv_cache_size).setText("缓存大小" + appInfo.getCacheSize());
            holder.getImageView(R.id.iv_clean).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //ActivityNotFoundException
                    Intent intent = new Intent();
                    //intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    intent.setData(Uri.parse("package:" + appInfo.getPackageName()));
                    //intent.setData(Uri.fromParts(SCHEME, appInfo.getPackageName(), null));

                    startActivity(intent);
                }
            });
        }
    }
}
