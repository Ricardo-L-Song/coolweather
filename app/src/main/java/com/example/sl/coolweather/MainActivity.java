package com.example.sl.coolweather;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.sl.coolweather.activity.WeatherActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //以下这个判断需要注意一下
        //由于我们在刚进入天气页面请求过了天气信息并且跳转到WeatherActivity以后，我们再次进入就不需要选择城市了
        //所以我们从缓存中取出是否有从服务器获取的weather实体类对象实例
        SharedPreferences preferences= PreferenceManager.getDefaultSharedPreferences(this);
        if (preferences.getString("weather",null) != null) {//取出缓存中的weather对象，如果不为空
            Intent intent=new Intent(MainActivity.this, WeatherActivity.class);
            startActivity(intent);
            finish();//直接结束Activity,跳转到WeatherActivity页面
        }
    }
}
