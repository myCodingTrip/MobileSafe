package com.my.mobilesafe.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.android.internal.telephony.ITelephony;
import com.my.mobilesafe.dao.BlackNumberDao;

import java.lang.reflect.Method;

/**
 * Created by MY on 2016/10/30.
 */

public class BlackListService extends Service {
    String TAG = "BlackListService";
    SmsReceiver receiver;
    BlackNumberDao dao;
    TelephonyManager telephonyManager;
    PhoneStateListener listener;
    @Override
    public void onCreate() {
        super.onCreate();
        dao = new BlackNumberDao(getApplicationContext());

        receiver = new SmsReceiver();
        IntentFilter filter = new IntentFilter();
        filter.setPriority(Integer.MAX_VALUE);
        filter.addAction("android.provider.Telephony.SMS_RECEIVED");
        registerReceiver(receiver, filter);

        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        listener = new PhoneStateListener(){
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                super.onCallStateChanged(state, incomingNumber);
                Log.d(TAG, "incomingNumber:"+incomingNumber);
                switch (state){
                    case TelephonyManager.CALL_STATE_IDLE://闲置
                        break;
                    case TelephonyManager.CALL_STATE_RINGING://响铃
                        if (dao.isCallBlack(incomingNumber)){
                            //挂断电话
                            endCall();
                        }
                        break;
                    case TelephonyManager.CALL_STATE_OFFHOOK://接听
                        break;
                    default:
                }
            }

            //利用反射挂断电话
            private void endCall() {
                Log.d(TAG, "endCall()");
                try {
                    //加载字节码
                    Class ServiceManager = Class.forName("android.os.ServiceManager");
                    //找到方法
                    Method method = ServiceManager.getMethod("getService", String.class);
                    //调用方法
                    IBinder iBinder = (IBinder) method.invoke(null, Context.TELEPHONY_SERVICE);
                    //现在iBinder是一个代理对象，需要转成真实对象
                    ITelephony iTelephony = ITelephony.Stub.asInterface(iBinder);
                    iTelephony.endCall();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        telephonyManager.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
    }

    private class SmsReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            dao = new BlackNumberDao(context);
            Object[] pdus = (Object[]) intent.getExtras().get("pdus");
            //遍历所有未读短信
            for (Object pdu : pdus){
                SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdu);
                String sender = smsMessage.getOriginatingAddress();
                if(dao.isSmsBlack(sender)){
                    //todo 不能拦截
                    Log.d(TAG, "abortBroadcast()");
                    abortBroadcast();//拦截短信
                }
            }
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(receiver);
        telephonyManager.listen(listener, PhoneStateListener.LISTEN_NONE);
        super.onDestroy();
    }
}
