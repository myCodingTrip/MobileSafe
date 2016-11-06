package com.my.mobilesafe;

import android.app.Application;
import android.os.Environment;
import android.test.ApplicationTestCase;
import android.util.Log;

import com.my.mobilesafe.bean.ShortMessage;
import com.my.mobilesafe.engine.SmsEngine;

import java.util.List;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
    String TAG = "ApplicationTest";
    public ApplicationTest() {
        super(Application.class);
        String path = Environment.getExternalStorageDirectory().getAbsolutePath()+"/sss";
        Log.d(TAG, path);
    }
}