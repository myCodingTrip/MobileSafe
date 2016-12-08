package com.my.mobilesafe.engine;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Debug;
import android.util.Log;

import com.my.mobilesafe.bean.Task;

public class TaskInfoEngine {
    final String TAG = "TaskInfoEngine";
	private PackageManager pm;
	private ActivityManager am;
	private Context context;
    List<RunningAppProcessInfo> runningAppProcesses;

	public TaskInfoEngine(Context context) {
		this.context = context;
		Activity activity = (Activity)context;
		pm = context.getPackageManager();
		am = (ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE);

	}

	public List<Task> getAllTasks(){
		runningAppProcesses = am.getRunningAppProcesses();
		List<Task> tasks = new ArrayList<>();
		ApplicationInfo appInfo = null;
		String processName = null;
		for (ActivityManager.RunningAppProcessInfo raInfo:runningAppProcesses){
			Task task = new Task();
			try {
				processName = raInfo.processName;
				task.setProcessName(processName);
				appInfo = pm.getPackageInfo(processName, 0).applicationInfo;

				//获取进程名称
				String appName = appInfo.loadLabel(pm).toString();
				task.setAppName(appName);
			} catch (PackageManager.NameNotFoundException e) {
				e.printStackTrace();
                //处理没有名字的应用
				task.setAppName(processName);
			}finally {
				//获取进程图标
				Drawable appIcon = appInfo.loadIcon(pm);
				task.setAppIcon(appIcon);

				int pid = raInfo.pid;
				task.setPid(pid);

				//进程的内存占用
				Debug.MemoryInfo[] memoryInfos = am.getProcessMemoryInfo(new int[]{pid});
				int memorySize = memoryInfos[0].getTotalPrivateDirty();
				task.setMemorySize(memorySize);

				task.setIsUserApp( AppInfoEngine.isUserApp(appInfo.flags) );

				tasks.add(task);
			}
		}
        Log.d(TAG,  "tasks.size()：" + tasks.size());
		return tasks;
	}

    public long getAvailableMemory(){
        ActivityManager.MemoryInfo outInfo = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(outInfo);
        return outInfo.availMem;
    }

    public int getTaskNum(){
		runningAppProcesses = am.getRunningAppProcesses();
		Log.d(TAG,  "runningAppProcesses.size()：" + runningAppProcesses.size());
        return runningAppProcesses.size();
    }

    private String getTotalMemory(){
        return null;
    }

	/**
	 * 结束某一进程
	 * @param task
     */
	public void killTask(Task task){
		am.killBackgroundProcesses(task.getProcessName());
	}

	/**
	 * 判断是否为手机卫士任务本身
	 * @return
     */
	public boolean isThisTask(Task task){
		if (task.getProcessName().equals("com.my.mobilesafe"))
			return true;
		return false;
	}
}

