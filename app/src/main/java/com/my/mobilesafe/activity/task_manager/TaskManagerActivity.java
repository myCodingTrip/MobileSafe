package com.my.mobilesafe.activity.task_manager;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.my.mobilesafe.R;
import com.my.mobilesafe.activity.BaseActivity;
import com.my.mobilesafe.adapter.BaseViewHolder;
import com.my.mobilesafe.adapter.SimpleAdapter;
import com.my.mobilesafe.bean.Task;
import com.my.mobilesafe.engine.TaskInfoEngine;
import com.my.mobilesafe.utils.TextFormater;
import com.my.mobilesafe.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by MY on 2016/11/28.
 */

public class TaskManagerActivity extends BaseActivity {
    @InjectView(R.id.tv_task_num)
    TextView tvTaskNum;
    @InjectView(R.id.tv_available_memory)
    TextView tvAvailableMemory;
    @InjectView(R.id.rv_task)
    RecyclerView rvTask;
    @InjectView(R.id.ll_task_manager_loading)
    LinearLayout llTaskManagerLoading;
    final int TASK_LOAD_FINISH = 0;
    List<Task> tasks;
    List<Task> systemTasks, userTasks;
    TaskInfoEngine engine;
    final String TAG = "TaskManagerActivity";
    TaskAdapter adapter;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_manager);
        ButterKnife.inject(this);
        engine = new TaskInfoEngine(this);
        tasks = new ArrayList<>();
        systemTasks = new ArrayList<>();
        userTasks = new ArrayList<>();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    private void loadData() {
        clearData();
        initView();
    }

    /**
     * 重新加载时清空原有的数据
     */
    private void clearData() {
        tasks.clear();
        systemTasks.clear();
        userTasks.clear();
    }

    /**
     * 初始化进程管理器界面
     */
    private void initView() {
        Log.d(TAG, "initView");
        llTaskManagerLoading.setVisibility(View.VISIBLE);
        tvTaskNum.setText("目前有" + engine.getTaskNum() + "个进程");
        tvAvailableMemory.setText("可用空间：" + TextFormater.getDataSize(engine.getAvailableMemory()));
        initTaskList();
    }

    /**
     * 初始化任务列表
     */
    private void initTaskList() {
        new Thread() {
            @Override
            public void run() {
                tasks = engine.getAllTasks();
                Log.d(TAG, "tasks:" + tasks.size());
                Message message = new Message();
                message.what = TASK_LOAD_FINISH;
                handler.sendMessage(message);
            }
        }.start();
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case TASK_LOAD_FINISH:  //任务列表加载完成
                    llTaskManagerLoading.setVisibility(View.INVISIBLE);
                    for (Task task : tasks) {
                        if (task.isUserApp()) {
                            userTasks.add(task);
                        } else systemTasks.add(task);
                    }
                    Log.d(TAG, "用户进程" + userTasks.size() + ",系统进程" + systemTasks.size());
//                    TaskAdapter adapter = new TaskAdapter(getApplicationContext(), tasks);
//                    rvTask.setAdapter(adapter);
//                    rvTask.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
//                    rvTask.setItemAnimator(new DefaultItemAnimator());

                    adapter = new TaskAdapter();

                    rvTask.setAdapter(adapter);

                    rvTask.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                    rvTask.setItemAnimator(new DefaultItemAnimator());
                    break;
            }
        }
    };

    @OnClick({R.id.btn_select_all, R.id.btn_clear_tasks})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_select_all:
                selectAllUserTasks();
                break;
            case R.id.btn_clear_tasks:
                killSelectedTasks();
                break;
        }
    }

    private void selectAllUserTasks() {
        for (int i = 0; i < userTasks.size(); i++) {
            Task task = userTasks.get(i);
            task.setChecked(true);
            adapter.notifyDataSetChanged();
        }
    }

    /**
     * 结束所有选中的进程
     */
    private void killSelectedTasks() {
        int taskNum = 0;

        for (int i = 0; i < userTasks.size(); i++) {
            Task task = userTasks.get(i);
            if (task.isChecked() && !engine.isThisTask(task)) {
                engine.killTask(task);
                taskNum++;
            }
        }

        for (int i = 0; i < systemTasks.size(); i++) {
            Task task = systemTasks.get(i);
            if (task.isChecked()) {
                engine.killTask(task);
                taskNum++;
            }
        }

        if (taskNum == 0) {
            ToastUtil.show(this, "没有选中的进程");
            return;
        }

        ToastUtil.show(this, "共结束" + taskNum + "个进程");
        //结束任务后重新加载列表
        loadData();
    }

    class TaskAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private int TITLE = 0;
        private int CONTENT = 1;

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == TITLE) {
                return new TitleViewHolder(getLayoutInflater().inflate(R.layout.item_task_category, null));
            } else
                return new ContentViewHolder(getLayoutInflater().inflate(R.layout.item_task_manager, null));
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (position == 0) {
                ((TitleViewHolder) holder).setText("用户进程数：" + userTasks.size());
            } else if (position > 0 && position < userTasks.size() + 1) {
                Task task = userTasks.get(position - 1);
                ((ContentViewHolder) holder).task = task;
                ((ContentViewHolder) holder).ivTaskIcon.setImageDrawable(task.getAppIcon());
                ((ContentViewHolder) holder).tvTaskName.setText(task.getAppName());
                ((ContentViewHolder) holder).tvTaskSize.setText("占用内存：" + TextFormater.getKBDataSize(task.getMemorySize()));
                ((ContentViewHolder) holder).cbTaskSelected.setChecked(task.isChecked());
                if (engine.isThisTask(task)) {
                    ((ContentViewHolder) holder).cbTaskSelected.setVisibility(View.INVISIBLE);
                }
            } else if (position == userTasks.size() + 1) {
                ((TitleViewHolder) holder).setText("系统进程数：" + systemTasks.size());
            } else {
                Task task = systemTasks.get(position - userTasks.size() - 2);
                ((ContentViewHolder) holder).task = task;
                ((ContentViewHolder) holder).ivTaskIcon.setImageDrawable(task.getAppIcon());
                ((ContentViewHolder) holder).tvTaskName.setText(task.getAppName());
                ((ContentViewHolder) holder).tvTaskSize.setText("占用内存：" + TextFormater.getKBDataSize(task.getMemorySize()));
                ((ContentViewHolder) holder).cbTaskSelected.setChecked(task.isChecked());
            }
        }

        @Override
        public int getItemCount() {
            return systemTasks.size() + userTasks.size() + 2;
        }

        @Override
        public int getItemViewType(int position) {
            if (position == 0 || position == userTasks.size() + 1) {
                return TITLE;
            } else return CONTENT;
        }

        class TitleViewHolder extends RecyclerView.ViewHolder {
            public TextView tv;

            public TitleViewHolder(View itemView) {
                super(itemView);
                tv = (TextView) itemView.findViewById(R.id.tv_category);
            }

            public void setText(String s) {
                tv.setText(s);
            }
        }

        class ContentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            public Task task;
            public ImageView ivTaskIcon;
            public TextView tvTaskName, tvTaskSize;
            public CheckBox cbTaskSelected;
            public RelativeLayout rlItemTask;

            public ContentViewHolder(View itemView) {
                super(itemView);
                ivTaskIcon = (ImageView) itemView.findViewById(R.id.iv_task_icon);
                tvTaskName = (TextView) itemView.findViewById(R.id.tv_task_name);
                tvTaskSize = (TextView) itemView.findViewById(R.id.tv_task_size);
                cbTaskSelected = (CheckBox) itemView.findViewById(R.id.cb_is_task_selected);
                rlItemTask = (RelativeLayout) itemView.findViewById(R.id.rl_item_task);
                rlItemTask.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                cbTaskSelected.setChecked(!cbTaskSelected.isChecked());
                task.setChecked(cbTaskSelected.isChecked());
            }
        }
    }

    class SimpleTaskAdapter extends SimpleAdapter<Task> {
        public SimpleTaskAdapter(Context context, List<Task> data) {
            super(context, data, R.layout.item_task_manager);
        }

        @Override
        public void setViews(BaseViewHolder holder, Task task) {
            holder.getImageView(R.id.iv_task_icon).setImageDrawable(task.getAppIcon());
            holder.getTextView(R.id.tv_task_name).setText(task.getProcessName());
            holder.getTextView(R.id.tv_task_size).setText("占用内存" + task.getMemorySize());
        }

        @Override
        public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return super.onCreateViewHolder(parent, viewType);
        }

        @Override
        public int getItemViewType(int position) {
            return super.getItemViewType(position);
        }

        @Override
        public int getItemCount() {
            return super.getItemCount();
        }
    }
}
