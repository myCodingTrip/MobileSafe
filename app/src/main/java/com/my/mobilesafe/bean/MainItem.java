package com.my.mobilesafe.bean;

/**
 * Created by MY on 2016/10/13.
 */

public class MainItem {
    String name;
    int pic;

    public MainItem(String name, int pic) {
        this.name = name;
        this.pic = pic;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPic() {
        return pic;
    }

    public void setPic(int pic) {
        this.pic = pic;
    }
}
