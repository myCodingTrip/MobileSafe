package com.my.mobilesafe.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import com.my.mobilesafe.R;
import com.my.mobilesafe.constant.SharedKey;


public class AddressShowService extends Service {
    TelephonyManager telephonyManager;
    MyPhoneStateListener listener;
    WindowManager wm;
    LayoutInflater mInflater;
    View view;
    SharedPreferences sp;

    @Override
    public void onCreate() {
        super.onCreate();
        wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        listener = new MyPhoneStateListener();
        telephonyManager.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
        sp = getSharedPreferences("config", MODE_PRIVATE);
    }

    private class MyPhoneStateListener extends PhoneStateListener{
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
            switch (state){
                case TelephonyManager.CALL_STATE_IDLE://闲置
                    if(view != null){
                        wm.removeView(view);
                    }
                    break;
                case TelephonyManager.CALL_STATE_RINGING://响铃
                    showAddress();
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK://接听
                    break;
                default:
            }
        }

        private void showAddress() {
            WindowManager.LayoutParams params = new WindowManager.LayoutParams();
            params.gravity = Gravity.CENTER;
            params.height = WindowManager.LayoutParams.WRAP_CONTENT;
            params.width = WindowManager.LayoutParams.WRAP_CONTENT;
            params.format = PixelFormat.TRANSLUCENT;
            params.type = WindowManager.LayoutParams.TYPE_TOAST;
            params.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON//保持屏幕高亮
                    | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE //不能获得焦点
                    | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;    //不能触摸
            int x = sp.getInt(SharedKey.LOCATION_X, 0);
            int y = sp.getInt(SharedKey.LOCATION_Y, 0);
            params.x = x;
            params.y = y;
            view = mInflater.inflate(R.layout.window_address, null);
            wm.addView(view, params);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        telephonyManager.listen(listener, PhoneStateListener.LISTEN_NONE);
    }
}
