package com.my.mobilesafe.activity.tool;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.my.mobilesafe.R;
import com.my.mobilesafe.service.WatchDogService;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class EnterAppActivity extends AppCompatActivity {
    PackageManager pm;
    @InjectView(R.id.tv_app_name)
    TextView tvAppName;
    @InjectView(R.id.img_app_icon)
    ImageView imgAppIcon;
    @InjectView(R.id.et_password)
    EditText etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_app);
        ButterKnife.inject(this);

        pm = getPackageManager();
        String packageName = getIntent().getStringExtra(WatchDogService.PACKAGE_NAME);

        ApplicationInfo applicationInfo = null;
        try {
            applicationInfo = pm.getApplicationInfo(packageName, 0);
            Drawable icon = applicationInfo.loadIcon(pm);
            String label = applicationInfo.loadLabel(pm).toString();

            tvAppName.setText(label);
            imgAppIcon.setImageDrawable(icon);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.btn_ok)
    public void onClick() {
        String password = etPassword.getText().toString().trim();
    }
}
