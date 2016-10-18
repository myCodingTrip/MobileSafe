package com.my.mobilesafe.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

import com.my.mobilesafe.R;

/**
 * Created by MY on 2016/10/7.
 */

public class BaseActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
    }
}
