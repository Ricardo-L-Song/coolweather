package com.example.sl.coolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by sl on 2018/2/24.
 */

public class Suggestion {

    /**
     * comf : {"txt":"今天夜间会有降雨，这种天气条件下，人们会感到有些凉意，但大部分人完全可以接受。"}
     * cw : {"txt":"不宜洗车，未来24小时内有雨，如果在此期间洗车，雨水和路上的泥水可能会再次弄脏您的爱车。"}
     * sport : {"txt":"有降水，且风力较强，推荐您在室内进行各种健身休闲运动；若坚持户外运动，请注意防风保暖。"}
     */

    @SerializedName("comf")
    private Comf comfort;
    @SerializedName("cw")
    private CarWash carWash;
    private Sport sport;

    public Comf getComfort() {
        return comfort;
    }

    public void setComfort(Comf comfort) {
        this.comfort = comfort;
    }

    public CarWash getCarWash() {
        return carWash;
    }

    public void setCarWash(CarWash carWash) {
        this.carWash = carWash;
    }

    public Sport getSport() {
        return sport;
    }

    public void setSport(Sport sport) {
        this.sport = sport;
    }

    public static class Comf {
        /**
         * txt : 今天夜间会有降雨，这种天气条件下，人们会感到有些凉意，但大部分人完全可以接受。
         */

        @SerializedName("txt")
        private String info;

        public String getInfo() {
            return info;
        }

        public void setInfo(String info) {
            this.info = info;
        }
    }

    public static class CarWash {
        /**
         * txt : 不宜洗车，未来24小时内有雨，如果在此期间洗车，雨水和路上的泥水可能会再次弄脏您的爱车。
         */

        @SerializedName("txt")
        private String info;

        public String getInfo() {
            return info;
        }

        public void setInfo(String info) {
            this.info = info;
        }
    }

    public static class Sport {
        /**
         * txt : 有降水，且风力较强，推荐您在室内进行各种健身休闲运动；若坚持户外运动，请注意防风保暖。
         */

        @SerializedName("txt")
        private String info;

        public String getInfo() {
            return info;
        }

        public void setInfo(String info) {
            this.info = info;
        }
    }
}
