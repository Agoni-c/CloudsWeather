package com.cloudsweather.android.gson;

import com.google.gson.annotations.SerializedName;

/**
 * 未来7日天气预测
 */
public class Forecast {

    @SerializedName("date")
    public String date;

    @SerializedName("cond_code_d")
    public String cond_code_n;


    @SerializedName("cond_txt_d")
    public String info;


    @SerializedName("tmp_max")
    public String max;

    @SerializedName("tmp_min")
    public String min;

}
