package com.my.mobilesafe.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Environment;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static com.my.mobilesafe.dao.AddressDao.DB_NAME;

/**
 * Created by MY on 2016/10/6.
 */

public class FileUtil {
    static String TAG = "FileUtil";

    /**
     * 获取文件名字
     * @param url 网络地址
     * @return 去掉路径后的文件名
     */
    public static String getFileName(String url){
        return url.substring(url.lastIndexOf("/")+1);
    }

    /**
     * app目录的路径，用于存放app相关文件
     */
    public static String APP_DIR = Environment.getExternalStorageDirectory().getAbsolutePath() + "/MobileSafe";

    /**
     * 创建app文件夹 MobileSafe
     */
    public static void createAppFolder(){
        try {
            File file = new File(APP_DIR);
            if (!file.exists()){
                file.mkdir();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 将输入流转成文件,下载更新包时使用
     * @param is
     * @param file
     * @return
     */
    public static boolean inputFile(InputStream is, File file){
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            byte[] bytes = new byte[1024];
            int len = 0;
            while ((len=is.read(bytes)) != -1){
                fos.write(bytes, 0, len);
            }
            fos.flush();
            Log.d(TAG, "文件下载成功");
            return true;
        }catch (Exception e){
            e.printStackTrace();
            Log.d(TAG, "文件下载失败");
            return false;
        }finally {
            try {
                if (is != null) is.close();
                if (fos != null) fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 将文件转化为字符串
     * @param file
     * @return
     */
    public static String fileToString(File file){
        //需要初始化为null才能关闭
        FileInputStream fis = null;
        ByteArrayOutputStream bos = null;
        try {
            fis = new FileInputStream(file);
            bos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len=fis.read(buffer)) != -1){
                bos.write(buffer, 0, len);
            }
            return bos.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }finally {
            try {
                fis.close();
                bos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void copyAssetsFile(Context context, String fileName){
        AssetManager assetManager = context.getAssets();
        InputStream is = null;
        try {
            is = assetManager.open(fileName);
            FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);

            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = is.read(buffer)) != -1){
                fos.write(buffer, 0, len);
            }
            fos.close();
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
