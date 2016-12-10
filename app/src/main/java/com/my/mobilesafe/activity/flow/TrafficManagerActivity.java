package com.my.mobilesafe.activity.flow;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.TrafficStats;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.my.mobilesafe.R;
import com.my.mobilesafe.activity.BaseActivity;
import com.my.mobilesafe.adapter.BaseViewHolder;
import com.my.mobilesafe.adapter.SimpleAdapter;
import com.my.mobilesafe.utils.TextFormater;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by MY on 2016/12/6.
 */

public class TrafficManagerActivity extends BaseActivity {
    @InjectView(R.id.tv_mobile_total)
    TextView tvMobileTotal;
    @InjectView(R.id.tv_wifi_total)
    TextView tvWifiTotal;
    @InjectView(R.id.rv_content)
    RecyclerView rvContent;
    PackageManager pm;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_traffic_manager);
        ButterKnife.inject(this);
        setFlowStatistics();

        List<ResolveInfo> resovleInfos;
        pm = getPackageManager();
        Intent intent = new Intent();
        intent.setAction("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.LAUNCHER");
        resovleInfos  =  pm.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        TrafficAdapter adapter = new TrafficAdapter(this, resovleInfos);
        rvContent.setAdapter(adapter);
        rvContent.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setFlowStatistics() {
        long mobileRx = TrafficStats.getMobileRxBytes();//获取通过Mobile连接收到的字节总数，不包含WiFi
        long mobileTx = TrafficStats.getMobileTxBytes();//Mobile发送的总字节数
        long mobileTotal = mobileRx + mobileTx;
        String mobileTraffic = (TextFormater.getDataSize(mobileTotal));
        tvMobileTotal.setText(mobileTraffic);

        long totalRx = TrafficStats.getTotalRxBytes();    //总接受流量
        long totalTx = TrafficStats.getTotalTxBytes();    //总发送流量
        long total = totalTx + totalRx;
        long wifiTotal = total - mobileTotal;
        String wifiTraffic =  (TextFormater.getDataSize(wifiTotal));
        tvWifiTotal.setText(wifiTraffic);
    }
    //item中weight失效
    private class TrafficAdapter extends SimpleAdapter<ResolveInfo>{

        public TrafficAdapter(Context context, List<ResolveInfo> data) {
            super(context, data, R.layout.item_traffic_manager);
        }

        @Override
        public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            //如果parent处传null进去会造成weight属性失效
            View view = layoutInflater.inflate(layoutResId, parent, false);
            return new BaseViewHolder(view, onItemClickListener);
        }

        @Override
        public void setViews(BaseViewHolder holder, ResolveInfo resolveInfo) {
            holder.getImageView(R.id.img_app_icon).setImageDrawable(resolveInfo.loadIcon(pm));
            holder.getTextView(R.id.tv_app_name).setText(resolveInfo.loadLabel(pm));
            String packageName = resolveInfo.activityInfo.packageName;
            try {
                PackageInfo packageInfo = pm.getPackageInfo(packageName, 0);
                int uid = packageInfo.applicationInfo.uid;
                holder.getTextView(R.id.tv_traffic_tx).setText(TextFormater.getDataSize( TrafficStats.getUidTxBytes(uid)));
                holder.getTextView(R.id.tv_traffic_rx).setText(TextFormater.getDataSize( TrafficStats.getUidRxBytes(uid)));
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}