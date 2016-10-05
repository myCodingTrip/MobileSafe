package com.my.mobilesafe.activity;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.my.mobilesafe.R;
import com.my.mobilesafe.utils.NetUtils;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by MY on 2016/10/4.
 */
public class SplashActivity extends Activity {

    @InjectView(R.id.tv_version)
    TextView tvVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash);
        ButterKnife.inject(this);

        String versionName = getVersionName();
        if(versionName != null)
            tvVersion.setText("版本号：" + versionName);

        checkNewVersion();
    }

    public String getVersionName(){
        try {
            PackageManager packageManager = getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(), 0);
            return packageInfo.versionName;
        }catch (Exception e){
            return null;
        }
    }

    public void checkNewVersion(){
        if(NetUtils.isConnected(this)){

        }else {

        }
    }
}
