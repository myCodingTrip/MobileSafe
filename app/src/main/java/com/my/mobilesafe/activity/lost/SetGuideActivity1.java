package com.my.mobilesafe.activity.lost;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.my.mobilesafe.R;
import com.my.mobilesafe.activity.BaseActivity;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class SetGuideActivity1 extends BaseActivity {

    @InjectView(R.id.btn_next)
    Button btnNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_guide1);
        ButterKnife.inject(this);
    }

    @OnClick(R.id.btn_next)
    public void onClick() {
        Intent intent = new Intent(this, SetGuideActivity2.class);
        startActivity(intent);
        finish();
    }
}
