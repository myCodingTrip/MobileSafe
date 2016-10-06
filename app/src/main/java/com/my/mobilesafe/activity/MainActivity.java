package com.my.mobilesafe.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.my.mobilesafe.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class MainActivity extends AppCompatActivity {

    @InjectView(R.id.gridview)
    GridView gridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        GridViewAdapter gridViewAdapter = new GridViewAdapter();
        gridView.setAdapter(gridViewAdapter);
    }

    private class GridViewAdapter extends BaseAdapter{
        private String[] names = {"手机防盗","通信卫士","软件管理",
                                    "进程管理","流量统计", "手机杀毒",
                                    "缓存清理","高级工具","设置中心"};
        private int[] images = new int[]{
                R.drawable.safe, R.drawable.callmsgsafe, R.drawable.app,
                R.drawable.taskmanager, R.drawable.netmanager, R.drawable.trojan,
                R.drawable.sysoptimize, R.drawable.atools, R.drawable.settings
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
            View view = getLayoutInflater().inflate(R.layout.item_gv, null);
            ImageView iv = (ImageView) view.findViewById(R.id.iv_grid_item);
            TextView tv = (TextView) view.findViewById(R.id.tv_grid_item);
            tv.setText(names[position]);
            iv.setImageResource(images[position]);
            return view;
        }
    }
}
