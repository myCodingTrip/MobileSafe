package com.my.mobilesafe.activity.setting;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.my.mobilesafe.R;
import com.my.mobilesafe.activity.BaseActivity;
import com.my.mobilesafe.constant.SharedKey;
import com.my.mobilesafe.service.AddressShowService;
import com.my.mobilesafe.service.BlackListService;
import com.my.mobilesafe.service.WatchDogService;
import com.my.mobilesafe.utils.SettingUtil;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import static com.my.mobilesafe.constant.SharedKey.ADDRESS_STYLE;

public class SettingCenterActivity extends BaseActivity {

    SharedPreferences sp;
    @InjectView(R.id.tv_update_state)
    TextView tvUpdateState;
    @InjectView(R.id.cb_update_setting)
    CheckBox cbUpdateSetting;
    @InjectView(R.id.tv_address_state)
    TextView tvAddressState;
    @InjectView(R.id.tv_blacklist_state)
    TextView tvBlacklistState;
    @InjectView(R.id.tv_applock_state)
    TextView tvApplockState;
    @InjectView(R.id.cb_address_setting)
    CheckBox cbAddressSetting;
    @InjectView(R.id.cb_blacklist_setting)
    CheckBox cbBlacklistSetting;
    @InjectView(R.id.cb_applock_setting)
    CheckBox cbApplockSetting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_center);
        ButterKnife.inject(this);

        sp = getSharedPreferences("config", MODE_PRIVATE);
        initView();
    }

    private void initView() {
        boolean isAutoUpdate = sp.getBoolean(SharedKey.IS_AUTO_UPDATE, false);
        boolean isShowAddress = sp.getBoolean(SharedKey.IS_SHOW_ADDRESS, false);
        boolean isBlacklistIntercept = sp.getBoolean(SharedKey.IS_BLACKLIST_INTERCEPT, false);
        boolean isApplockOpen = sp.getBoolean(SharedKey.IS_APPLOCK_OPEN, false);

        SettingUtil.set(isAutoUpdate, cbUpdateSetting,
                tvUpdateState, getString(R.string.tv_auto_update_open), getString(R.string.tv_auto_update_closed));
        SettingUtil.set(isShowAddress, cbAddressSetting,
                tvAddressState, getString(R.string.tv_address_open), getString(R.string.tv_address_closed));
        SettingUtil.set(isBlacklistIntercept, cbBlacklistSetting,
                tvBlacklistState, getString(R.string.tv_blacklist_intercept_open), getString(R.string.tv_blacklist_intercept_closed));
        SettingUtil.set(isApplockOpen, cbApplockSetting,
                tvApplockState, getString(R.string.tv_applock_open), getString(R.string.tv_applock_closed));
    }


    @OnClick({R.id.cb_update_setting, R.id.cb_address_setting, R.id.cb_blacklist_setting,
            R.id.cb_applock_setting, R.id.tv_change_style, R.id.tv_change_location})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cb_update_setting:
                if (cbUpdateSetting.isChecked()) {
                    tvUpdateState.setText(R.string.tv_auto_update_open);
                } else {
                    tvUpdateState.setText(R.string.tv_auto_update_closed);
                }
                break;
            case R.id.cb_address_setting:
                if (cbAddressSetting.isChecked()) {
                    tvAddressState.setText(R.string.tv_address_open);
                    Intent serviceIntent = new Intent(this, AddressShowService.class);
                    startService(serviceIntent);
                } else {
                    tvAddressState.setText(R.string.tv_address_closed);
                    Intent serviceIntent = new Intent(this, AddressShowService.class);
                    stopService(serviceIntent);
                }
                break;
            case R.id.cb_blacklist_setting:
                if (cbBlacklistSetting.isChecked()) {
                    tvBlacklistState.setText(R.string.tv_blacklist_intercept_open);
                    Intent serviceIntent = new Intent(this, BlackListService.class);
                    startService(serviceIntent);
                } else {
                    tvBlacklistState.setText(R.string.tv_blacklist_intercept_closed);
                    Intent serviceIntent = new Intent(this, BlackListService.class);
                    stopService(serviceIntent);
                }
                break;
            case R.id.cb_applock_setting:
                Intent intent = new Intent(this, WatchDogService.class);
                if (cbApplockSetting.isChecked()) {
                    tvApplockState.setText(R.string.tv_applock_open);
                    startService(intent);
                } else {
                    tvApplockState.setText(R.string.tv_applock_closed);
                    stopService(intent);
                }
                break;
            case R.id.tv_change_style:
                final String[] items = new String[]{"透明", "卫士蓝", "金属灰", "苹果绿", "屎黄黄"};
                int checkedItem = 0;
                //TODO 获得默认的选择
//                for (int i=0; i<items.length; i++){
//                    if(items[i].equals(style))
//                    checkedItem = i;
//                }
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("归属地提示风格");
                builder.setSingleChoiceItems(items, checkedItem, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String selected = items[which];//选中的风格
                        //TODO .9图片不规范，无法完成选择风格功能
                        int styleResId = 0;
                        switch (which){
                            case 0:
                                break;
                            case 1:
                                break;
                            case 2:
                                break;
                            case 3:
                                break;
                            case 4:
                                break;
                            case 5:
                                break;

                        }

                        SharedPreferences.Editor editor = sp.edit();
                        editor.putInt(ADDRESS_STYLE, styleResId);
                        editor.commit();
                        tvAddressState.setText(selected);
                        dialog.cancel();
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
                break;
            case R.id.tv_change_location:
                startActivity(new Intent(this, ChangeAddressLocationActivity.class));
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        putBooleans();
    }

    /**
     * 保存自动更新、归属地显示、黑名单拦截和程序锁的设置
     */
    private void putBooleans() {
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(SharedKey.IS_AUTO_UPDATE, cbUpdateSetting.isChecked());
        editor.putBoolean(SharedKey.IS_SHOW_ADDRESS, cbAddressSetting.isChecked());
        editor.putBoolean(SharedKey.IS_BLACKLIST_INTERCEPT, cbBlacklistSetting.isChecked());
        editor.putBoolean(SharedKey.IS_APPLOCK_OPEN, cbApplockSetting.isChecked());
        editor.commit();
    }

}
