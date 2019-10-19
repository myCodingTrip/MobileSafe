package com.my.mobilesafe.engine;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

import com.my.mobilesafe.bean.ContactPerson;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MY on 2016/10/9.
 * 获取联系人的业务类
 */

public class ContactEngine {

    public static List<ContactPerson> getContactInfoList(ContentResolver cr){

        List<ContactPerson> contactPersonList = new ArrayList<>();

        //常量CONTENT_URI是使用Uri.parse解析出来的结果
        Cursor cursor = cr.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null, null, null, null);

        //将联系人姓名和手机号逐个取出，并添加到contactsList中
        while(cursor.moveToNext()){
            String name = cursor.getString(cursor.getColumnIndex(
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String number = cursor.getString(cursor.getColumnIndex(
                    ContactsContract.CommonDataKinds.Phone.NUMBER));
            ContactPerson contactPerson = new ContactPerson();
            contactPerson.setName(name);
            contactPerson.setNumber(number);
            contactPersonList.add(contactPerson);
        }

        cursor.close();
        return contactPersonList;
    }
}
