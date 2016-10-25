package com.my.mobilesafe.dao;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

/**
 * Created by MY on 2016/10/18.
 */

public class AddressDao {
    public static final String DB_NAME = "address.db";

    public static boolean isFileExist(Context context){
        File dir = context.getFilesDir();
        File file = new File(dir, DB_NAME);
        return file.exists();
    }

    public static void copyDB(Context context) throws Exception{
        AssetManager assetManager = context.getAssets();
        InputStream is = assetManager.open(DB_NAME);
        FileOutputStream fos = context.openFileOutput(DB_NAME, Context.MODE_PRIVATE);

        byte[] buffer = new byte[1024];
        int len = 0;
        while ((len = is.read(buffer)) != -1){
            fos.write(buffer, 0, len);
        }
        fos.close();
        is.close();
    }


}
