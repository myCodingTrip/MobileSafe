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

public class ContactInfoEngine {
    /**
     * 获取联系人
     * @param cr    访问内容提供者
     * @return
     */
    public List<ContactPerson> getContactInfoList1(ContentResolver cr){
        //1.访问raw_contacts获取联系人的id
        //2.根据联系人的id查询data表获取联系人名字
        //3.根据联系人的id 数据的type 获取到对应的数据(电话,email);
        List<ContactPerson> contactPersonList = new ArrayList<ContactPerson>();
        Uri uri = Uri.parse("content://com.android.contacts/raw_contacts");
        Uri dataUri = Uri.parse("content://com.android.contacts/data");
        Cursor cursor = cr.query(uri, null, null, null, null);
        ContactPerson contactPerson ;
        while (cursor.moveToNext()) {
            contactPerson = new ContactPerson();
            String id =	cursor.getString(cursor.getColumnIndex("_id"));
            String name = cursor.getString(cursor.getColumnIndex("display_name"));
            contactPerson.setName(name);
            Cursor dataCursor =  cr.query(dataUri, null, "raw_contact_id=?", new String[]{id}, null);
            while (dataCursor.moveToNext()) {
                //mimetype
                String type = dataCursor.getString(dataCursor.getColumnIndex("mimetype"));
                if("vnd.android.cursor.item/phone_v2".equals(type)){
                    String number = dataCursor.getString(dataCursor.getColumnIndex("data1"));
                    contactPerson.setNumber(number);
                }
            }
            dataCursor.close();
            contactPersonList.add(contactPerson);
            //contactPerson = null;
        }
        cursor.close();
        return contactPersonList;
    }

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
