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
import android.telephony.SmsMessage;
import android.util.Log;

import com.my.mobilesafe.R;

/**
 * Created by MY on 2016/10/13.
 */

public class SmsReceiver extends BroadcastReceiver {
    String TAG = "SmsReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("SmsReceiver", "onReceive");
        // 获取短信的内容
        Object[] pdus = (Object[]) intent.getExtras().get("pdus");
        Log.d("SmsReceiver", String.valueOf(pdus.length));
        for (Object pdu : pdus){
            SmsMessage sms = SmsMessage.createFromPdu((byte[]) pdu);
            String sender = sms.getOriginatingAddress();
            String content = sms.getMessageBody();
            Log.d("SmsReceiver", content);
            if(content.equals("#*location*#")){
                getLocation(context);

            }else if(content.equals("#*resetPassword*#")){
                DevicePolicyManager manager = (DevicePolicyManager) context.
                        getSystemService(Context.DEVICE_POLICY_SERVICE);
                manager.resetPassword("123", 0);
                manager.lockNow();
                abortBroadcast();
            }else if(content.equals("#*alarm*#")){
                Log.d("SmsReceiver", "#*alarm*#");
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

    private void getLocation(Context context) throws SecurityException{
        LocationManager locationManager = (LocationManager) context.
                getSystemService(Context.LOCATION_SERVICE);
        String providerType;
        Location location;
        providerType = LocationManager.GPS_PROVIDER;
        //将选择好的位置提供器传入到getLastKnownLocation()中，就可以得到一个Location对象
        location = locationManager.getLastKnownLocation(providerType);
        if (location == null) {
            providerType = LocationManager.NETWORK_PROVIDER;
            location = locationManager.getLastKnownLocation(providerType);
        }
        if (location == null){
            Log.d(TAG, "无法获取位置信息");
            return;
        }
        Log.d(TAG, providerType);
        //（提供器的类型，监听位置变化的时间间隔，监听位置变化的距离间隔，LocationListener监听器）
        //LocationListener每隔5s监听一次位置的变化情况，当距离间隔超过1m时调用onStatusChanged()方法，把新位置信息作为参数传入
        locationManager.requestLocationUpdates(providerType, 5000, 1,
                locationListener);
    }

    //位置监听器
    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            String currentPosition =
                    "纬度:" + location.getLatitude() + "\n" +
                    "经度:" + location.getLongitude()+ "\n" +
                    "高度:" + location.getAltitude()+ "\n" +
                    "速度:" + location.getSpeed();
            Log.d(TAG, currentPosition);
        }
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
        @Override
        public void onProviderEnabled(String provider) {
        }
        @Override
        public void onProviderDisabled(String provider) {
        }
    };
}
