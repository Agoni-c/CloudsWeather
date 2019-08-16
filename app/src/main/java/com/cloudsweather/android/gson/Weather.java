package com.cloudsweather.android.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 天气数据
 */
public class Weather {

    public String status;

    public Basic basic;

    public AQI aqi;

    public Now now;

    public Suggestion suggestion;

    @SerializedName("daily_forecast")
    public List<Forecast> forecastList;

    @SerializedName("lifestyle")
    public List<Lifestyle> lifestyleList;

    @SerializedName("hourly")
    public List<Forecast_hourly> forecast_hourlyList;

}
