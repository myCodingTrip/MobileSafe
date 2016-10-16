package com.my.mobilesafe.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import static android.content.Context.MODE_PRIVATE;
import static com.my.mobilesafe.constant.SharedKey.SIM_SERIAL_NUM;

/**
 * Created by MY on 2016/10/9.
 */

public class BootCompleteReceiver extends BroadcastReceiver {
    SharedPreferences sp;
    @Override
    public void onReceive(Context context, Intent intent) {
        //ToastUtil.show(context, "开机啦");
        Log.d("BootCompleteReceiver", "开机啦");
        sp = context.getSharedPreferences("config", MODE_PRIVATE);
        String simSerialNum1 = sp.getString(SIM_SERIAL_NUM, null);
        if(!TextUtils.isEmpty(simSerialNum1)){
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            String simSerialNum2 = tm.getSimSerialNumber();
            if(!simSerialNum1.equals(simSerialNum2)){
                //TODO 发短信
            }
        }

    }
}
