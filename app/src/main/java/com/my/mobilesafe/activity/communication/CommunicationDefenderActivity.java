package com.my.mobilesafe.activity.communication;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.my.mobilesafe.R;
import com.my.mobilesafe.bean.BlackNumber;
import com.my.mobilesafe.dao.BlackNumberDao;
import com.my.mobilesafe.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class CommunicationDefenderActivity extends AppCompatActivity {
    String TAG = "CommProActivity";
    BlackNumberDao dao;
    @InjectView(R.id.lv_black_list)
    ListView lvBlackList;
    List<BlackNumber> numbers;
    MyAdapter adapter;
    int startId = 0;//当前加载的页数
    int length = 15;//每次加载的条数
    int total;//总共的条数
    View footerView;
    boolean isLoading = false;
    public static final int UPDATE_ADAPTER = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_communication_protect);
        ButterKnife.inject(this);
        dao = new BlackNumberDao(getApplicationContext());
        numbers = new ArrayList<>();

        //增加100条记录用于测试
//        for (int i = 0; i < 100; i++) {
//            dao.add(new BlackNumber("", i + "", i%3));
//        }

        adapter = new MyAdapter();
        footerView = getLayoutInflater().inflate(R.layout.footer, null);
        //赋予能力
        lvBlackList.addFooterView(footerView);
        lvBlackList.setAdapter(adapter);
        lvBlackList.removeFooterView(footerView);
        lvBlackList.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                switch (scrollState){
                    case AbsListView.OnScrollListener.SCROLL_STATE_IDLE://静止
                        break;
                    case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL://慢慢滑动
                        break;
                    case AbsListView.OnScrollListener.SCROLL_STATE_FLING://快速滚动
                        break;
                }
            }

            //每次滑动都会调用
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                //滑动到最后
                if(firstVisibleItem + visibleItemCount == totalItemCount){
                    if(!isLoading){
                        //没有全部加载完成
                        if(numbers.size() < dao.getCount()){
                            isLoading = true;
                            lvBlackList.addFooterView(footerView);
                            loadData();
                        }

                    }
                }
            }

            private void loadData() {
                new Thread(){
                    @Override
                    public void run() {
                        //模拟延时2s
                        SystemClock.sleep(2000);
                        //加载数据
                        startId+=length;
                        numbers.addAll(dao.queryLimit(startId, length));
                        isLoading = false;

                        Message message = Message.obtain();
                        message.what = UPDATE_ADAPTER;
                        mHandler.sendMessage(message);
                    }
                }.start();
            }
        });
    }

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case UPDATE_ADAPTER:
                    adapter.notifyDataSetChanged();
                    lvBlackList.removeFooterView(footerView);
                    break;
            }
        }
    };

    @OnClick({R.id.btn_add_black_number, R.id.btn_clear})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_add_black_number:
                showAddBlackNumDialog();
                break;
            case R.id.btn_clear:
                //TODO 耗时，添加多线程
                dao.deleteAll();
                numbers = dao.queryAll();
                adapter.notifyDataSetChanged();
                break;
        }
    }

    private void showAddBlackNumDialog() {
//        View.inflate()
//        getLayoutInflater()
//        LayoutInflater.from(this).inflate() 最终使用

        View view = getLayoutInflater().inflate(R.layout.dialog_input_black_num, null);
        final EditText etBlackNum = (EditText) view.findViewById(R.id.et_black_num);
        final CheckBox cbPhoneBlack = (CheckBox) view.findViewById(R.id.cb_phone_black);
        final CheckBox cbSmsBlack = (CheckBox) view.findViewById(R.id.cb_sms_black);
        Button btnOk = (Button) view.findViewById(R.id.btn_ok);
        Button btnCancel = (Button) view.findViewById(R.id.btn_cancel);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);
        builder.setCancelable(false);
        final AlertDialog dialog = builder.create();
        dialog.show();

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String blackNum = etBlackNum.getText().toString().trim();
                if (TextUtils.isEmpty(blackNum)) {
                    ToastUtil.show(getApplicationContext(), "号码为空");
                    return;
                } else if (dao.isBlackNum(blackNum)) {
                    ToastUtil.show(getApplicationContext(), "号码已存在");
                    return;
                }
                int type = -1;
                if (cbPhoneBlack.isChecked() && cbSmsBlack.isChecked()) type = BlackNumber.BLACK_ALL;
                else if (cbPhoneBlack.isChecked()) type = BlackNumber.BLACK_PHONE;
                else if (cbSmsBlack.isChecked()) type = BlackNumber.BLACK_SMS;
                else {
                    ToastUtil.show(getApplicationContext(), "必须选择拦截类型");
                    return;
                }

                BlackNumber number = new BlackNumber("", blackNum, type);
                dao.add(number);
                //numbers.add(number);
                numbers = dao.queryAll();
                adapter.notifyDataSetChanged();
                dialog.dismiss();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        //builder.setTitle("添加黑名单号码");
//        final EditText et = new EditText(this);
//        et.setInputType(InputType.TYPE_CLASS_PHONE);
//        builder.setPositiveButton("添加", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//
//            }
//        });
//        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//
//            }
//        });

    }

    class MyAdapter extends BaseAdapter {
        MyAdapter() {
            numbers = dao.queryLimit(startId, length);
        }

        @Override
        public int getCount() {
            return numbers.size();
        }

        @Override
        public BlackNumber getItem(int position) {
            return numbers.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.item_black_num, null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.tvBlackNum.setText(numbers.get(position).number);
            String type = null;
            switch (numbers.get(position).type) {
                case BlackNumber.BLACK_PHONE:
                    type = "电话拦截";
                    break;
                case BlackNumber.BLACK_SMS:
                    type = "短信拦截";
                    break;
                case BlackNumber.BLACK_ALL:
                    type = "全部拦截";
                    break;
            }
            viewHolder.tvBlackType.setText(type);

            viewHolder.ivDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //不能传入getApplicationContext()
                    final AlertDialog.Builder builder = new AlertDialog.Builder(CommunicationDefenderActivity.this);
                    builder.setTitle("确认删除");

                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dao.delete(numbers.get(position)._id);
                            numbers.remove(position);
                            notifyDataSetChanged();
                        }
                    });
                    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            });

            return convertView;
        }

        class ViewHolder {
            @InjectView(R.id.tv_black_num)
            TextView tvBlackNum;
            @InjectView(R.id.tv_black_type)
            TextView tvBlackType;
            @InjectView(R.id.iv_delete)
            ImageView ivDelete;

            ViewHolder(View view) {
                ButterKnife.inject(this, view);
            }
        }
    }
}
