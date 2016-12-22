package com.my.mobilesafe.bean;

/**
 * Created by MY on 2016/12/11.
 */

public class Virus {
    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getMd5Code() {
        return md5Code;
    }

    public void setMd5Code(String md5Code) {
        this.md5Code = md5Code;
    }

    String packageName, md5Code;
}
