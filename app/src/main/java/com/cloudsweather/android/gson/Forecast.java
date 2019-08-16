package com.cloudsweather.android.gson;

import com.google.gson.annotations.SerializedName;

/**
 * 未来7日天气预测
 */
public class Forecast {

    @SerializedName("date")
    public String date;

    @SerializedName("cond")
    public Cond cond;

    public class Cond{
        @SerializedName("txt_d")
        public String info;
    }

    @SerializedName("tmp")
    public Temperature temperature;

    public class Temperature{
        @SerializedName("max")
        public String max;

        @SerializedName("min")
        public String min;
    }

}
