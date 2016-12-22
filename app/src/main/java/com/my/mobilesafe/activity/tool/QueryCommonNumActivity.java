package com.my.mobilesafe.activity.tool;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.my.mobilesafe.R;
import com.my.mobilesafe.activity.BaseActivity;
import com.my.mobilesafe.dao.CommonNumDao;
import com.my.mobilesafe.utils.FileUtil;

import java.io.File;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class QueryCommonNumActivity extends BaseActivity {

    @InjectView(R.id.elv)
    ExpandableListView elv;
    final String fileName = "commonnum.db";
    File file;
    CommonNumDao dao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common_num_query);
        ButterKnife.inject(this);

        file = new File(getFilesDir(), fileName);
        if (!file.exists()){
            FileUtil.copyAssetsFile(this, fileName);
        }

        dao = new CommonNumDao(file);
        elv.setAdapter(new MyAdapter());
    }

    private class MyAdapter extends BaseExpandableListAdapter {

        @Override
        public int getGroupCount() {
            return dao.getGroupCount();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return dao.getChildrenCount(groupPosition + 1);
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
            text = dao.getGroupName(groupPosition + 1);
            tv.setText("             " + text);
            return tv;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            TextView tv = new TextView(QueryCommonNumActivity.this);
            tv.setTextSize(16);
            String text = dao.getChildInfo(groupPosition+1, childPosition+1);
            tv.setText(text);
            return tv;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }
}
