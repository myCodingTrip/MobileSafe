package com.my.mobilesafe.bean;

import android.graphics.drawable.Drawable;

/**
 * Created by MY on 2016/11/8.
 */

public class AppInfo {
    private int uid;
    private Drawable icon;
    private String name;
    private String versionName;
    private String packageName;
    private boolean isUserApp;
    private boolean isSdcardApp;

    private String md5Code;

    private String cacheSize;

    public String getCacheSize() {
        return cacheSize;
    }

    public void setCacheSize(String cacheSize) {
        this.cacheSize = cacheSize;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public boolean isUserApp() {
        return isUserApp;
    }

    public void setUserApp(boolean userApp) {
        isUserApp = userApp;
    }

    public boolean isSdcardApp() {
        return isSdcardApp;
    }

    public void setSdcardApp(boolean sdcardApp) {
        isSdcardApp = sdcardApp;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getMd5Code() {
        return md5Code;
    }

    public void setMd5Code(String md5Code) {
        this.md5Code = md5Code;
    }
}
