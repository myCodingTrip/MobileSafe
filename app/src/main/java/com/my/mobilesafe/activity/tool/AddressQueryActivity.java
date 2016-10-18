package com.my.mobilesafe.activity.tool;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.TextView;

import com.my.mobilesafe.R;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class AddressQueryActivity extends AppCompatActivity {

    @InjectView(R.id.et_query_number)
    EditText etQueryNumber;
    @InjectView(R.id.tv_address)
    TextView tvAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_query);
        ButterKnife.inject(this);
    }

    @OnClick(R.id.btn_query)
    public void onClick() {

    }
}
