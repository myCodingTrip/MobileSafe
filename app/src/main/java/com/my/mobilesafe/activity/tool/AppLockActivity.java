package com.my.mobilesafe.activity.tool;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StatFs;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.Formatter;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.my.mobilesafe.R;
import com.my.mobilesafe.activity.BaseActivity;
import com.my.mobilesafe.activity.task_manager.TaskManagerActivity;
import com.my.mobilesafe.adapter.BaseAdapter;
import com.my.mobilesafe.adapter.BaseViewHolder;
import com.my.mobilesafe.adapter.SimpleAdapter;
import com.my.mobilesafe.bean.AppInfo;
import com.my.mobilesafe.dao.AppLockDao;
import com.my.mobilesafe.engine.AppInfoEngine;
import com.my.mobilesafe.utils.ToastUtil;

import java.io.File;
import java.lang.reflect.Field;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class AppLockActivity extends BaseActivity {
    private final int APP_LOAD_FINISH = 0;
    String TAG = "AppLockActivity";
    PopupWindow popupWindow;
    @InjectView(R.id.tv_available_memory)
    TextView tvAvailableMemory;
    @InjectView(R.id.tv_available_sdcard)
    TextView tvAvailableSdcard;
    @InjectView(R.id.ll_app_manager_loading)
    LinearLayout llAppManagerLoading;
    @InjectView(R.id.rv_app_lock)
    RecyclerView rvAppLock;
    AppInfoEngine engine;
    List<AppInfo> appInfoList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_lock);
        ButterKnife.inject(this);
        //todo 无法获取实际的大小
        String availableMemory = getAvailableMemory();
        tvAvailableMemory.setText("可用内存：" + availableMemory);
        String availableSdcard = getAvailableSdcard();
        tvAvailableSdcard.setText("SD卡可用空间：" + availableSdcard);
        engine = new AppInfoEngine(this);
        new Thread(){
            @Override
            public void run() {
                appInfoList = engine.getAllApps();
                Message message = new Message();
                message.what = APP_LOAD_FINISH;
                handler.sendMessage(message);
            }
        }.start();
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case APP_LOAD_FINISH:
                    llAppManagerLoading.setVisibility(View.INVISIBLE);
                    setRecyclerView();
                    break;
            }
        }
    };

    /**
     * 获取手机内存可用空间
     * @return
     */
    private String getAvailableMemory() {
        File file = Environment.getDataDirectory();
        StatFs sf = new StatFs(file.getAbsolutePath());
        //总共有多少个块
        int availableBlocks = sf.getAvailableBlocks();
        //每个块的大小
        int blockCount = sf.getBlockCount();
        //进行格式化
        String size = Formatter.formatFileSize(this, availableBlocks * blockCount / (1024*1024));
        return size;
    }

    /**
     * 获取sd卡可用空间
     * @return
     */
    private String getAvailableSdcard() {
        File file = Environment.getExternalStorageDirectory();
        StatFs sf = new StatFs(file.getAbsolutePath());
        //总共有多少个块
        int availableBlocks = sf.getAvailableBlocks();
        //每个块的大小
        int blockCount = sf.getBlockCount();
        //进行格式化
        String size = Formatter.formatFileSize(this, availableBlocks * blockCount);

        Field mStatClass = null;
        try {
            mStatClass =StatFs.class.getDeclaredField("mStat");
            mStatClass.setAccessible(true);// 设置可操作属性
            Field f_bfree = mStatClass.get(sf).getClass().getDeclaredField("f_bfree");
            Field f_bsize = mStatClass.get(sf).getClass().getDeclaredField("f_bsize");
            // 通过反射获取的值才是真实可用内存
            size = (f_bfree.getLong(mStatClass.get(sf)) * f_bsize.getLong(mStatClass.get(sf)))/(1024*1024*1024) + "";
            Log.d(TAG, "mStat size " + size);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return size;
    }

    /**
     * 设置所有应用信息的列表
     */
    private void setRecyclerView() {
        AppLockAdapter adapter = new AppLockAdapter(this, appInfoList);
        adapter.setOnItemClickListener(new BaseAdapter.OnItemClickListener() {
            @Override
            public void OnClick(View view, final int position) {
                //点击后条目右移的动画
                //ToastUtil.show(getApplicationContext(), position);
//                TranslateAnimation ta = new TranslateAnimation(
//                        Animation.RELATIVE_TO_SELF, 0.0f,
//                        Animation.RELATIVE_TO_SELF, 0.5f,
//                        Animation.RELATIVE_TO_SELF, 0.0f,
//                        Animation.RELATIVE_TO_SELF, 0.0f);
//                ta.setDuration(500);
//                view.startAnimation(ta);
                if(popupWindow != null){
                    popupWindow.dismiss();
                }
                final AppInfo appInfo = appInfoList.get(position);
                View contentView = getLayoutInflater().inflate(R.layout.window_app_control, null);
                //卸载程序
                contentView.findViewById(R.id.ll_uninstall).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (appInfo.isUserApp()) {
                            Intent intent = new Intent();
                            intent.setAction("android.intent.action.DELETE");
                            intent.addCategory("android.intent.category.DEFAULT");
                            intent.setData(Uri.parse("package:" + appInfo.getPackageName()));
                            startActivityForResult(intent, 0);
                        } else {
                            ToastUtil.show(getApplicationContext(), "系统应用需要有root权限才能卸载");
                        }
                    }
                });

                //运行程序
                contentView.findViewById(R.id.ll_run).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PackageManager pm = getPackageManager();
                        Intent intent = pm.getLaunchIntentForPackage(appInfo.getPackageName());
                        if (intent != null) {
                            startActivity(intent);
                        } else {
                            ToastUtil.show(getApplicationContext(), "无法开启当前的应用");
                        }
                    }
                });

                //分享程序
                contentView.findViewById(R.id.ll_share).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.setAction("android.intent.action.SEND");
                        intent.addCategory("android.intent.category.DEFAULT");
                        intent.setType("text/plain");
                        intent.putExtra(Intent.EXTRA_TEXT,
                                "推荐您使用一款软件，软件的名称叫：" + appInfo.getName() +
                                        "应用程序包名：" + appInfo.getPackageName());
                        startActivity(intent);
                    }
                });

                //程序详细信息
                contentView.findViewById(R.id.ll_detail).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                        intent.addCategory(Intent.CATEGORY_DEFAULT);
                        intent.setData(Uri.parse("package:"+appInfo.getPackageName()));
                        startActivity(intent);

                    }
                });

                //设置程序锁
                final AppLockDao dao = new AppLockDao(getApplicationContext());
                final TextView tvAppLock = (TextView) contentView.findViewById(R.id.tv_app_lock);
                final ImageView ivLock = (ImageView) contentView.findViewById(R.id.img_lock);
                if(dao.find(appInfo.getPackageName())){
                    ivLock.setImageResource(R.mipmap.lock);
                    tvAppLock.setText("解锁");
                }
                contentView.findViewById(R.id.ll_lock).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        boolean isLock = dao.find(appInfo.getPackageName());
                        if(!isLock){
                            dao.add(appInfo.getPackageName());
                            tvAppLock.setText("解锁");
                            ivLock.setImageResource(R.mipmap.lock);
                        }else {
                            dao.delete(appInfo.getPackageName());
                            tvAppLock.setText("加锁");
                            ivLock.setImageResource(R.mipmap.unlock);
                        }
                        //popupWindow.update();
                    }
                });
                popupWindow = new PopupWindow(contentView, -2, -2);
                popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                int[] location = new int[2];// 创建一个数据用来存放 x，y的坐标
                view.getLocationInWindow(location);
                popupWindow.showAtLocation(view, Gravity.LEFT + Gravity.TOP, 60, location[1]);

            }
        });
        rvAppLock.setAdapter(adapter);
        //这两句不写啥也没有......
        rvAppLock.setLayoutManager(new LinearLayoutManager(this));
        rvAppLock.setItemAnimator(new DefaultItemAnimator());
        rvAppLock.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (popupWindow != null){
                    popupWindow.dismiss();
                    popupWindow = null;
                }
            }
        });
    }

    private class AppLockAdapter extends SimpleAdapter<AppInfo>{

        public AppLockAdapter(Context context, List<AppInfo> data) {
            super(context, data, R.layout.item_app_lock);
        }

        @Override
        public void setViews(BaseViewHolder holder, AppInfo appInfo) {
            holder.getImageView(R.id.iv_app_icon).setImageDrawable(appInfo.getIcon());
            holder.getTextView(R.id.tv_app_name).setText(appInfo.getName());
            holder.getTextView(R.id.tv_app_package_name).setText(appInfo.getVersionName());
        }


    }
}
