package com.my.mobilesafe.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.my.mobilesafe.bean.BlackNumber;
import com.my.mobilesafe.db.BlackNumberDBHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MY on 2016/10/26.
 */

public class BlackNumberDao {
    private BlackNumberDBHelper dbHelper;
    public static String TABLE_NAME = "blacknumber";

    public BlackNumberDao(Context context) {
        dbHelper = BlackNumberDBHelper.getInstance(context);
    }

    /**
     * 判断是否是短信黑名单号码
     */
    public boolean isSmsBlack(String number) {
        boolean result = false;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        if (db.isOpen()) {
//            Cursor cursor = db.rawQuery(
//                    "select number from blacknumber where number=?",
//                    new String[] { number });
            String selection = "number=?";
            String[] selectionArgs = new String[]{number};
            Cursor cursor = db.query(TABLE_NAME, new String[]{"*"},
                    selection, selectionArgs, null, null, null);
            if (cursor.moveToFirst()) {
                int type = cursor.getInt(2);
                switch (type){
                    case BlackNumber.BLACK_SMS://短信
                        result = true;
                        break;
                    case BlackNumber.BLACK_PHONE://电话
                        result = false;
                        break;
                    case BlackNumber.BLACK_ALL://短信+电话
                        result = true;
                        break;
                }
            }
            cursor.close();
            db.close();
        }
        return result;
    }

    /**
     * 判断是否是电话黑名单号码
     */
    public boolean isCallBlack(String number) {
        boolean result = false;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        if (db.isOpen()) {
//            Cursor cursor = db.rawQuery(
//                    "select number from blacknumber where number=?",
//                    new String[] { number });
            String selection = "number=?";
            String[] selectionArgs = new String[]{number};
            Cursor cursor = db.query(TABLE_NAME, new String[]{"*"},
                    selection, selectionArgs, null, null, null);
            if (cursor.moveToFirst()) {
                int type = cursor.getInt(2);
                switch (type){
                    case BlackNumber.BLACK_SMS://短信
                        result = false;
                        break;
                    case BlackNumber.BLACK_PHONE://电话
                        result = true;
                        break;
                    case BlackNumber.BLACK_ALL://短信+电话
                        result = true;
                        break;
                }
            }
            cursor.close();
            db.close();
        }
        return result;
    }

    /**
     * 判断是否是黑名单号码
     */
    public boolean isBlackNum(String number) {
        if (isCallBlack(number) || isSmsBlack(number)) return true;
        else return false;
    }

    /**
     * 查找全部拦截号码
     */
    public List<BlackNumber> queryAll(){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<BlackNumber> numbers = new ArrayList<>();
        if (db.isOpen()) {
            //Cursor cursor = db.rawQuery("select number from blacknumber", null);
            Cursor cursor = db.query(TABLE_NAME, new String[]{"*"}, null, null,null,null,null);
            while (cursor.moveToNext()) {
                String _id = cursor.getString(0);
                String number = cursor.getString(1);
                int type = cursor.getInt(2);
                numbers.add(new BlackNumber(_id, number, type));
            }
            cursor.close();
            db.close();
        }
        return numbers;
    }

    /**
     * 添加
     */
    public void add(BlackNumber info){
        if(isBlackNum(info.number)){
            return ;
        }
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if(db.isOpen()){
            //db.execSQL("insert into blacknumber (number) values (?)", new Object[]{number});
            ContentValues values = new ContentValues();
            values.put("number", info.number);
            values.put("type", info.type);
            db.insert(TABLE_NAME, null, values);
            db.close();
        }
    }

    /**
     * 删除
     */
    public void delete(String _id){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if(db.isOpen()){
            //db.execSQL("delete from blacknumber where number=?", new Object[]{number});
            String whereClause = "_id = ?";
            String[] whereArgs = new String[]{_id};
            db.delete(TABLE_NAME, whereClause, whereArgs);
            db.close();
        }
    }

    /**
     * 删除全部拦截号码
     */
    public void deleteAll(){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        if (db.isOpen()) {
            //Cursor cursor = db.rawQuery("select number from blacknumber", null);
            Cursor cursor = db.query(TABLE_NAME, new String[]{"*"}, null, null, null, null, null);
            while (cursor.moveToNext()) {
                String _id = cursor.getString(0);
                delete(_id);
            }
            cursor.close();
            db.close();
        }
    }

    /**
     * 更新操作
     * @param oldNumber 旧的号码
     * @param newNumber 新的号码
     */
    public void update(String oldNumber ,String newNumber){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if(db.isOpen()){
            db.execSQL("update blacknumber set number=? where number=?  ", new Object[]{newNumber,oldNumber});
            db.close();
        }
    }


    /**
     * 分页查找
     * @param startId 开始的行号
     * @param length 每次加载记录数量
     * @return
     */
    public List<BlackNumber> queryLimit(int startId, int length){
        List<BlackNumber> numbers = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        if (db.isOpen()) {
            Cursor cursor = db.query(TABLE_NAME, new String[]{"*"}, null, null, null, null, null, startId+","+length);
            while (cursor.moveToNext()) {
                String _id = cursor.getString(0);
                String number = cursor.getString(1);
                int type = cursor.getInt(2);
                numbers.add(new BlackNumber(_id, number, type));
            }
            cursor.close();
            db.close();
        }
        return numbers;
    }

    public int getCount(){
        int count = 0;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        if (db.isOpen()) {
            Cursor cursor = db.query(TABLE_NAME, new String[]{"*"}, null, null, null, null, null, null);
            count = cursor.getCount();
            cursor.close();
            db.close();
        }
        return count;
    }
}
