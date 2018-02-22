package com.example.sl.coolweather.db;

import org.litepal.crud.DataSupport;

/**
 * Created by sl on 2018/2/22.
 */
//实体类Country映射成数据库中县的表
public class Country extends DataSupport {
    private int id;//id对应id 用于数据库操作 如果查询本地数据库 只需要id
    private String countryName;
    private int weatherId;//weatherId用来获取天气，用来发送与服务器交互的请求（如果从服务器查询 就需要代号）
    private int cityId;//记录当前县所属市的id值

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public int getWeatherId() {
        return weatherId;
    }

    public void setWeatherId(int weatherId) {
        this.weatherId = weatherId;
    }

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }


}
