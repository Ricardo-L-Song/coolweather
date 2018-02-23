package com.example.sl.coolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by sl on 2018/2/24.
 */

public class Forecast {

    /**
     * cond : {"txt_d":"晴间多云"}
     * date : 2018-02-23
     * tmp : {"max":"15","min":"6"}
     */

    @SerializedName("cond")
    private More more;//JSON数据cond对应more字段
    private String date;
    @SerializedName("tmp")
    private Temperature tmperature;

    public More getMore() {
        return more;
    }

    public void setMore(More more) {
        this.more = more;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Temperature getTmperature() {
        return tmperature;
    }

    public void setTmperature(Temperature tmperature) {
        this.tmperature = tmperature;
    }

    public static class More {
        /**
         * txt_d : 晴间多云
         */

        @SerializedName("txt_d")
        private String info;

        public String getInfo() {
            return info;
        }

        public void setInfo(String info) {
            this.info = info;
        }
    }

    public static class Temperature {
        /**
         * max : 15
         * min : 6
         */

        private String max;
        private String min;

        public String getMax() {
            return max;
        }

        public void setMax(String max) {
            this.max = max;
        }

        public String getMin() {
            return min;
        }

        public void setMin(String min) {
            this.min = min;
        }
    }
}
