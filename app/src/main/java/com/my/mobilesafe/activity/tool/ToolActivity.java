package com.my.mobilesafe.activity.tool;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.View;

import com.my.mobilesafe.R;
import com.my.mobilesafe.activity.BaseActivity;
import com.my.mobilesafe.bean.ShortMessage;
import com.my.mobilesafe.engine.SmsEngine;
import com.my.mobilesafe.utils.FileUtil;
import com.my.mobilesafe.utils.ToastUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.my.mobilesafe.engine.SmsEngine.SMS_BACKUP_FILE_NAME;

public class ToolActivity extends BaseActivity {
    String TAG = "ToolActivity";
    final int BACKUP_FINISHED = 1000;
    final int BACKUP_ERROR = 1001;
    final int RESTORE_FINISHED = 1002;
    SmsEngine engine;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tool);
        ButterKnife.inject(this);
        engine = new SmsEngine(this);
    }

    @OnClick({R.id.tv_query_phone_address, R.id.tv_backup_sms, R.id.tv_restore_sms, R.id.tv_app_lock, R.id.tv_common_num})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_query_phone_address:
                startActivity(new Intent(this, AddressQueryActivity.class));
                break;
            case R.id.tv_backup_sms:
                backupSms();
                break;
            case R.id.tv_restore_sms:
                restoreSms();
                break;
            case R.id.tv_app_lock:
                startActivity(new Intent(this, AppLockActivity.class));
                break;
            case R.id.tv_common_num:
                break;
        }
    }

    /**
     * 根据sd卡中的Json文件还原短息内容
     */
    private void restoreSms() {
        new Thread(){
            @Override
            public void run() {
                Message message = new Message();
                String json = engine.readSmsBackup();
                List<ShortMessage> messages = SmsEngine.changeJsonToList(json);
//                SmsEngine.deleteAllMessages(getApplicationContext());
                engine.insertMessages(messages);
                message.what = RESTORE_FINISHED;
                handler.sendMessage(message);
            }
        }.start();
    }

    /**
     * 备份短信,将短信转成Json字符串存入sd卡
     */
    private void backupSms() {
        new Thread(){
            @Override
            public void run() {
                Message message = new Message();
                try {
                    List<ShortMessage> messages = engine.getSMSList();
                    String json = SmsEngine.changeListToJson(messages);
                    FileUtil.createAppFolder();
                    File file = new File(FileUtil.APP_DIR, SMS_BACKUP_FILE_NAME);

                    FileOutputStream fos = new FileOutputStream(file);
                    fos.write(json.getBytes());
                    fos.close();

                    SystemClock.sleep(2000);
                    message.what = BACKUP_FINISHED;
                } catch (Exception e) {
                    message.what = BACKUP_ERROR;
                    e.printStackTrace();
                }finally {
                    handler.sendMessage(message);
                }
            }
        }.start();
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case BACKUP_FINISHED:
                    ToastUtil.show(getApplicationContext(), "短信备份成功");
                    break;
                case BACKUP_ERROR:
                    ToastUtil.show(getApplicationContext(), "短信备份失败");
                    break;
                case RESTORE_FINISHED:
                    ToastUtil.show(getApplicationContext(), "短信还原成功");
                    break;
            }
        }
    };


}
