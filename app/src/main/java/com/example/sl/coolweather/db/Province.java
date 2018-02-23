package com.example.sl.coolweather.db;

import org.litepal.crud.DataSupport;

//实体类Province映射成数据库中省的表 表中provinceCode字段用来发送请求

public class Province extends DataSupport {
    private int id;//每个实体类必有 id对应id 用于数据库操作 如果查询本地数据库 只需要id
    private String provinceName;//省
    private int provinceCode;//省的代号,用来之后的市发送与服务器交互的请求（如果从服务器查询省内所有市 就需要代号）
    //http://guolin.tech/api/china
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public int getProvinceCode() {
        return provinceCode;
    }

    public void setProvinceCode(int provinceCode) {
        this.provinceCode = provinceCode;
    }
}
