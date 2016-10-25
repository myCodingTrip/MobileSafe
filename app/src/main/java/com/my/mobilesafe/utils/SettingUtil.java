package com.my.mobilesafe.utils;

import android.widget.CheckBox;
import android.widget.TextView;

/**
 * Created by MY on 2016/10/21.
 */

public class SettingUtil {
    public static void set(boolean isOpen, CheckBox cb, TextView tv, String open, String closed){
        cb.setChecked(isOpen);
        if(isOpen){
            tv.setText(open);
        }else {
            tv.setText(closed);
        }
    }
}
