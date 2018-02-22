package com.example.sl.coolweather.db;

import org.litepal.crud.DataSupport;

/**
 * Created by sl on 2018/2/22.
 */
//实体类City映射成数据库中城市的表
public class City extends DataSupport {
    private int id;//id对应id 用于数据库操作 如果查询本地数据库 只需要id
    private String cityName;
    private int cityCode;//当前城市的代号，用来用来发送与服务器交互的请求（如果从服务器查询市内所有县 就需要代号）
    private int provinceId;//记录当前城市所属省的代号
    //"http://guolin.tech/api/china/"+provinceCode 其中选中的省代号是通过listview中的getposition()得到Province实例，再调用.getProvinceCode()得到
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public int getCityCode() {
        return cityCode;
    }

    public void setCityCode(int cityCode) {
        this.cityCode = cityCode;
    }

    public int getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(int provinceId) {
        this.provinceId = provinceId;
    }

}
