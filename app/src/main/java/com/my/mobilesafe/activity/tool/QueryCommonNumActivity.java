package com.my.mobilesafe.activity.tool;

import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.my.mobilesafe.R;
import com.my.mobilesafe.activity.BaseActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class QueryCommonNumActivity extends BaseActivity {

    @InjectView(R.id.elv)
    ExpandableListView elv;
    final String fileName = "CommonNum.db";
    final String fileDirectory = "/sdcard/" + fileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common_num_query);
        ButterKnife.inject(this);

        // 判断这个CommonNum.db的数据库是否被放置到了sd卡上
        // 如果不在sd卡上 要把db从asset目录拷贝到数据库
        File file = new File(fileDirectory);
        if (!file.exists()) {
            copyFileToSdcard();
        }
        elv.setAdapter(new MyAdapter());
    }

    private void copyFileToSdcard() {
        AssetManager manager = getAssets();
        try {
            InputStream is = manager.open(fileName);
            File file = new File(fileDirectory);
            FileOutputStream fos = new FileOutputStream(file);
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = is.read(buffer)) != -1) {
                fos.write(buffer, 0, len);
            }
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class MyAdapter extends BaseExpandableListAdapter {

        @Override
        public int getGroupCount() {
            int count = 0;
            SQLiteDatabase db = SQLiteDatabase.openDatabase(fileDirectory, null, SQLiteDatabase.OPEN_READONLY);
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

        @Override
        public int getChildrenCount(int groupPosition) {
            int count=0;
            int tableIndex = groupPosition + 1;
            String sql = "select count(*) from table" + tableIndex;

            SQLiteDatabase db = SQLiteDatabase.openDatabase(fileDirectory, null, SQLiteDatabase.OPEN_READONLY);
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

        @Override
        public Object getGroup(int groupPosition) {
            return null;
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return null;
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            TextView tv = new TextView(QueryCommonNumActivity.this);
            tv.setTextSize(18);
            String text = "";
            int currentPosition = groupPosition + 1;
            SQLiteDatabase db = SQLiteDatabase.openDatabase(fileDirectory, null, SQLiteDatabase.OPEN_READONLY);
            if(db.isOpen()){
                Cursor cursor = db.rawQuery("select name from classlist where idx=?", new String[]{currentPosition+""});
                if(cursor.moveToFirst()){
                    text = cursor.getString(0);
                }
                cursor.close();
                db.close();
            }
            tv.setText("             "+text);
            return tv;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            TextView tv = new TextView(QueryCommonNumActivity.this);
            tv.setTextSize(16);
            StringBuilder sb = new StringBuilder();
            int tableIndex = groupPosition + 1;
            int childIndex = childPosition + 1;
            String sql = "select number,name from table" + tableIndex;

            SQLiteDatabase db = SQLiteDatabase.openDatabase(fileDirectory, null, SQLiteDatabase.OPEN_READONLY);
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
            String text = sb.toString();
            tv.setText(text);
            return tv;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }
}
