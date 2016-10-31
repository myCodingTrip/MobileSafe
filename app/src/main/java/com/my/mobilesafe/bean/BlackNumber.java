package com.my.mobilesafe.bean;

/**
 * 黑名单号码，与数据库中的blacknumber表相对应
 * Created by MY on 2016/10/27.
 */

public class BlackNumber {
    public String _id, number;
    public int type;

    public BlackNumber(String _id, String number, int type){
        this._id = _id;
        this.number = number;
        this.type = type;
    }

    public static final int BLACK_PHONE = 0;
    public static final int BLACK_SMS = 1;
    public static final int BLACK_ALL = 2;
}
