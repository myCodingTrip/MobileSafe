package com.my.mobilesafe.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.provider.CallLog;
import android.support.annotation.Nullable;
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.android.internal.telephony.ITelephony;
import com.my.mobilesafe.R;
import com.my.mobilesafe.activity.communication.CommunicationDefenderActivity;
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
    NotificationManager nm;
    public static String NUMBER = "NUMBER";

    @Override
    public void onCreate() {
        super.onCreate();
        dao = new BlackNumberDao(getApplicationContext());
        nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        receiver = new SmsReceiver();
        IntentFilter filter = new IntentFilter();
        filter.setPriority(Integer.MAX_VALUE);
        filter.addAction("android.provider.Telephony.SMS_RECEIVED");
        registerReceiver(receiver, filter);

        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        listener = new PhoneStateListener(){
            long ringingTime, endTime;
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                super.onCallStateChanged(state, incomingNumber);
                Log.d(TAG, "incomingNumber:"+incomingNumber);
                switch (state){
                    case TelephonyManager.CALL_STATE_IDLE://闲置
                        if(ringingTime > 0){
                            endTime = System.currentTimeMillis();
                            long time = endTime - ringingTime;
                            if(time>0 && time<1000){
                                showNotification(incomingNumber);
                            }
                            ringingTime = 0;
                        }
                        break;
                    case TelephonyManager.CALL_STATE_RINGING://响铃
                        if (dao.isCallBlack(incomingNumber)){
                            //挂断电话
                            endCall();
                            //删除黑名单通话记录
                            Uri uri = CallLog.Calls.CONTENT_URI;
                            MyContentObserver observer = new MyContentObserver(new Handler(), incomingNumber);
                            getContentResolver().registerContentObserver(uri, true, observer);
                            return;
                        }
                        ringingTime = System.currentTimeMillis();
                        break;
                    case TelephonyManager.CALL_STATE_OFFHOOK://接听
                        break;
                    default:
                }
            }

            private void showNotification(String incomingNumber) {
                Log.d(TAG, "showNotification");
                Intent intent = new Intent(getApplicationContext(), CommunicationDefenderActivity.class);
                intent.putExtra(NUMBER, incomingNumber);
                PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 100, intent, PendingIntent.FLAG_ONE_SHOT);
                //setLatestEventInfo()已弃用
                Notification.Builder builder = new Notification.Builder(getApplicationContext())
                        //.setTicker("拦截到来电一声响")
                        .setContentTitle("拦截到来电一声响")
                        .setContentText("点击将此号码设为黑名单")
                        .setSmallIcon(R.mipmap.ic_launcher);
                builder.setContentIntent(contentIntent);
                Notification notification = builder.build();
                notification.flags = Notification.FLAG_AUTO_CANCEL;

                nm.notify("my", 100, notification);
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

    //用于删除黑名单通话记录
    private class MyContentObserver extends ContentObserver{
        String incomingNumber;
        /**
         * Creates a content observer.
         *
         * @param handler The handler to run {@link #onChange} on, or null if none.
         */
        public MyContentObserver(Handler handler, String incomingNumber) {
            super(handler);
            this.incomingNumber = incomingNumber;
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            Uri uri = CallLog.Calls.CONTENT_URI;
            String where = CallLog.Calls.NUMBER + "=?";
            String[] selectionArgs = new String[]{incomingNumber};
            try {
                getContentResolver().delete(uri, where, selectionArgs);
            }catch (Exception e){
                e.printStackTrace();
            }
            getContentResolver().unregisterContentObserver(this);
        }
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
