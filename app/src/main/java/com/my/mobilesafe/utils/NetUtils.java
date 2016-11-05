package com.my.mobilesafe.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by MY on 2016/10/4.
 * 联网工具类
 */
public class NetUtils {

    /**
     * 获取网络连接信息
     * @param context
     * @return 当前网络连接是否可用
     */
    public static boolean isConnected(Context context){
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = cm.getActiveNetworkInfo();
        if(activeNetworkInfo == null){
            return false;
        }else if(activeNetworkInfo.getType() == ConnectivityManager.TYPE_MOBILE){
            return true;
        }else if(activeNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI){
            return true;
        }else return false;
    }
}
