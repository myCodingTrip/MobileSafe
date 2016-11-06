package com.my.mobilesafe.bean;

/**
 * Created by MY on 2016/11/5.
 */

public class ShortMessage {
    public String address;
    public long date;
    public int type;
    public String body;

    public ShortMessage(String address, long date, int type, String body){
        this.address = address;
        this.date = date;
        this.type = type;
        this.body = body;
    }

    public ShortMessage(){}

    @Override
    public String toString() {
        return "address"+address + " date"+date + " type"+type + " body"+body;
    }
}
