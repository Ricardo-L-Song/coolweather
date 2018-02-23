package com.example.sl.coolweather.gson;

/**
 * Created by sl on 2018/2/24.
 */

public class Now {

    /**
     * cond : {"code":"104","txt":"阴"}
     * fl : 1
     * hum : 69
     * pcpn : 0.0
     * pres : 1022
     * tmp : 7
     * vis : 8
     * wind : {"deg":"136","dir":"东南风","sc":"微风","spd":"8"}
     */

    @com.google.gson.annotations.SerializedName("cond")
    private More more;
    @com.google.gson.annotations.SerializedName("tmp")
    private String tmperature;

    public static class More {
        /**
         * code : 104
         * txt : 阴
         */

        @com.google.gson.annotations.SerializedName("txt")
        private String info;//cond下的txt于info属性建立关系

        public String getInfo() {
            return info;
        }

        public void setInfo(String info) {
            this.info = info;
        }
    }
}
