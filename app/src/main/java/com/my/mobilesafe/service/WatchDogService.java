package com.my.mobilesafe.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.my.mobilesafe.activity.tool.EnterAppActivity;
import com.my.mobilesafe.dao.AppLockDao;

import java.util.List;


public class WatchDogService extends Service {
    public static final String PACKAGE_NAME = "PACKAGE_NAME";
    String TAG = "WatchDogService";
    private boolean isOpen = true;
    ActivityManager am;

    public WatchDogService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        new Thread(){
            @Override
            public void run() {
                while (isOpen){
                    //获取最近的任务
                    //todo api已被抛弃，无法使用
                    List<ActivityManager.RunningTaskInfo> runningTasks = am.getRunningTasks(1);
                    //最新的任务
                    ActivityManager.RunningTaskInfo runningTaskInfo = runningTasks.get(0);
                    //最顶端的activity
                    ComponentName componentName = runningTaskInfo.topActivity;
                    String packageName = componentName.getPackageName();
                    AppLockDao dao = new AppLockDao(getApplicationContext());
                    Log.d(TAG, packageName);
                    if(dao.find(packageName)){
                        Intent intent = new Intent(getApplicationContext(), EnterAppActivity.class);
                        intent.putExtra(PACKAGE_NAME, packageName);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                    try {
                        sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isOpen = false;
    }
}
