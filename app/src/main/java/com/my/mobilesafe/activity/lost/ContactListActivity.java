package com.my.mobilesafe.activity.lost;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.my.mobilesafe.R;
import com.my.mobilesafe.activity.BaseActivity;
import com.my.mobilesafe.bean.ContactPerson;
import com.my.mobilesafe.engine.ContactInfoEngine;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 *
 */
public class ContactListActivity extends BaseActivity {

    @InjectView(R.id.lv_contact)
    ListView lvContact;
    @InjectView(R.id.rl_loading)
    RelativeLayout rlLoading;
    public static final String NUMBER = "number";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);
        ButterKnife.inject(this);
        showContactInfo();
    }

    private void showContactInfo() {
        List<ContactPerson> contactPersons = ContactInfoEngine.getContactInfoList(getContentResolver());
        rlLoading.setVisibility(View.INVISIBLE);
        final ArrayAdapter<ContactPerson> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, contactPersons);
        lvContact.setAdapter(adapter);
        lvContact.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String number = adapter.getItem(position).getNumber();
                Intent intent = new Intent();
                intent.putExtra(NUMBER, number);
                setResult(200 ,intent);
                finish();
            }
        });
    }

}
