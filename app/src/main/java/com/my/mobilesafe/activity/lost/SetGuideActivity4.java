package com.my.mobilesafe.activity.lost;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;

import com.my.mobilesafe.R;
import com.my.mobilesafe.activity.BaseActivity;
import com.my.mobilesafe.constant.SharedKey;
import com.my.mobilesafe.receiver.MyAdmin;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class SetGuideActivity4 extends BaseActivity {

    @InjectView(R.id.cb_is_protecting)
    CheckBox cbIsProtecting;
    SharedPreferences sp;
    boolean isProtect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_guide4);
        ButterKnife.inject(this);

        sp = getSharedPreferences(SharedKey.CONFIG, MODE_PRIVATE);
        isProtect = sp.getBoolean(SharedKey.IS_LOST_PROTECT_OPEN, false);
        cbIsProtecting.setChecked(isProtect);
        setCbText();
    }

    @OnClick({R.id.cb_is_protecting, R.id.btn_previous, R.id.btn_next})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cb_is_protecting:
                isProtect = !isProtect;
                setCbText();
                break;
            case R.id.btn_previous:
                startActivity(new Intent(this, SetGuideActivity3.class));
                finish();
                overridePendingTransition(R.anim.pre_in, R.anim.pre_out);
                break;
            case R.id.btn_next:
                SharedPreferences.Editor editor = sp.edit();
                editor.putBoolean(SharedKey.IS_LOST_PROTECT_OPEN, isProtect);
                editor.putBoolean(SharedKey.SETTING_FINISH, true);
                editor.commit();
                startMyAdmin();
//                startActivity(new Intent(this, ProtectInfoActivity.class));
                finish();
                break;
        }
    }

    private void startMyAdmin() {
        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        // 指定要激活的组件
        ComponentName mDeviceAdminSample = new  ComponentName(this,
                MyAdmin.class);
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN,
                mDeviceAdminSample);
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                "开启后可以锁定屏幕。不开启这个功能扣200块钱");
        startActivity(intent);
    }

    private void setCbText(){
        if(isProtect){
            cbIsProtecting.setText("保护已开启");
        }else {
            cbIsProtecting.setText("保护没有开启");
        }
    }

}
