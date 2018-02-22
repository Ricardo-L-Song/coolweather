package com.example.sl.coolweather.util;

import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by sl on 2018/2/22.
 */
//发送请求的工具类(形参形参形参)
//调用的时候只需要HttpUtil.sendOkHttpRequest( , )方法 就能与服务器交互，第一个参数是请求ip地址，第二个参数是回调注册
public class HttpUtil {
    public static void sendOkHttpRequest(String address,okhttp3.Callback callback){
        OkHttpClient client=new OkHttpClient();
        Request request=new Request.Builder().url(address).build();//形参，用来发送请求
        client.newCall(request).enqueue(callback);//请求后，相应服务器请求的回调调用
    }
}
