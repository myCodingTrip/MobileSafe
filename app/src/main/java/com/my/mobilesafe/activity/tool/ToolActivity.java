package com.my.mobilesafe.activity.tool;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.my.mobilesafe.R;
import com.my.mobilesafe.activity.BaseActivity;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class ToolActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tool);
        ButterKnife.inject(this);
    }

    @OnClick({R.id.tv_query_phone_address, R.id.tv_backup_sms, R.id.tv_restore_sms, R.id.tv_app_lock, R.id.tv_common_num})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_query_phone_address:
                startActivity(new Intent(this, AddressQueryActivity.class));
                break;
            case R.id.tv_change_style:
                break;
            case R.id.tv_change_location:
                break;
            case R.id.tv_backup_sms:
                break;
            case R.id.tv_restore_sms:
                break;
            case R.id.tv_app_lock:
                break;
            case R.id.tv_common_num:
                break;
        }
    }
}
