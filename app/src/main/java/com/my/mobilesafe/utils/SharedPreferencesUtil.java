package com.my.mobilesafe.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.my.mobilesafe.constant.SharedKey;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by MY on 2016/10/13.
 */

public class SharedPreferencesUtil {
    SharedPreferences sp;

    public SharedPreferencesUtil(Context context) {
        sp = context.getSharedPreferences("config", MODE_PRIVATE);
    }


}
