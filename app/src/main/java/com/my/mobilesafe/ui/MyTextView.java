package com.my.mobilesafe.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by MY on 2016/10/6.
 * 实现跑马灯效果
 */

public class MyTextView extends TextView {
    //直接 new TextView(this)
    public MyTextView(Context context) {
        super(context);
    }

    //<TextView android:..../>
    public MyTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    //<TextView android:.... style=""/>
    public MyTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    //默认获得焦点
    @Override
    public boolean isFocused() {
        return true;
    }
}
