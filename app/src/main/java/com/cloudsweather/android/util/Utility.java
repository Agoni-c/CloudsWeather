package com.cloudsweather.android.util;

import android.text.TextUtils;

import com.cloudsweather.android.database.City;
import com.cloudsweather.android.database.County;
import com.cloudsweather.android.database.Province;
import com.cloudsweather.android.gson.Weather;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Utility {
    /**
     *解析和处理服务器返回的省级数据
     */
    public static boolean handleProvinceResponse(String response){
        if(!TextUtils.isEmpty(response)){
            try{
                JSONArray allProvince = new JSONArray(response);
                for (int i = 0; i < allProvince.length(); i++){
                    JSONObject provinceObject = allProvince.getJSONObject(i);
                    Province province = new Province();
                    province.setProvinceName(provinceObject.getString("name"));
                    province.setProvinceCode(provinceObject.getInt("id"));
                    province.save();
                }
                return true;
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
        return false;
    }
    /**
     *解析和处理服务器返回的市级数据
     */
    public static boolean handleCityResponse(String response,int provinceId){
        if(!TextUtils.isEmpty(response)){
            try{
                JSONArray allCities = new JSONArray(response);
                for (int i = 0; i < allCities.length(); i++){
                    JSONObject cityObject = allCities.getJSONObject(i);
                    City city = new City();
                    city.setCityName(cityObject.getString("name"));
                    city.setCityCode(cityObject.getInt("id"));
                    city.setProvinceId(provinceId);
                    city.save();
                }
                return true;
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
        return false;
    }
    /**
     *解析和处理服务器返回的县级数据
     */
    public static boolean handleCountyResponse(String response,int cityId){
        if(!TextUtils.isEmpty(response)){
            try{
                JSONArray allCounties = new JSONArray(response);
                for (int i = 0; i < allCounties.length(); i++){
                    JSONObject countyObject = allCounties.getJSONObject(i);
                    County county = new County();
                    county.setCountyName(countyObject.getString("name"));
                    county.setWeatherId(countyObject.getString("weather_id"));
                    county.setCityId(cityId);
                    county.save();
                }
                return true;
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     *将返回的JSON数据解析成Weather实体类
     */

    public static Weather handleWeatherResponse(String responseText){
        try{
            JSONObject jsonObject = new JSONObject(responseText);
            JSONArray jsonArray = jsonObject.getJSONArray("HeWeather6");
            String  weatherContent = jsonArray.getJSONObject(0).toString();
            return new Gson().fromJson(weatherContent,Weather.class);
//            JSONObject jsonObject = new JSONObject(responseText);
//            HashMap<String,Object> map = new HashMap<>();
//            String heWeather = jsonObject.getString("HeWeather");
//            String heWeather6 = jsonObject.getString("HeWeather6");
//            map.put("HeWeather",heWeather);
//            map.put("HeWeather6",heWeather6);
//            Log.d("testt", map.get("HeWeather").toString());

//            JSONObject jsonObject = new JSONObject(responseText);
//            JSONArray jsonArray = jsonObject.getJSONArray("HeWeather");
//            String  weatherContent = jsonArray.getJSONObject(0).toString();
//            return new Gson().fromJson(weatherContent,Weather.class);

//            JSONObject jsonObject = new JSONObject(responseText);
//            JSONArray jsonArray1 = jsonObject.getJSONArray("HeWeather");
//            JSONArray jsonArray2 = jsonObject.getJSONArray("HeWeather6");
//            String  weatherContent1 = jsonArray1.getJSONObject(0).toString();
//            String  weatherContent2 = jsonArray2.getJSONObject(0).toString();
//            String weatherContent = weatherContent1+weatherContent2;
//            return new Gson().fromJson(weatherContent,Weather.class);

//            JSONArray jsonArray = new JSONArray(responseText);
//            JSONObject jsonObject1 = jsonArray.getJSONObject(0);
//            Log.e("weatherInform", jsonObject1.toString ());
//            JSONObject jsonObject2 = jsonArray.getJSONObject(1);
//            JSONObject jsonObject3 = jsonArray.getJSONObject(2);
//            JSONArray jsonArray1 = jsonObject1.getJSONArray("HeWeather");
//            String weatherContent1 = jsonArray1.getJSONObject(0).toString();
//            String weatherContent2 = jsonObject2.getString("hourly");
//            String weatherContent3 = jsonObject3.getString("lifestyle");
//            String weatherContent = weatherContent1+weatherContent2+weatherContent3;
//            return new Gson().fromJson(weatherContent,Weather.class);

        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

}
