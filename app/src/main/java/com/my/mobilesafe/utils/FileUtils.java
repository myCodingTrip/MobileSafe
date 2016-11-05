package com.my.mobilesafe.utils;

/**
 * Created by MY on 2016/10/6.
 */

public class FileUtils {
    /**
     * 获取文件名字
     * @param url 网络地址
     * @return
     */
    public static String getFileName(String url){
        return url.substring(url.lastIndexOf("/")+1);
    }
}
