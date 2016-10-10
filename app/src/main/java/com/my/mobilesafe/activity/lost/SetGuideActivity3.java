package com.my.mobilesafe.activity.lost;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.my.mobilesafe.R;
import com.my.mobilesafe.activity.BaseActivity;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by MY on 2016/10/9.
 */

public class SetGuideActivity3 extends BaseActivity {
    @InjectView(R.id.et_safe_number)
    EditText etSafeNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_guide3);
        ButterKnife.inject(this);
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
                break;
            case R.id.btn_next:
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
