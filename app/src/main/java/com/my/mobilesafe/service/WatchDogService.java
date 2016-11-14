package com.my.mobilesafe.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import java.util.List;

public class WatchDogService extends Service {
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
                    List<ActivityManager.RunningTaskInfo> runningTasks = am.getRunningTasks(10);
                    //最新的任务
                    ActivityManager.RunningTaskInfo runningTaskInfo = runningTasks.get(0);
                    //最顶端的activity
                    ComponentName componentName = runningTaskInfo.topActivity;
                    componentName.getPackageName();
                }
            }
        }.start();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isOpen = false;
    }
}
