package com.my.mobilesafe.activity.lost;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.my.mobilesafe.R;
import com.my.mobilesafe.activity.BaseActivity;
import com.my.mobilesafe.constant.SharedKey;
import com.my.mobilesafe.utils.ToastUtil;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class SetGuideActivity2 extends BaseActivity {
    SharedPreferences sp;
    boolean hasBind = true;
    @InjectView(R.id.btn_bind_sim)
    Button btnBindSim;
    @InjectView(R.id.img_lock)
    ImageView imgLock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_guide2);
        ButterKnife.inject(this);

        imgLock.bringToFront();
        sp = getSharedPreferences("config", MODE_PRIVATE);
        String simSerialNum = sp.getString(SharedKey.SIM_SERIAL_NUM, null);
        if (TextUtils.isEmpty(simSerialNum)) {
            hasBind = false;
        }
        updateBtnView();
    }

    private void updateBtnView() {
        if (!hasBind) {
            btnBindSim.setText("点击绑定SIM卡");
            imgLock.setImageResource(R.mipmap.lock);
        } else {
            btnBindSim.setText("点击解绑SIM卡");
            imgLock.setImageResource(R.mipmap.unlock);
        }
    }

    @OnClick({R.id.btn_bind_sim, R.id.btn_previous, R.id.btn_next})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_bind_sim:
                if (!hasBind) bindSim();
                else unbindSim();
                break;
            case R.id.btn_previous:
                startActivity(new Intent(this, SetGuideActivity1.class));
                finish();
                overridePendingTransition(R.anim.pre_in, R.anim.pre_out);
                break;
            case R.id.btn_next:
                startActivity(new Intent(this, SetGuideActivity3.class));
                finish();
                break;
        }
    }

    private void bindSim() {
        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String simSerialNum = tm.getSimSerialNumber();
        if (simSerialNum == null) {
            ToastUtil.show(this, "SIM卡不存在");
            return;
        }
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(SharedKey.SIM_SERIAL_NUM, simSerialNum);
        editor.commit();
        hasBind = true;
        updateBtnView();
    }

    private void unbindSim() {
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(SharedKey.SIM_SERIAL_NUM, null);
        editor.commit();
        hasBind = false;
        updateBtnView();
    }
}
