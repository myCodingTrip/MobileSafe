package com.my.mobilesafe.activity.tool;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;

import com.my.mobilesafe.R;
import com.my.mobilesafe.dao.AddressDao;
import com.my.mobilesafe.utils.RegexUtil;

import java.io.File;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class AddressQueryActivity extends AppCompatActivity {

    @InjectView(R.id.et_query_number)
    EditText etQueryNumber;
    @InjectView(R.id.tv_address)
    TextView tvAddress;
    String TAG = "AddressQueryActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_query);
        ButterKnife.inject(this);

        if(!AddressDao.isFileExist(this)){
            try {
                AddressDao.copyDB(this);
            } catch (Exception e) {
                Log.d(TAG, "文件拷贝失败");
                e.printStackTrace();
            }
        }else Log.d(TAG, "文件已存在");

        //设置号码动态查询功能
        etQueryNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String num = s.toString();
                String address = null;
                if(s.length() < 7){
                    address = getString(R.string.tv_unknown_num);;
                }else if(s.length() >= 7){
                    address = AddressDao.getAddress(getApplicationContext(), num);
                }
                tvAddress.setText(address);
            }
        });
    }

    @OnClick(R.id.btn_query)
    public void onClick() {
        String num = etQueryNumber.getText().toString();
        if (TextUtils.isEmpty(num)){
            Animation animation = AnimationUtils.loadAnimation(this, R.anim.shake);
            etQueryNumber.startAnimation(animation);
        }else {
            String address = AddressDao.getAddress(this, num);
            tvAddress.setText(address);
        }
    }

}
