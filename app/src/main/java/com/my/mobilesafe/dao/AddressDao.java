package com.my.mobilesafe.dao;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.my.mobilesafe.R;
import com.my.mobilesafe.utils.RegexUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

/**
 * Created by MY on 2016/10/18.
 */

public class AddressDao {
    public static final String DB_NAME = "address.db";

    public static boolean isFileExist(Context context){
        File dir = context.getFilesDir();
        File file = new File(dir, DB_NAME);
        return file.exists();
    }

    public static void copyDB(Context context) throws Exception{
        AssetManager assetManager = context.getAssets();
        InputStream is = assetManager.open(DB_NAME);
        FileOutputStream fos = context.openFileOutput(DB_NAME, Context.MODE_PRIVATE);

        byte[] buffer = new byte[1024];
        int len = 0;
        while ((len = is.read(buffer)) != -1){
            fos.write(buffer, 0, len);
        }
        fos.close();
        is.close();
    }

    public static String getAddress(Context context, String num) {
        String address = null;
        File dir = context.getFilesDir();
        File file = new File(dir, AddressDao.DB_NAME);
        SQLiteDatabase db = SQLiteDatabase.openDatabase(file.getAbsolutePath(), null, SQLiteDatabase.OPEN_READONLY);
        if(RegexUtil.isMobileNum(num)){
            String prefix = num.substring(0, 7);
            Cursor cursor = db.rawQuery("select location from data2 where id = " +
                    "(select outkey from data1 where id=?)", new String[]{prefix});
            if(cursor.moveToFirst()){
                address = cursor.getString(0);
            }
            cursor.close();
        }
        if (address == null){
            address = context.getString(R.string.tv_unknown_num);
        }
        return address;
    }
}
