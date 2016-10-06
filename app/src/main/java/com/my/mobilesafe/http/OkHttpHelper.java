package com.my.mobilesafe.http;

import android.os.Handler;
import android.os.Looper;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by MY on 2016/9/14.
 * 单例类
 */
public class OkHttpHelper {
    private static OkHttpClient okHttpClient;
    private static OkHttpHelper okHttpHelper;
    private Handler handler;
    private Gson gson;

    private OkHttpHelper(){
        okHttpClient = new OkHttpClient();
        okHttpClient.setReadTimeout(10, TimeUnit.SECONDS);
        okHttpClient.setWriteTimeout(10, TimeUnit.SECONDS);
        okHttpClient.setConnectTimeout(10, TimeUnit.SECONDS);
        gson = new Gson();
        handler = new Handler(Looper.getMainLooper());
    }

    public static OkHttpHelper getInstance(){
        if(okHttpHelper == null)
            okHttpHelper = new OkHttpHelper();
        return okHttpHelper;
    }

    public void get(String url, BaseCallback callback){
        Request request = buildRequest(url, null, HttpMethodType.GET);
        doRequest(request, callback);
    }

    public void post(String url, Map<String, String> params, BaseCallback callback){
        Request request = buildRequest(url, params, HttpMethodType.POST);
        doRequest(request, callback);
    }

    private Request buildRequest(String url, Map<String, String> params, HttpMethodType methodType){
        Request.Builder builder = new Request.Builder();
        builder.url(url);
        if(methodType == HttpMethodType.GET){
            builder.get();
        }else if(methodType == HttpMethodType.POST){
            RequestBody body = buildFormData(params);
            builder.post(body);
        }
        return builder.build();
    }

    private RequestBody buildFormData(Map<String, String> params){
        FormEncodingBuilder builder = new FormEncodingBuilder();
        if(params != null){
            for (Map.Entry<String, String> entry : params.entrySet()){
                builder.add(entry.getKey(), entry.getValue());
            }
        }
        return builder.build();
    }

    public void doRequest(final Request request, final BaseCallback callback){
        callback.onRequestBefore(request);
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                callback.onFailure(request, e);
            }
            @Override
            public void onResponse(Response response) throws IOException {
                callback.onResponse(response);
                if(response.isSuccessful()){
                    String result = response.body().string();
                    try {
                        if (callback.type == String.class){
                            callbackSuccess(callback, response, result);
                        }else {
                            Object object = gson.fromJson(result, callback.type);
                            callbackSuccess(callback, response, object);
                        }
                    } catch (JsonSyntaxException e) {
                        callbackError(callback, response, null);
                    }
                }else {
                    callbackError(callback, response, null);
                }
            }
        });
    }

    private void callbackSuccess(final BaseCallback callback, final Response response, final Object object){
        handler.post(new Runnable() {
            @Override
            public void run() {
                callback.onSuccess(response, object);
            }
        });
    }

    private void callbackError(final  BaseCallback callback , final Response response, final Exception e ){
        handler.post(new Runnable() {
            @Override
            public void run() {
                callback.onError(response, response.code(), e);
            }
        });
    }

    private void callbackFailure(final  BaseCallback callback , final Request request, final IOException e ){
        handler.post(new Runnable() {
            @Override
            public void run() {
                callback.onFailure(request, e);
            }
        });
    }

    enum HttpMethodType{
        GET,
        POST
    }
}
