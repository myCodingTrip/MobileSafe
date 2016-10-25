package com.my.mobilesafe.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.util.Xml;
import android.widget.TextView;

import com.my.mobilesafe.R;
import com.my.mobilesafe.bean.UpdateInfo;
import com.my.mobilesafe.constant.SharedKey;
import com.my.mobilesafe.http.OkHttpHelper;
import com.my.mobilesafe.http.SpotsCallBack;
import com.my.mobilesafe.utils.FileUtils;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.xmlpull.v1.XmlPullParser;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by MY on 2016/10/4.
 */
public class SplashActivity extends BaseActivity {

    @InjectView(R.id.tv_version)
    TextView tvVersion;
    public static final String LOG_TAG = "MobileSafe";
    private String versionName;
    SharedPreferences sp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);
        ButterKnife.inject(this);

        versionName = getVersionName();
        if(versionName != null)
            tvVersion.setText("版本号：" + versionName);
        sp = getSharedPreferences("config", MODE_PRIVATE);
        boolean isAutoUpdate = sp.getBoolean(SharedKey.IS_AUTO_UPDATE, true);
        if (isAutoUpdate){
            //checkNewVersion();
        }
        loadMainActivity();
    }

    public String getVersionName(){
        try {
            PackageManager packageManager = getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(), 0);
            return packageInfo.versionName;
        }catch (Exception e){
            return null;
        }
    }

    public void checkNewVersion(){
        //NetUtils.isConnected(this)
        if(true){
            OkHttpHelper okHttpHelper = OkHttpHelper.getInstance();
            String url = "http://10.0.2.2:8080/MobileSafeWeb/updateinfo.xml";

            okHttpHelper.get(url, new SpotsCallBack<String>(this) {

                @Override
                public void onSuccess(Response response, String xmlString) {
                    parseXML(xmlString);
                    //System.out.println(xmlString);
                }

                @Override
                public void onError(Response response, int code, Exception e) {

                }
            });
        }
    }

    private void parseXML(String xmlString) {
        ByteArrayInputStream is = new ByteArrayInputStream(xmlString.getBytes());
        XmlPullParser parser = Xml.newPullParser();
        try {
            parser.setInput(is, "UTF-8");
            int eventType = parser.getEventType();
            UpdateInfo updateInfo = new UpdateInfo();;
            while (eventType != XmlPullParser.END_DOCUMENT){
                switch (eventType){
                    case XmlPullParser.START_TAG:// 开始元素事件
                        String name = parser.getName();
                        if(name.equals("updateinfo")){
                            //updateInfo
                        }else if (name.equals("version")){
                            String version = parser.nextText();
                            updateInfo.setVersion(version);
                        }else if (name.equals("url")){
                            String url = parser.nextText();
                            updateInfo.setUrl(url);
                        }else if (name.equals("description")){
                            String description = parser.nextText();
                            updateInfo.setDescription(description);
                        }
                        break;
                }
                eventType = parser.next();
            }
            if (!updateInfo.getVersion().equals(versionName)){
                showUpdateDialog(updateInfo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showUpdateDialog(final UpdateInfo updateInfo) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("更新提示");
        builder.setMessage(updateInfo.getDescription());
        builder.setCancelable(false);
        builder.setPositiveButton("立刻升级", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                downloadNewApk(updateInfo.getUrl());
            }
        });
        builder.setNegativeButton("下次再说", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void downloadNewApk(final String url) {
        if(!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            return;
        }

        OkHttpClient mOkHttpClient = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();
        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response){
                InputStream is = null;
                FileOutputStream fos = null;
                String SDPath = Environment.getExternalStorageDirectory().getAbsolutePath();
                try {
                    byte[] buf = new byte[1024];
                    int len = 0;
                    is = response.body().byteStream();
                    long total = response.body().contentLength();
                    String name = FileUtils.getFileName(url);
                    File file = new File(Environment.getExternalStorageDirectory(), name);
                    System.out.println(Environment.getExternalStorageDirectory());
                    //File file = new File("/sdcard", name);
                    fos = new FileOutputStream(file);
                    while ((len = is.read(buf)) != -1) {
                        fos.write(buf, 0, len);
                    }
                    fos.flush();
                    Log.d(LOG_TAG, "文件下载成功");
                } catch (Exception e) {
                    //TODO  权限被拒绝
                    Log.d(LOG_TAG, "文件下载失败");
                    e.printStackTrace();
                } finally {
                    try {
                        if (is != null) is.close();
                        if (fos != null) fos.close();
                    } catch (IOException e) {
                    }
                }
            }
        });
    }

    private void loadMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
