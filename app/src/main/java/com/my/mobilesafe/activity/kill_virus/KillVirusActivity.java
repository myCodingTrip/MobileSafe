package com.my.mobilesafe.activity.kill_virus;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.my.mobilesafe.R;
import com.my.mobilesafe.activity.BaseActivity;
import com.my.mobilesafe.bean.AppInfo;
import com.my.mobilesafe.bean.Virus;
import com.my.mobilesafe.dao.VirusDao;
import com.my.mobilesafe.engine.AppInfoEngine;
import com.my.mobilesafe.utils.FileUtil;
import com.my.mobilesafe.utils.Md5Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import static com.my.mobilesafe.dao.VirusDao.fileName;

/**
 * Created by MY on 2016/12/10.
 */

public class KillVirusActivity extends BaseActivity {
    @InjectView(R.id.img_scanning)
    ImageView imgScanning;
    @InjectView(R.id.tv_scanning)
    TextView tvScanning;
    @InjectView(R.id.pb_scanning)
    ProgressBar pbScanning;
    VirusDao dao;
    PackageManager pm;
    RotateAnimation ra;
    AppInfoEngine engine;
    final int SET_MAX = -1;
    final int SCAN_APP = 0;
    final int NOT_VIRUS = 1;
    final int FIND_VIRUS = 2;
    final int FINISH = 3;

    @InjectView(R.id.ll_scan_content)
    LinearLayout llScanContent;
    @InjectView(R.id.sv)
    ScrollView sv;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kill_virus);
        ButterKnife.inject(this);

        dao = new VirusDao();
        engine = new AppInfoEngine(this);
        pm = getPackageManager();
        File file = new File(getFilesDir(), fileName);
        if (!file.exists()) {
            FileUtil.copyAssetsFile(this, fileName);
        }
        pbScanning.setProgress(0);
        tvScanning.setText("正在获取应用信息.....");
        setScanAnimation();
        startScanThread();
    }

    private void setScanAnimation() {
        //360会卡顿
        ra = new RotateAnimation(0, 359,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        ra.setDuration(2000);
        ra.setRepeatCount(1000);
        ra.start();
        imgScanning.startAnimation(ra);
    }

    private void startScanThread() {
        new Thread() {
            @Override
            public void run() {
                List<Virus> viruses = dao.getAllViruses(getApplicationContext());
                List<AppInfo> appInfoList = engine.getAllAppSignatures();

                Message msg1 = new Message();
                msg1.what = SET_MAX;
                msg1.arg1 = appInfoList.size();
                handler.sendMessage(msg1);

                for (AppInfo info : appInfoList) {
                    boolean isVirus = false;
                    Message msg = new Message();
                    msg.obj = info.getName();
                    msg.what = SCAN_APP;
                    handler.sendMessage(msg);
                    for (Virus virus : viruses) {
                        //如果是病毒
                        if (info.getMd5Code().equals(virus.getMd5Code()) && info.getPackageName().equals(virus.getPackageName())) {
                            msg.what = FIND_VIRUS;
                            handler.sendMessage(msg);
                            isVirus = true;
                            break;
                        }
                    }
                    if (!isVirus) {
                        Message msg0 = new Message();
                        msg0.obj = info.getName();
                        msg0.what = NOT_VIRUS;
                        handler.sendMessage(msg0);
                    }
                    SystemClock.sleep(200);
                }

                Message msg = new Message();
                msg.what = FINISH;
                handler.sendMessage(msg);

            }
        }.start();
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            String name = null;
            if (msg.obj != null){
                name = (String) msg.obj;
            }
            TextView tv = new TextView(getApplicationContext());
            switch (msg.what) {
                case SET_MAX:
                    pbScanning.setMax(msg.arg1);
                    break;
                case SCAN_APP:
                    tvScanning.setText("正在扫描：" + name);
                    break;
                case NOT_VIRUS:
                    tv.setText("扫描安全:" + name);
                    llScanContent.addView(tv, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    sv.fullScroll(ScrollView.FOCUS_DOWN);
                    pbScanning.setProgress(pbScanning.getProgress() + 1);
                    break;
                case FIND_VIRUS:
                    tv.setTextColor(Color.RED);
                    tv.setText("发现病毒:" + name);
                    llScanContent.addView(tv, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    sv.fullScroll(ScrollView.FOCUS_DOWN);
                    pbScanning.setProgress(pbScanning.getProgress() + 1);
//                    sv.scrollBy(0, 100);
                    break;
                case FINISH:
                    imgScanning.clearAnimation();
                    tvScanning.setText("扫描完成!");
                    break;
            }
        }
    };
}
