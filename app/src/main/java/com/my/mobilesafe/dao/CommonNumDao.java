package com.my.mobilesafe.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.my.mobilesafe.utils.FileUtil;

import java.io.File;

/**
 * Created by MY on 2016/12/11.
 */

public class CommonNumDao {

    File file;

    public CommonNumDao(File file){
        this.file = file;
    }

    /**
     * 获取分组的数目
     * @return
     */
    public int getGroupCount(){
        int count = 0;
        SQLiteDatabase db = SQLiteDatabase.openDatabase(file.getAbsolutePath(), null, SQLiteDatabase.OPEN_READONLY);
        if(db.isOpen()){
            Cursor cursor = db.rawQuery("select count(*) from classlist", null);
            if(cursor.moveToFirst()){
                count = cursor.getInt(0);
            }
            cursor.close();
            db.close();
        }
        return count;
    }

    public int getChildrenCount(int tableIndex){
        int count=0;
        //table1-table8共8张表
        String sql = "select count(*) from table" + tableIndex;

        SQLiteDatabase db = SQLiteDatabase.openDatabase(file.getAbsolutePath(), null, SQLiteDatabase.OPEN_READONLY);
        if(db.isOpen()){
            Cursor cursor = db.rawQuery(sql, null);
            if(cursor.moveToFirst()){
                count = cursor.getInt(0);
            }
            cursor.close();
            db.close();
        }
        return count;
    }

    public String getGroupName(int currentPosition){
        String text = "";
        SQLiteDatabase db = SQLiteDatabase.openDatabase(file.getAbsolutePath(), null, SQLiteDatabase.OPEN_READONLY);
        if(db.isOpen()){
            Cursor cursor = db.rawQuery("select name from classlist where idx=?", new String[]{currentPosition+""});
            if(cursor.moveToFirst()){
                text = cursor.getString(0);
            }
            cursor.close();
            db.close();
        }
        return text;
    }

    /**
     * 得到第tableIndex组第childIndex条信息
     * @param tableIndex
     * @param childIndex
     * @return
     */
    public String getChildInfo(int tableIndex, int childIndex){
        String sql = "select number, name from table" + tableIndex;
        StringBuilder sb = new StringBuilder();
        SQLiteDatabase db = SQLiteDatabase.openDatabase(file.getAbsolutePath(), null, SQLiteDatabase.OPEN_READONLY);
        if(db.isOpen()){
            Cursor cursor = db.rawQuery(sql+ " where _id=?", new String[]{childIndex+""});
            if(cursor.moveToFirst()){
                sb.append(	cursor.getString(0)); //number
                sb.append(" : ");
                sb.append(	cursor.getString(1)); //name
            }
            cursor.close();
            db.close();
        }
        return sb.toString();
    }
}
