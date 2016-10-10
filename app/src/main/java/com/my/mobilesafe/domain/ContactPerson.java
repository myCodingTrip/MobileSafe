package com.my.mobilesafe.domain;

/**
 * Created by MY on 2016/10/9.
 */

public class ContactPerson {
    String name;
    String number;

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name+"\n" + number;
    }
}
