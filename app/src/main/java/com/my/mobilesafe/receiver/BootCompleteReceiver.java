package com.my.mobilesafe.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import com.my.mobilesafe.service.AddressShowService;

import static android.content.Context.MODE_PRIVATE;
import static com.my.mobilesafe.constant.SharedKey.IS_SHOW_ADDRESS;
import static com.my.mobilesafe.constant.SharedKey.SAFE_NUM;
import static com.my.mobilesafe.constant.SharedKey.SIM_SERIAL_NUM;

/**
 * Created by MY on 2016/10/9.
 */

public class BootCompleteReceiver extends BroadcastReceiver {
    SharedPreferences sp;
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("BootCompleteReceiver", "开机啦");
        sp = context.getSharedPreferences("config", MODE_PRIVATE);
        judgeIfSmsCorrect(context);
        boolean isShowAddress = sp.getBoolean(IS_SHOW_ADDRESS, false);
        if(isShowAddress){
            Intent serviceIntent = new Intent(context, AddressShowService.class);
            context.startService(serviceIntent);
        }
    }

    private void judgeIfSmsCorrect(Context context) {
        String simSerialNum1 = sp.getString(SIM_SERIAL_NUM, null);
        String safeNum = sp.getString(SAFE_NUM, null);
        if(!TextUtils.isEmpty(simSerialNum1)){
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            String simSerialNum2 = tm.getSimSerialNumber();
            if(!simSerialNum1.equals(simSerialNum2)){
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(safeNum, null, "大兄弟手机被偷了", null, null);
            }
        }
    }
}
