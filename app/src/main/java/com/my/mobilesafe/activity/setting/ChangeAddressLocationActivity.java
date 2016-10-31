package com.my.mobilesafe.activity.setting;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;

import com.my.mobilesafe.R;
import com.my.mobilesafe.activity.BaseActivity;
import com.my.mobilesafe.constant.SharedKey;

public class ChangeAddressLocationActivity extends BaseActivity {
    String TAG = "AddressLocation";
    SharedPreferences sp;
    int finalX, finalY;
    //归属地窗口的宽高
    int windowWidth, windowHeight;
    Button btnWindowAddress = null;
    //判断双击的时间
    long startTime=0, endTime=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_address_location);

        sp = getSharedPreferences("config", MODE_PRIVATE);
        finalX = sp.getInt(SharedKey.LOCATION_X, 0);
        finalY = sp.getInt(SharedKey.LOCATION_Y, 0);
        btnWindowAddress = (Button) findViewById(R.id.btn_window_address);

        final int screenWidth = getWindowManager().getDefaultDisplay().getWidth();
        final int screenHeight = getWindowManager().getDefaultDisplay().getHeight();

        //解决一开始无法获取View的宽高问题
        btnWindowAddress.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                windowWidth = btnWindowAddress.getRight() - btnWindowAddress.getLeft();
                windowHeight = btnWindowAddress.getBottom() - btnWindowAddress.getTop();
                Log.d(TAG, "width:" + windowWidth);
                Log.d(TAG, "height:" + windowHeight);
                btnWindowAddress.layout(finalX, finalY, finalX+windowWidth, finalY+windowHeight);
            }
        });
        //拖动归属地信息
        btnWindowAddress.setOnTouchListener(new View.OnTouchListener() {
            int startX=finalX, startY=finalY;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        startX = (int) event.getRawX();
                        startY = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        int stopX = (int) event.getRawX();
                        int stopY = (int) event.getRawY();
                        int x = stopX - startX;
                        int y = stopY - startY;

                        int l = btnWindowAddress.getLeft() + x;
                        int t = btnWindowAddress.getTop() + y;
                        int r = btnWindowAddress.getRight() + x;//=l+width
                        int b = btnWindowAddress.getBottom() + y;//=t+height

                        //不能越界
                        if(l>0 && t>0 && r<screenWidth && b<screenHeight){
                            btnWindowAddress.layout(l, t, r, b);
                            finalX = l;
                            finalY = t;
                        }

                        startX = stopX;
                        startY = stopY;
                        break;
                }
                //此处设为false否则会与onClick事件冲突
                return false;
            }
        });

        btnWindowAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick");
                long time = System.currentTimeMillis();
                if(startTime == 0){
                    startTime = time;
                    return;
                }
                endTime = time;
                Log.d(TAG, "endTime"+endTime);
                if(endTime-startTime < 500){
                    Log.d(TAG, "双击");
                    setCenter();
                }
                startTime = endTime;
            }

            //设置归属地窗口居中
            private void setCenter() {
                int centerX = screenWidth/2;
                int centerY = screenHeight/2;
                int l = centerX - windowWidth/2;
                int t = centerY - windowHeight/2;
                int r = centerX + windowWidth/2;
                int b = centerY + windowHeight/2;
                btnWindowAddress.layout(l, t, r, b);
                finalX = l;
                finalY = t;
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(SharedKey.LOCATION_X, finalX);
        editor.putInt(SharedKey.LOCATION_Y, finalY);
        editor.commit();
    }
}
