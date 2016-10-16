package com.my.mobilesafe.activity.setting;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.my.mobilesafe.R;
import com.my.mobilesafe.activity.BaseActivity;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class SettingCenterActivity extends BaseActivity {

    @InjectView(R.id.tv_update_state)
    TextView tvUpdateState;
    @InjectView(R.id.cb_update_setting)
    CheckBox cbUpdateSetting;
    SharedPreferences sp;
    public static final String IS_AUTO_UPDATE = "isAutoUpdate";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_center);
        ButterKnife.inject(this);

        sp = getSharedPreferences("config", MODE_PRIVATE);
        boolean isAutoUpdate = sp.getBoolean(IS_AUTO_UPDATE, true);
        if (isAutoUpdate){
            tvUpdateState.setText(R.string.tv_auto_update_open);
            cbUpdateSetting.setChecked(true);
        }else {
            tvUpdateState.setText(R.string.tv_auto_update_closed);
            cbUpdateSetting.setChecked(false);
        }

        cbUpdateSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isChecked = cbUpdateSetting.isChecked();
                SharedPreferences.Editor editor = sp.edit();
                editor.putBoolean(IS_AUTO_UPDATE, isChecked);
                editor.commit();
                if(isChecked){
                    tvUpdateState.setText(R.string.tv_auto_update_open);
                }else {
                    tvUpdateState.setText(R.string.tv_auto_update_closed);
                }
            }
        });
    }
}
