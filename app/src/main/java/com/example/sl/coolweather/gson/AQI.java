package com.example.sl.coolweather.gson;

/**
 * Created by sl on 2018/2/24.
 */

public class AQI {

    /**
     * city : {"aqi":"65","pm25":"47"}
     */

    private AQICity city;

    public AQICity getCity() {
        return city;
    }

    public void setCity(AQICity city) {
        this.city = city;
    }

    public static class AQICity {
        /**
         * aqi : 65
         * pm25 : 47
         */

        private String aqi;
        private String pm25;

        public String getAqi() {
            return aqi;
        }

        public void setAqi(String aqi) {
            this.aqi = aqi;
        }

        public String getPm25() {
            return pm25;
        }

        public void setPm25(String pm25) {
            this.pm25 = pm25;
        }
    }
}
