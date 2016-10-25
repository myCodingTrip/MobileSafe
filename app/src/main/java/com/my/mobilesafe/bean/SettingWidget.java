package com.my.mobilesafe.bean;

import android.widget.CheckBox;
import android.widget.TextView;

/**
 * Created by MY on 2016/10/21.
 */

public class SettingWidget {
    CheckBox cb;
    TextView tv;
    String openText, closeText;

    public void setTv(boolean isOpen){
        cb.setChecked(isOpen);
        if(cb.isChecked())  tv.setText(openText);
        else  tv.setText(closeText);
    }
}
