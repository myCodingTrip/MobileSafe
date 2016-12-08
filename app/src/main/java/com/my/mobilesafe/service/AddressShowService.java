package com.my.mobilesafe.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.my.mobilesafe.R;
import com.my.mobilesafe.constant.SharedKey;
import com.my.mobilesafe.dao.AddressDao;
import com.my.mobilesafe.utils.RegexUtil;

import java.io.File;


public class AddressShowService extends Service {
    TelephonyManager telephonyManager;
    MyPhoneStateListener listener;
    WindowManager wm;
    LayoutInflater mInflater;
    View view;
    SharedPreferences sp;
    OutgoingCallReceiver receiver;
    @Override
    public void onCreate() {
        super.onCreate();
        wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        listener = new MyPhoneStateListener();
        telephonyManager.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
        sp = getSharedPreferences("config", MODE_PRIVATE);

        receiver = new OutgoingCallReceiver();
        IntentFilter filter = new IntentFilter(Intent.ACTION_NEW_OUTGOING_CALL);
        registerReceiver(receiver, filter);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        telephonyManager.listen(listener, PhoneStateListener.LISTEN_NONE);
        unregisterReceiver(receiver);
    }

    public class MyPhoneStateListener extends PhoneStateListener{
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
                    showAddressWindow(incomingNumber);
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK://接听
                    break;
                default:
            }
        }
    }

    public class OutgoingCallReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //弹出归属地窗体
            String number = getResultData();
            showAddressWindow(number);
        }
    }

    public void showAddressWindow(String incomingNumber) {
        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.gravity = Gravity.CENTER;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.format = PixelFormat.TRANSLUCENT;
        params.type = WindowManager.LayoutParams.TYPE_PRIORITY_PHONE;//像电话应用优先级一样
        params.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON//保持屏幕高亮
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE //不能获得焦点
                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;    //不能触摸
        final int x = sp.getInt(SharedKey.LOCATION_X, 0);
        int y = sp.getInt(SharedKey.LOCATION_Y, 0);
        params.x = x;
        params.y = y;
        view = mInflater.inflate(R.layout.window_address, null);

        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //TODO 设置来电窗口拖动
                return true;
            }
        });

        TextView tv = (TextView) view.findViewById(R.id.tv_address);
        tv.setText(AddressDao.getAddress(getApplicationContext(), incomingNumber));
        wm.addView(view, params);
    }

}
