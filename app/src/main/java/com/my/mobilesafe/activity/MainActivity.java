package com.my.mobilesafe.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.my.mobilesafe.R;
import com.my.mobilesafe.activity.lost.LostFoundActivity;
import com.my.mobilesafe.activity.setting.SettingCenterActivity;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class MainActivity extends BaseActivity {

    @InjectView(R.id.gridview)
    GridView gridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        GridViewAdapter gridViewAdapter = new GridViewAdapter();
        gridView.setAdapter(gridViewAdapter);
        gridView.setOnItemClickListener(new GridItemClickListener());
    }

    private class GridViewAdapter extends BaseAdapter{
        private String[] names = {"手机防盗","通信卫士","软件管理",
                                    "进程管理","流量统计", "手机杀毒",
                                    "缓存清理","高级工具","设置中心"};
        private int[] images = new int[]{
                R.mipmap.safe, R.mipmap.callmsgsafe, R.mipmap.app,
                R.mipmap.taskmanager, R.mipmap.netmanager, R.mipmap.trojan,
                R.mipmap.sysoptimize, R.mipmap.atools, R.mipmap.settings
        };

        @Override
        public int getCount() {
            return names.length;
        }

        @Override
        public Object getItem(int position) {
            return names[position];
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
//            TextView tv = new TextView(getApplicationContext());
//            tv.setText(names[position]);
            View view = getLayoutInflater().inflate(R.layout.item_main_gridview, null);
            ImageView iv = (ImageView) view.findViewById(R.id.iv_grid_item);
            TextView tv = (TextView) view.findViewById(R.id.tv_grid_item);
            tv.setText(names[position]);
            iv.setImageResource(images[position]);
            return view;
        }
    }

    //TODO  设置按下效果
    private class GridItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            switch (position){
                case 0:
                    Intent safeIntent = new Intent(getApplicationContext(), LostFoundActivity.class);
                    startActivity(safeIntent);
                    break;
                case 1:

                    break;
                case 2:

                    break;
                case 3:

                    break;
                case 4:

                    break;
                case 5:

                    break;
                case 6:

                    break;
                case 7:

                    break;
                case 8:
                    Intent settingIntent = new Intent(getApplicationContext(), SettingCenterActivity.class);
                    startActivity(settingIntent);
                    break;
            }
        }
    }
}
