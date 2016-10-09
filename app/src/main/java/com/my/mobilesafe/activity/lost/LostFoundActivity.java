package com.my.mobilesafe.activity.lost;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.my.mobilesafe.R;
import com.my.mobilesafe.activity.BaseActivity;
import com.my.mobilesafe.utils.ToastUtil;


public class LostFoundActivity extends BaseActivity {
    SharedPreferences sp;
    private final String PASSWORD = "password";
    String password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sp = getSharedPreferences("config", Context.MODE_PRIVATE);
        password = sp.getString(PASSWORD, null);
        if (TextUtils.isEmpty(password)) {
            setPassword();
        } else {
            verifyPassword();
        }
    }

    private void setPassword() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_lost_found_set_password, null, false);
        builder.setView(view);
        builder.setCancelable(false);
        final AlertDialog dialog = builder.create();
        dialog.show();

        final EditText etInputPassword = (EditText) view.findViewById(R.id.et_input_password);
        final EditText etConfirmPassword = (EditText) view.findViewById(R.id.et_confirm_password);
        Button btnOk = (Button) view.findViewById(R.id.btn_ok);
        Button btnCancel = (Button) view.findViewById(R.id.btn_cancel);

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputPassword = etInputPassword.getText().toString().trim();
                String confirmPassword = etConfirmPassword.getText().toString().trim();
                if (TextUtils.isEmpty(inputPassword)){
                    ToastUtil.show(getApplicationContext(), "密码不能为空");
                }else if(!inputPassword.equals(confirmPassword)){
                    ToastUtil.show(getApplicationContext(), "密码不相同");
                }else {
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString(PASSWORD, inputPassword);
                    editor.commit();
                    dialog.dismiss();
                    finish();
                }
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                finish();
            }
        });
    }

    private void verifyPassword() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_lost_found_verify_password, null, false);
        builder.setView(view);
        builder.setCancelable(false);
        final AlertDialog dialog = builder.create();
        dialog.show();

        final EditText etInputPassword = (EditText) view.findViewById(R.id.et_input_password);
        Button btnOk = (Button) view.findViewById(R.id.btn_ok);
        Button btnCancel = (Button) view.findViewById(R.id.btn_cancel);

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputPassword = etInputPassword.getText().toString().trim();
                if (TextUtils.isEmpty(inputPassword)){
                    ToastUtil.show(getApplicationContext(), "密码不能为空");
                }else if(inputPassword.equals(password)){
                    Intent intent = new Intent(getApplicationContext(), SetGuideActivity1.class);
                    startActivity(intent);
                    dialog.dismiss();
                    finish();
                }else {
                    ToastUtil.show(getApplicationContext(), "密码错误");
                }
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                finish();
            }
        });
    }

}
