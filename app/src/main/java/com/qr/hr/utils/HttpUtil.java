package com.qr.hr.utils;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class HttpUtil {
    private static OkHttpClient okHttpClient;
    private static Request request;
    //get 请求
    public static void SendOkHttpRequest(String address,okhttp3.Callback callback){
        if(null == okHttpClient){
            okHttpClient= new OkHttpClient.Builder()
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .readTimeout(60, TimeUnit.SECONDS)
                    .build();
        }

        request = new Request.Builder().url(address).build();

        okHttpClient.newCall(request).enqueue(callback);
    }
    //post请求
    public static void  SendOkHttpRequest(String address, RequestBody requestBody, okhttp3.Callback callback){
        if(null == okHttpClient){
            okHttpClient= new OkHttpClient.Builder()
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .readTimeout(60, TimeUnit.SECONDS)
                    .build();
        }
        request = new Request.Builder().url(address).post(requestBody).build();
        okHttpClient.newCall(request).enqueue(callback);
    }
}
