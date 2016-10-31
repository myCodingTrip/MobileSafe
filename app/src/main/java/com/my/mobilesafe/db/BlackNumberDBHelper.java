package com.my.mobilesafe.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by MY on 2016/10/26.
 */

public class BlackNumberDBHelper extends SQLiteOpenHelper {
    private static BlackNumberDBHelper blackListHelper;
    static final String dbName = "black.db";
    public synchronized static BlackNumberDBHelper getInstance(Context context){
        if(blackListHelper == null)
            blackListHelper = new BlackNumberDBHelper(context, dbName, null, 1);
        return blackListHelper;
    }

    private BlackNumberDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE blacknumber (_id integer primary key autoincrement, " +
                "number text, " +
                "type integer)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
