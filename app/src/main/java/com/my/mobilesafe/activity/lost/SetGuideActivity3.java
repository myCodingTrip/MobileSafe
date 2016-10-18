package com.my.mobilesafe.activity.lost;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.my.mobilesafe.R;
import com.my.mobilesafe.activity.BaseActivity;
import com.my.mobilesafe.constant.SharedKey;
import com.my.mobilesafe.utils.RegexUtil;
import com.my.mobilesafe.utils.ToastUtil;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by MY on 2016/10/9.
 */

public class SetGuideActivity3 extends BaseActivity {
    @InjectView(R.id.et_safe_number)
    EditText etSafeNumber;
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_guide3);
        ButterKnife.inject(this);
        sp = getSharedPreferences("config", MODE_PRIVATE);
        String safeNum = sp.getString(SharedKey.SAFE_NUM, null);
        if(!TextUtils.isEmpty(safeNum)){
            etSafeNumber.setText(safeNum);
            etSafeNumber.setSelection(safeNum.length());
        }
    }

    @OnClick({R.id.btn_select_contact, R.id.btn_previous, R.id.btn_next})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_select_contact:
                getContact();
                break;
            case R.id.btn_previous:
                startActivity(new Intent(this, SetGuideActivity2.class));
                finish();
                overridePendingTransition(R.anim.pre_in, R.anim.pre_out);
                break;
            case R.id.btn_next:
                String number = etSafeNumber.getText().toString();
                if (!RegexUtil.isMobileNum(number)){
                    ToastUtil.show(this, "请输入正确的手机号");
                }else {
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString(SharedKey.SAFE_NUM, number);
                    editor.commit();
                    startActivity(new Intent(this, SetGuideActivity4.class));
                    finish();

                }
                break;
        }
    }

    private void getContact() {
        Intent intent = new Intent(this, ContactListActivity.class);
        startActivityForResult(intent, 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 100 && resultCode == 200){
            String number = data.getStringExtra(ContactListActivity.NUMBER).trim();
            etSafeNumber.setText(number);
            etSafeNumber.setSelection(number.length());
        }
    }
}
