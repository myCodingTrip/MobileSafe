package com.my.mobilesafe.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.my.mobilesafe.R;
import com.my.mobilesafe.utils.LocationUtil;

public class TestActivity extends AppCompatActivity implements LocationUtil.DealLocationListener{
    String TAG = "TestActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        LocationUtil locationUtil = new LocationUtil(this);
        locationUtil.getLocation();
    }

    @Override
    public void dealLocation(String location) {
        Log.d(TAG, location);
    }
}
