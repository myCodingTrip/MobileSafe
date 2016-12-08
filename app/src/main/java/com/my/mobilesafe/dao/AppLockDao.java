package com.my.mobilesafe.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.my.mobilesafe.db.AppLockDBHelper;

public class AppLockDao {
	private Context context;
	private AppLockDBHelper dbHelper;

	public AppLockDao(Context context) {
		this.context = context;
		dbHelper = new AppLockDBHelper(context);
	}

	/**
	 * 根据包名查询该app是否加锁
	 * @param packagename
	 * @return
     */
	public boolean find(String packagename) {
		boolean result = false;
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		if (db.isOpen()) {
			Cursor cursor = db.rawQuery(
					"select packagename from applock where packagename=?",
					new String[] { packagename });
			if (cursor.moveToNext()) {
				result = true;
			}
			cursor.close();
			db.close();
		}
		return result;
	}

	/**
	 * 为app加锁
	 */
	public void add(String packagename){
		if(find(packagename)){
			return ;
		}
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		if(db.isOpen()){
			db.execSQL("insert into applock (packagename) values (?)", new Object[]{packagename});
			db.close();
		}
	}

	/**
	 *
	 */
	public void delete(String packagename){
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		if(db.isOpen()){
			db.execSQL("delete from applock where packagename=?", new Object[]{packagename});
			db.close();
		}
	}
	
	/**
	 * 获取所有加锁的app
	 */
	public List<String> getAllApps(){
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		List<String> packageNames = new ArrayList<String>();
		if (db.isOpen()) {
		  Cursor cursor = db.rawQuery("select packagename from applock", null);
			while (cursor.moveToNext()) {
				String packageName = cursor.getString(0);
				packageNames.add(packageName);
			}
			cursor.close();
			db.close();
		}
		return packageNames;
	}
}
