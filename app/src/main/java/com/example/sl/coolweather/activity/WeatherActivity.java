package com.example.sl.coolweather.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.sl.coolweather.R;
import com.example.sl.coolweather.gson.Weather;
import com.example.sl.coolweather.service.AutoUpdateService;
import com.example.sl.coolweather.util.HttpUtil;
import com.example.sl.coolweather.util.Utility;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity {

    @BindView(R.id.title_city)
    TextView titleCity;
    @BindView(R.id.title_update_time)
    TextView titleUpdateTime;
    @BindView(R.id.degree_text)
    TextView degreeText;
    @BindView(R.id.weather_info_text)
    TextView weatherInfoText;
    @BindView(R.id.forecast_layout)
    LinearLayout forecastLayout;
    @BindView(R.id.aqi_text)
    TextView aqiText;
    @BindView(R.id.pm25_text)
    TextView pm25Text;
    @BindView(R.id.comfort_text)
    TextView comfortText;
    @BindView(R.id.car_wash_text)
    TextView carWashText;
    @BindView(R.id.sport_text)
    TextView sportText;
    @BindView(R.id.weather_layout)
    ScrollView weatherLayout;
    @BindView(R.id.bing_pic_img)
    ImageView bingPicImg;
    @BindView(R.id.swipe_refresh)
    public SwipeRefreshLayout swipeRefresh;
    @BindView(R.id.nav_button)
    Button navButton;
    @BindView(R.id.drawer_layout)
    public DrawerLayout drawerLayout;
    private String mWeatherId;//这是用来刷新天气信息的id

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);//自动进行判断 当前版本为5.0以上
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);//活动布局显示在系统状态栏上面
        getWindow().setStatusBarColor(Color.TRANSPARENT);//设置系统状态栏的颜色为透明
        setContentView(R.layout.activity_weather);
        ButterKnife.bind(this);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = preferences.getString("weather", null);//键值对方式从SharePrefences中取出
        if (weatherString != null) {
            //有缓存的时候直接解析天气数据
            Weather weather = Utility.handleWeatherResponse(weatherString);
            mWeatherId = weather.basic.getWeatherId();//直接从实体类中获取id 因为下面的requestWeather传入的是一个id值
            showWeatherInfo(weather);
        } else {
            //无缓存时去服务器查询数据
            mWeatherId = getIntent().getStringExtra("weather_id");//否则取出跳转Intent中的weatherId mWeatherId为本地变量
            weatherLayout.setVisibility(View.INVISIBLE);//设置ScrollView不可见 要不然空数据界面显得很奇怪
            requestWeather(mWeatherId);
        }
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestWeather(mWeatherId);//用全局变量mweatherId来根据天气id请求天气信息 达到刷新的功能
            }
        });
        String bingPic = preferences.getString("bing_pic", null);
        if (bingPic != null) {
            Glide.with(this).load(bingPic).into(bingPicImg);//如果缓存中有键值对bing_pic 则使用Glide来加载图片
        } else {
            loadBingPic();//调用方法从服务器获取数据并且缓存
        }
        navButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);//开启侧滑菜单
            }
        });
    }

    //根据传入的天气id从服务器请求城市天气信息（判断哪里要刷新UI 就从哪里发出获取数据请求 服务器响应的数据可以通过回调导入）
    public void requestWeather(final String weatherId) {
        String weatherUrl = "http://guolin.tech/api/weather?cityid=" + weatherId + "&key=bc0418b57b2d4918819d3974ac1285d9";
        //拼装出响应的接口地址
        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {//新建了回调，可以将请求到的数据回调到这里
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {//从服务器获取天气信息失败也回到主线程刷新UI
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
                        swipeRefresh.setRefreshing(false);//设置刷新事件停止，隐藏刷新进度条
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();//分离出响应的数据体
                final Weather weather = Utility.handleWeatherResponse(responseText);//一串字符串的数据体调用工具类解析成Weather类
                //从回调方法中回到主线程更新UI
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (weather != null && "ok".equals(weather.status)) {//如果weather对象存在并且状态值为OK
                            SharedPreferences.Editor editor = PreferenceManager.
                                    getDefaultSharedPreferences(WeatherActivity.this).edit();
                            editor.putString("weather", responseText);
                            editor.apply();//存入缓存
                            showWeatherInfo(weather);//调用函数显示天气,更新UI
                            Intent intent=new Intent(WeatherActivity.this, AutoUpdateService.class);
                            startService(intent);
                        } else {//未能从Weather实体类获取数据
                            Toast.makeText(WeatherActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
                        }
                        swipeRefresh.setRefreshing(false);//设置刷新事件停止，隐藏刷新进度条
                    }
                });
            }
        });
        loadBingPic();//每次请求天气信息时刷新背景图片
    }

    //处理并展示Weather实体类中的数据（从服务器返回的数据解析成Weather实体类用来展示）
    public void showWeatherInfo(Weather weather) {
        String cityName = weather.basic.getCityName();
        String updateTime = weather.basic.getUpdate().getUpdateTime().split(" ")[1];//分离出空格以后的时间
        String degree = weather.now.getTmperature();
        String weatherInfo = weather.now.more.getInfo();
        //在类中本地化使用
        titleCity.setText(cityName);
        titleUpdateTime.setText(updateTime);
        degreeText.setText(degree);
        weatherInfoText.setText(weatherInfo);
        //更新控件
        forecastLayout.removeAllViews();
        for (int i = 0; i < weather.forecastList.size(); i++) {
            View view = LayoutInflater.from(this).inflate(R.layout.forecast_item, forecastLayout, false);
            //将R.layout.forecast_item子项布局填充进forecastLayout布局（而forecastLayout布局被include进activity_weather里）
            TextView dateText = (TextView) view.findViewById(R.id.date_text);
            TextView infoText = (TextView) view.findViewById(R.id.info_text);
            TextView maxText = (TextView) view.findViewById(R.id.max_text);
            TextView minText = (TextView) view.findViewById(R.id.min_text);
            dateText.setText(weather.forecastList.get(i).getDate());
            infoText.setText(weather.forecastList.get(i).getMore().getInfo());
            maxText.setText(weather.forecastList.get(i).getTmperature().getMax());
            minText.setText(weather.forecastList.get(i).getTmperature().getMin());
            forecastLayout.addView(view);//填充进LinearLayout
        }
        if (weather.aqi != null) {//如果对AQI实体类对象的引用不为空
            aqiText.setText(weather.aqi.getCity().getAqi());
            pm25Text.setText(weather.aqi.getCity().getPm25());
        }
        String comfort = "舒适度" + weather.suggestion.getComfort().getInfo();
        String carWash = "洗车指数" + weather.suggestion.getCarWash().getInfo();
        String sport = "运动建议" + weather.suggestion.getSport().getInfo();
        comfortText.setText(comfort);
        carWashText.setText(carWash);
        sportText.setText(sport);
        weatherLayout.setVisibility(View.VISIBLE);
    }

    //加载必应每日一周 存入缓存并且进行UI操作 用Glide加载
    public void loadBingPic() {
        String requestBingPic = "http://guolin.tech/api/bing_pic";//请求必应每日一图的接口地址
        HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {//发出请求并且回调响应数据至此处
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String bingPic = response.body().string();//服务器响应的数据同样化成字符串 之后可以通过Glide解析加载成图片
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                editor.putString("bing_pic", bingPic);//将String存入缓存
                editor.apply();
                runOnUiThread(new Runnable() {//从回调函数回到主线程 更新UI
                    @Override
                    public void run() {
                        Glide.with(WeatherActivity.this).load(bingPic).into(bingPicImg);
                        //使用Glide加载图片至ImageView Glide完成字符串到图片的转换
                    }
                });
            }
        });
    }
}
