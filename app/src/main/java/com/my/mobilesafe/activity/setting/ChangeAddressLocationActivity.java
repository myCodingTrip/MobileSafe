package com.my.mobilesafe.activity.setting;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;

import com.my.mobilesafe.R;
import com.my.mobilesafe.activity.BaseActivity;
import com.my.mobilesafe.constant.SharedKey;

public class ChangeAddressLocationActivity extends BaseActivity {
    String TAG = "AddressLocation";
    SharedPreferences sp;
    int finalX, finalY;
    View windowAddress = null;
    boolean hasInit = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_address_location);

        sp = getSharedPreferences("config", MODE_PRIVATE);
        finalX = sp.getInt(SharedKey.LOCATION_X, 0);
        finalY = sp.getInt(SharedKey.LOCATION_Y, 0);
        windowAddress = findViewById(R.id.window_address);

        final int screenWidth = getWindowManager().getDefaultDisplay().getWidth();
        final int screenHeight = getWindowManager().getDefaultDisplay().getHeight();

        //解决一开始无法获取View的宽高问题
        windowAddress.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int width = windowAddress.getRight() - windowAddress.getLeft();
                int height = windowAddress.getBottom() - windowAddress.getTop();
                Log.d(TAG, "width:" + width);
                Log.d(TAG, "height:" + height);
                windowAddress.layout(finalX, finalY, finalX+width, finalY+height);
            }
        });
        //拖动归属地信息
        windowAddress.setOnTouchListener(new View.OnTouchListener() {
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

                        int l = windowAddress.getLeft() + x;
                        int t = windowAddress.getTop() + y;
                        int r = windowAddress.getRight() + x;//=l+width
                        int b = windowAddress.getBottom() + y;//=t+height

                        //不能越界
                        if(l>0 && t>0 && r<screenWidth && b<screenHeight){
                            windowAddress.layout(l, t, r, b);
                            finalX = l;
                            finalY = t;
                        }

                        startX = stopX;
                        startY = stopY;
                        break;
                }
                return true;
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
