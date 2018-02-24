package com.example.sl.coolweather.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by sl on 2018/2/24.
 */
//实体类weather对具体的五个实体类进行了引用，解析完五个实体类以后，最终解析weather
public class Weather {
    public String status;//对应状态是否成功
    public Basic basic;
    public AQI aqi;
    public Now now;
    public Suggestion suggestion;
    @SerializedName("daily_forecast")
    public List<Forecast> forecastList;//由于预告了一个礼拜的天气 所以我们建立了一个泛型数组
}
