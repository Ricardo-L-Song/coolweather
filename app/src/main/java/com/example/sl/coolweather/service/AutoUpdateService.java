package com.example.sl.coolweather.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;

import com.bumptech.glide.Glide;
import com.example.sl.coolweather.activity.WeatherActivity;
import com.example.sl.coolweather.gson.Weather;
import com.example.sl.coolweather.util.HttpUtil;
import com.example.sl.coolweather.util.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

//设置定时请求数据，并且将更新的数据存入缓存 而不用更新UI，因为打开app以后，优先从SharePreferences之类的缓存中读取数据
//读取不到再从服务器获取
public class AutoUpdateService extends Service {

    @Override
    public IBinder onBind(Intent intent) {//与活动交互的时候调用的方法
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {//启动服务时候调用
        updateWeather();
        updateBingPic();
        AlarmManager manager=(AlarmManager)getSystemService(ALARM_SERVICE);//获取系统定时服务（定时干什么）
        int anHour=8*60*60*1000;//8小时的毫秒数
        long triggerAtTime= SystemClock.elapsedRealtime()+anHour;//系统现在时长+8小时
        Intent i=new Intent(this,AutoUpdateService.class);
        PendingIntent pendingIntent=PendingIntent.getService(this,0,i,0);
        manager.cancel(pendingIntent);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,triggerAtTime,pendingIntent);//每隔8小时就执行这个AutoUpdateService
        return super.onStartCommand(intent, flags, startId);
    }
    //更新天气信息
    private void updateWeather(){
        SharedPreferences preferences= PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString=preferences.getString("weather",null);//从缓存中获取weather
        //有缓存时直接解析天气数据（由于我们是点击启动这个服务 缓存中一定有 这里加判断是为了防止bug）
        if (weatherString!=null){
            Weather weather= Utility.handleWeatherResponse(weatherString);
            //从weather实体类对象中，我们获取其weather_id 再发送请求，得到新的weather
            String weatherId=weather.basic.getWeatherId();
            String weatherUrl = "http://guolin.tech/api/weather?cityid=" + weatherId + "&key=bc0418b57b2d4918819d3974ac1285d9";
            HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String responseText=response.body().string();
                    Weather weather=Utility.handleWeatherResponse(responseText);
                    if (weather!=null&&"ok".equals(weather.status)){
                        SharedPreferences.Editor editor=PreferenceManager.getDefaultSharedPreferences(AutoUpdateService.this).edit();
                        //如果不空 就从此处提交数据到缓存
                        editor.putString("weather",responseText);
                        editor.apply();
                        //数据为weather
                    }
                }
            });
        }
    }

    //更新必应每日一图
    private void updateBingPic(){
        String requestBingPic = "http://guolin.tech/api/bing_pic";//请求必应每日一图的接口地址
        HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {//发出请求并且回调响应数据至此处
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String bingPic = response.body().string();//服务器响应的数据同样化成字符串 之后可以通过Glide解析加载成图片
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(AutoUpdateService.this).edit();
                editor.putString("bing_pic", bingPic);//将String存入缓存
                editor.apply();
            }
        });
    }
}
