package com.my.mobilesafe.utils;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import com.my.mobilesafe.http.OkHttpHelper;
import com.my.mobilesafe.http.SimpleCallback;
import com.squareup.okhttp.Response;

/**
 * Created by MY on 2016/10/18.
 * 用于获取位置信息
 */
public class LocationUtil {
    static final String TAG = "LocationUtil";
    static String APP_KEY = "7b379c569591bc5148c4eeb630f1f63c";
    static Context mContext;
    DealLocationListener dealLocationListener;

    public LocationUtil(Context context){
        mContext = context;
        dealLocationListener = (DealLocationListener) context;
    }

    public void getLocation() throws SecurityException{
        LocationManager locationManager = (LocationManager) mContext.
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
            ToastUtil.show(mContext, "无法获取位置信息");
            return;
        }
        Log.d(TAG, "providerType"+providerType);
        //（提供器的类型，监听位置变化的时间间隔，监听位置变化的距离间隔，LocationListener监听器）
        //LocationListener每隔5s监听一次位置的变化情况，当距离间隔超过1m时调用onStatusChanged()方法，把新位置信息作为参数传入
        locationManager.requestLocationUpdates(providerType, 50000, 1,
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

            OkHttpHelper okHttpHelper = OkHttpHelper.getInstance();
            String url = "http://apis.juhe.cn/geo/?key=" + APP_KEY + "&lat=" + location.getLatitude()
                    + "&lng=" + location.getLongitude() + "&type=1";

            okHttpHelper.get(url, new SimpleCallback<String>(mContext) {

                @Override
                public void onSuccess(Response response, String s) {
                    int a1 = s.indexOf("address");
                    int a2 = s.indexOf("\",\"business\"");
                    String result = s.substring(a1+10, a2);
                    dealLocationListener.dealLocation(result);
                }

                @Override
                public void onError(Response response, int code, Exception e) {

                }
            });
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

    public interface DealLocationListener {
        void dealLocation(String location);
    }
}
