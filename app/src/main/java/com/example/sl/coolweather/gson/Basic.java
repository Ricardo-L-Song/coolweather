package com.example.sl.coolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by sl on 2018/2/24.
 */
//当返回的JSON数据 结构比较复杂时 可以将一个实体类拆分成多个实体类
//这里将返回的Basic JSON数据转换成实体类
public class Basic {

    /**
     * city : 常州
     * cnty : 中国
     * id : CN101191101
     * lat : 31.77275276
     * lon : 119.94697571
     * update : {"loc":"2018-02-23 23:49","utc":"2018-02-23 15:49"}
     */

    @SerializedName("city")
    private String cityName;
    @SerializedName("id")
    private String weatherId;
    private Update update;

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getWeatherId() {
        return weatherId;
    }

    public void setWeatherId(String weatherId) {
        this.weatherId = weatherId;
    }

    public Update getUpdate() {
        return update;
    }

    public void setUpdate(Update update) {
        this.update = update;
    }

    public static class Update {
        /**
         * loc : 2018-02-23 23:49
         * utc : 2018-02-23 15:49
         */

        @SerializedName("loc")
        private String updateTime;

        public String getUpdateTime() {
            return updateTime;
        }

        public void setUpdateTime(String updateTime) {
            this.updateTime = updateTime;
        }
    }
}
