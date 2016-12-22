package com.my.mobilesafe.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.my.mobilesafe.bean.Virus;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by MY on 2016/12/11.
 */

public class VirusDao {
    public static String fileName = "antivirus.db";

    /**
     * 获取所有的病毒信息
     * @param context
     * @return
     */
    public List<Virus> getAllViruses(Context context){
        List<Virus> viruses = new ArrayList<>();
        File dir = context.getFilesDir();
        File file = new File(dir, fileName);
        SQLiteDatabase db = SQLiteDatabase.openDatabase(file.getAbsolutePath(), null, SQLiteDatabase.OPEN_READONLY);
        Cursor cursor = db.query("datable", new String[]{"md5", "name"}, null, null, null, null, null);
        while (cursor.moveToNext()){
            String md5 = cursor.getString(0);
            String name = cursor.getString(1);
            Virus virus = new Virus();
            virus.setMd5Code(md5);
            virus.setPackageName(name);
            viruses.add(virus);
        }
        return viruses;
    }


}
