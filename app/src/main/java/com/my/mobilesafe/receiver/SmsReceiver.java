package com.my.mobilesafe.receiver;

import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;

import com.my.mobilesafe.R;
import com.my.mobilesafe.utils.LocationUtil;
import com.my.mobilesafe.utils.ToastUtil;

/**
 * Created by MY on 2016/10/13.
 */

public class SmsReceiver extends BroadcastReceiver implements LocationUtil.DealLocationListener{
    String TAG = "SmsReceiver";
    String sender;
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive");
        // 获取短信的内容
        Object[] pdus = (Object[]) intent.getExtras().get("pdus");
        Log.d(TAG, String.valueOf(pdus.length));
        //遍历所有未读短信
        for (Object pdu : pdus){
            SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdu);
            sender = smsMessage.getOriginatingAddress();
            String content = smsMessage.getMessageBody();
            Log.d(TAG, content);
            if(content.equals("#*location*#")){
                abortBroadcast();
                LocationUtil locationUtil = new LocationUtil(context);
                locationUtil.getLocation();
            }else if(content.equals("#*resetPassword*#")){
                DevicePolicyManager manager = (DevicePolicyManager) context.
                        getSystemService(Context.DEVICE_POLICY_SERVICE);
                manager.resetPassword("123", 0);
                manager.lockNow();
                abortBroadcast();
            }else if(content.equals("#*alarm*#")){
                Log.d(TAG, "#*alarm*#");
                MediaPlayer player = MediaPlayer.create(context, R.raw.ylzs);
                player.setVolume(1.0f, 1.0f);
                player.start();
                abortBroadcast();
            }else if("#*wipedata*#".equals(content)){
                DevicePolicyManager manager = (DevicePolicyManager) context.
                        getSystemService(Context.DEVICE_POLICY_SERVICE);
                manager.wipeData(0);
                abortBroadcast();
            }
        }
    }

    @Override
    public void dealLocation(String location) {
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(sender, null, location, null, null);
    }
}
