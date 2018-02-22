package com.example.sl.coolweather.util;

import android.text.TextUtils;

import com.example.sl.coolweather.db.Province;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by sl on 2018/2/22.
 */
//处理服务器返回的Json数据的工具类（形参形参形参）
public class Utility {
        //解析处理服务器返回的省级数据
    public static boolean handleProvinceResponse(String response){
        if (!TextUtils.isEmpty(response)) {//CharSequence类型的参数不为null或"" （TextView的一个非空判断）
            try{
                JSONArray allProvince=new JSONArray(response);//将返回的所有省级数据变成allProvince的JSON数组
                for (int i = 0; i <allProvince.length() ; i++) {
                    //for语句里面的循环操作 取出第一个省的数据 第二个省的数据 以此类推
                    JSONObject provinceObject=allProvince.getJSONObject(i);//取出所有省里面第i+1个省Object对象的数据 索引为i
                    //JSONObject对象中有id，name等数据，具体看服务器返回什么数据
                    Province province=new Province();//这里实体类对象的创建保证各个省级数据的差异性/不同
                    province.setProvinceName(provinceObject.getString("name"));//取出第i+1省Object对象的name数据,封装成实体类方便进行数据库操作
                    province.setProvinceCode(provinceObject.getInt("id"));//取出第i+1个省Object对象的id数据，封装成实体类方便数据库操作
                    province.save();//实体类对象继承自DataSupport，可以进行CRUD操作，这里进行增操作
                }
                return true;//处理了省级数据
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
        return false;//未处理省级数据
    }
    //解析处理服务器返回的省级数据
    public static boolean handleCityResponse(String response,int provinceId){

    }
}
