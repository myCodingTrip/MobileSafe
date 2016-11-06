package com.my.mobilesafe.engine;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Message;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.my.mobilesafe.activity.tool.ToolActivity;
import com.my.mobilesafe.bean.ShortMessage;
import com.my.mobilesafe.utils.FileUtil;
import com.my.mobilesafe.utils.ToastUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by MY on 2016/11/5.
 */

public class SmsEngine {
    public static final String SMS_BACKUP_FILE_NAME = "sms.json";
    String TAG = "SmsEngine";
    Context context;
    public SmsEngine(Context context){
        this.context = context;
    }
    /**
     * 获取所有短信内容
     * @return 封装了短信信息的List
     */
    public List<ShortMessage> getSMSList(){
        List<ShortMessage> shortMessages = new ArrayList<>();
        ContentResolver resolver = context.getContentResolver();
        Uri uri = Uri.parse("content://sms/");
        Cursor cursor = resolver.query(uri, new String[]{"address", "date", "type", "body"},
                null, null, null);
        ShortMessage shortMessage;
        while(cursor.moveToNext()){
            String address = cursor.getString(0);
            long date = cursor.getLong(1);
            int type = cursor.getInt(2);
            String body = cursor.getString(3);
            shortMessage = new ShortMessage(address, date, type, body);
            shortMessages.add(shortMessage);
        }
        return shortMessages;
    }

    /**
     * 将List<ShortMessage>对象转化为Json字符串
     * @param messages
     * @return
     */
    public static String changeListToJson(List<ShortMessage> messages){
        Gson gson = new Gson();
        String json = gson.toJson(messages);
        return json;
    }

    /**
     *读取用于短信备份的Json文件
     * @return
     */
    public String readSmsBackup(){
        File file = new File(FileUtil.APP_DIR, SMS_BACKUP_FILE_NAME);
        if(!file.exists()){
            ToastUtil.show(context, "找不到备份文件");
            return null;
        }
        String json = FileUtil.fileToString(file);
        return json;
    }

    /**
     * 把Json转化为List<ShortMessage>对象
     * @param json
     * @return
     */
    public static List<ShortMessage> changeJsonToList(String json){
        Type type = new TypeToken<List<ShortMessage>>(){}.getType();
        Gson gson = new Gson();
        List<ShortMessage> messages = gson.fromJson(json, type);
        return messages;
    }

    /**
     * 删除所有短信 android 4.4之后非默认的短信应用已经没有办法删除短信了
     */
    public void deleteAllMessages(){
        ContentResolver resolver = context.getContentResolver();
        Uri uri = Uri.parse("content://sms");
        int num = resolver.delete(uri, null, null);
        Log.d(TAG, "num"+num);
    }

    //todo AUDIO_OUTPUT_FLAG_FAST denied by client
    public void insertMessages(List<ShortMessage> messages){
        Uri uri = Uri.parse("content://sms");
        ContentResolver resolver = context.getContentResolver();
        for (ShortMessage message:messages){
            ContentValues values = new ContentValues();
            values.put("address", message.address);
            values.put("date", message.date);
            values.put("type", message.type);
            values.put("body", message.body);
            resolver.insert(uri, values);
        }
    }
}
