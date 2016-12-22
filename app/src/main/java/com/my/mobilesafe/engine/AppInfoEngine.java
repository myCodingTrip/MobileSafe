package com.my.mobilesafe.engine;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.graphics.drawable.Drawable;
import android.os.RemoteException;
import android.util.Log;

import com.my.mobilesafe.bean.AppInfo;
import com.my.mobilesafe.utils.Md5Utils;
import com.my.mobilesafe.utils.TextFormater;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by MY on 2016/11/8.
 */

public class AppInfoEngine {
    private static final String TAG = "AppInfoEngine";
    private PackageManager packageManager;
    private Context mContext;
    public AppInfoEngine(Context context) {
        packageManager = context.getPackageManager();
        mContext = context;
    }

    /**
     * 返回当前手机里面安装的所有的程序信息的集合
     * @return 应用程序的集合
     */
    public List<AppInfo> getAllApps(){
        List<AppInfo> appInfoList = new ArrayList<>();
        List<PackageInfo> packageInfoList = packageManager.getInstalledPackages(0);
        Log.d(TAG, "size=" + packageInfoList.size());
        for(PackageInfo packageInfo :packageInfoList){
            AppInfo appInfo = new AppInfo();

            //获取应用图标
            ApplicationInfo applicationInfo = packageInfo.applicationInfo;
            Drawable icon = applicationInfo.loadIcon(packageManager);
            appInfo.setIcon(icon);

            //获取应用名称
            String appName = applicationInfo.loadLabel(packageManager).toString();
            appInfo.setName(appName);
            Log.d(TAG, appName);

            //获取版本号
            String versionName = packageInfo.versionName;
            appInfo.setVersionName(versionName);

            //程序包名
            String packageName = packageInfo.packageName;
            appInfo.setPackageName(packageName);

            int flags = applicationInfo.flags;

            //是否为第三方应用
            boolean isUserApp = isUserApp(flags);
            appInfo.setUserApp(isUserApp);

            //是否安装在sd卡上
            boolean isSdcardApp = isSdcardApp(flags);
            appInfo.setSdcardApp(isSdcardApp);

            appInfoList.add(appInfo);
        }
        return appInfoList;
    }

    /**
     * 判断某个应用程序是 不是三方的应用程序
     * @param flags
     * @return
     */
    public static boolean isUserApp(int flags) {
        //用户应用/系统应用
        if((flags&ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0){
            //升级过的系统应用
            return false;
        }else if((flags&ApplicationInfo.FLAG_SYSTEM) == 0) {
            //第三方应用
            return true;
        }else {
            //未升级的系统应用
            return false;
        }
    }

    /**
     * 判断某个应用程序是否安装在sd卡上
     * @param flags
     * @return
     */
    private boolean isSdcardApp(int flags) {
        if((flags&ApplicationInfo.FLAG_EXTERNAL_STORAGE) == 0){
            //手机内存
            return false;
        }else {
            //sd卡
            return true;
        }
    }

    /**
     * 获取所有应用的名称、包名、签名及其md5码，用于杀毒
     * @return
     */
    public List<AppInfo> getAllAppSignatures(){
        List<PackageInfo> installedPackages = packageManager.getInstalledPackages(PackageManager.GET_SIGNATURES);
        List<AppInfo> appInfoList = new ArrayList<>();
        for (PackageInfo packageInfo : installedPackages) {
            AppInfo info = new AppInfo();

            String name = packageInfo.applicationInfo.loadLabel(packageManager).toString();
            info.setName(name);

            String packageName = packageInfo.packageName;
            info.setPackageName(packageName);

            String result = packageInfo.signatures[0].toCharsString();
            String md5 = Md5Utils.encode(result);
            info.setMd5Code(md5);

            appInfoList.add(info);
        }
        return appInfoList;
    }


    /**
     * 获取所有应用的图标、名称、包名、缓存大小
     * @return
     */
    public void getAllAppCacheSize(){
        appInfoList = new ArrayList<>();
        List<PackageInfo> installedPackages = packageManager.getInstalledPackages(0);
        for (PackageInfo packageInfo : installedPackages) {
            //程序包名
            String packageName = packageInfo.packageName;
            //获取缓存信息
            try {
                Class clazz = Class.forName("android.content.pm.PackageManager");
                Method method = clazz.getMethod("getPackageSizeInfo", new Class[]{String.class, IPackageStatsObserver.class});
                method.invoke(packageManager, new Object[]{packageName, mStatsObserver});
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
    List<AppInfo> appInfoList;
    private IPackageStatsObserver.Stub mStatsObserver = new IPackageStatsObserver.Stub() {
        @Override
        public void onGetStatsCompleted(PackageStats pStats, boolean succeeded) throws RemoteException {
            long cacheSize = pStats.cacheSize;
            try {
                if (cacheSize > 0){
                    AppInfo appInfo = new AppInfo();
                    String packageName = pStats.packageName;
                    PackageManager pm = packageManager;

                    ApplicationInfo applicationInfo = pm.getApplicationInfo(packageName, 0);
                    //获取应用图标
                    Drawable icon = applicationInfo.loadIcon(packageManager);
                    appInfo.setIcon(icon);
                    //获取应用名称
                    String appName = applicationInfo.loadLabel(packageManager).toString();
                    appInfo.setName(appName);
                    String cache = TextFormater.getDataSize(cacheSize);
                    appInfo.setCacheSize(cache);

                    appInfoList.add(appInfo);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    };
}
