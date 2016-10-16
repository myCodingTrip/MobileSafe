package com.my.mobilesafe.activity.lost;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.my.mobilesafe.R;
import com.my.mobilesafe.constant.SharedKey;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import static com.my.mobilesafe.constant.SharedKey.IS_PROTECT_OPEN;

public class ProtectInfoActivity extends AppCompatActivity {

    @InjectView(R.id.tv_safe_number)
    TextView tvSafeNumber;
    SharedPreferences sp;
    @InjectView(R.id.cb_is_protecting)
    CheckBox cbIsProtecting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_protect_info);
        ButterKnife.inject(this);
        sp = getSharedPreferences("config", MODE_PRIVATE);
        getSafeNum();
        boolean isProtect = sp.getBoolean(IS_PROTECT_OPEN, false);
        cbIsProtecting.setChecked(isProtect);
        setCheckBoxText();
    }

    private void setCheckBoxText() {
        if(cbIsProtecting.isChecked()){
            cbIsProtecting.setText("已开启防盗保护");
        }else {
            cbIsProtecting.setText("没有开启防盗保护");
        }
    }

    private void getSafeNum() {
        String num = sp.getString(SharedKey.SAFE_NUM, null);
        if (num != null) {
            tvSafeNumber.setText(getResources().getText(R.string.tv_sate_num) + num);
        }

    }

    @OnClick({R.id.cb_is_protecting, R.id.tv_reset_guide})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cb_is_protecting:
                setCheckBoxText();
                break;
            case R.id.tv_reset_guide:
                startActivity(new Intent(this, SetGuideActivity1.class));
                finish();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(IS_PROTECT_OPEN, cbIsProtecting.isChecked());
        editor.commit();
    }
}
