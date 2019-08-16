package com.cloudsweather.android;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.cloudsweather.android.gson.Forecast;
import com.cloudsweather.android.gson.Lifestyle;
import com.cloudsweather.android.gson.Weather;
import com.cloudsweather.android.util.HttpUtil;
import com.cloudsweather.android.util.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
@SuppressLint("NewApi")
public class WeatherActivity extends AppCompatActivity {

    private DrawerLayout mDrawLayout;
    private ScrollView weatherLayout;

    private TextView title_county;//城市

    private TextView title_update_time;//时间
    private TextView temperatureText;//温度
    private TextView weatherInfoText;//天气信息
    private TextView weatherWinfindText;//风向及风力

    private Toolbar toolbar;

    private LinearLayout forecastLayout;//未来几日天气预报

    //AQi，空气质量，PM2.5
    private TextView aqiText;
    private TextView qltyText;
    private TextView pm25Text;

    //生活建议
    private TextView comfortText;
    private TextView carWashText;
    private TextView sportText;

    //生活指数
    private LinearLayout lifestyleLayout;

    //24小时天气预报
    private LinearLayout forecastHourlyLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        androidx.appcompat.widget.Toolbar toolbar = (androidx.appcompat.widget.Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mDrawLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);//让导航按钮显示出来
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);//设置导航按钮图标
        }
        //初始化各控件
        weatherLayout = (ScrollView) findViewById(R.id.weather_layout);
        title_county = (TextView)findViewById(R.id.title_county) ;
        title_update_time = (TextView) findViewById(R.id.title_update_time);
        temperatureText = (TextView) findViewById(R.id.temperature_text);
        weatherInfoText = (TextView) findViewById(R.id.weather_info_text);
        weatherWinfindText = (TextView) findViewById(R.id.weather_wind);
        forecastLayout = (LinearLayout) findViewById(R.id.forecast_layout);
        lifestyleLayout = (LinearLayout) findViewById(R.id.lifestyle_layout);
        qltyText = (TextView) findViewById(R.id.aqi_qlty);
        aqiText = (TextView) findViewById(R.id.aqi_aqi) ;
        pm25Text = (TextView) findViewById(R.id.aqi_pm25);
        comfortText = (TextView) findViewById(R.id.comfort_txt);
        sportText = (TextView) findViewById(R.id.sport_txt);
        carWashText = (TextView) findViewById(R.id.carWash_txt);
        SharedPreferences prefs = getSharedPreferences("data",MODE_PRIVATE);
        String weatherString = prefs.getString("weather",null);
        if (weatherString != null){
            //有缓存时直接解析天气数据
            try {
                Weather weather = Utility.handleWeatherResponse(weatherString);
                showWeatherInfo(weather);
            }catch (Exception e){
                e.printStackTrace();
            }
        }else {
            //无缓冲时去服务器查询天气
            String weatherId = getIntent().getStringExtra("weather_id");
            weatherLayout.setVisibility(View.INVISIBLE);
            requestWeather(weatherId);
        }


    }
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.toolbar,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem){
        switch (menuItem.getItemId()){
            case R.id.share:
                Toast.makeText(this,"分享",Toast.LENGTH_SHORT).show();
                break;
            case android.R.id.home:
                mDrawLayout.openDrawer(GravityCompat.START);
                break;
            default:
                break;
        }
        return true;
    }
    /**
     * 根据天气id请求城市天气信息
     */
    public void requestWeather(final String weatherId){
        String weatherUrl1 = "http://guolin.tech/api/weather?cityid="+weatherId+"&key=d2e05f5d675e4be4a1b68951c44e878c";
        HttpUtil.sendOkHttpRequest(weatherUrl1, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this,"获取天气失败",Toast.LENGTH_SHORT).show();
                    }
                });
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                final Weather weather = Utility.handleWeatherResponse(responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (weather != null && "ok".equals(weather.status)){
                            SharedPreferences.Editor editor = getSharedPreferences("data",MODE_PRIVATE).edit();
                            editor.putString("weather",responseText);
                            editor.apply();
                            showWeatherInfo(weather);
                        }else {
                            Toast.makeText(WeatherActivity.this,"获取天气信息失败",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

    }

    /**
     * 处理并显示Weather实体类中的数据
     */
    private void showWeatherInfo(Weather weather){
        String temperature = weather.now.temperature+"℃";
        String weatherInfo = weather.now.txt+" | ";
        String county = weather.basic.countyName;
        String time = weather.basic.update.updateTime.split(" ")[1];
        String wind = weather.now.wind_direction+weather.now.wind_strength+"级 | ";
        title_county.setText(county);
        title_update_time.setText(time);
        temperatureText.setText(temperature);
        weatherInfoText.setText(weatherInfo);
        weatherWinfindText.setText(wind);

        if(weather.aqi != null){
            qltyText.setText(weather.aqi.city.qlty);
            aqiText.setText(weather.aqi.city.aqi);
            pm25Text.setText(weather.aqi.city.pm25);
        }
        String comfort = "舒适指数:" + weather.suggestion.comfort.index;
        String carWash = "洗车指数:" + weather.suggestion.carWash.index;
        String sport = "运动指数：" + weather.suggestion.sport.index;
        comfortText.setText(comfort);
        carWashText.setText(carWash);
        sportText.setText(sport);
        forecastLayout.removeAllViews();
        for(Forecast forecast:weather.forecastList){
            View view = LayoutInflater.from(this).inflate(R.layout.forecast_item,forecastLayout,false);
            TextView infoText = (TextView) view.findViewById(R.id.info_text);
            TextView dateText = (TextView) view.findViewById(R.id.date_text);
            TextView maxText = (TextView) view.findViewById(R.id.max_text);
            TextView minText = (TextView) view.findViewById(R.id.min_text);
            dateText.setText(forecast.date);
            infoText.setText(forecast.cond.info);
            maxText.setText(forecast.temperature.max);
            minText.setText(forecast.temperature.min);
            forecastLayout.addView(view);
        }
        lifestyleLayout.removeAllViews();
        for(Lifestyle lifestyle:weather.lifestyleList){
            View view = LayoutInflater.from(this).inflate(R.layout.lifestyle,lifestyleLayout,false);
            TextView type = (TextView) findViewById(R.id.lifestyle_type);
            TextView brf = (TextView) findViewById(R.id.lifestyle_brf);
            TextView text = (TextView) findViewById(R.id.lifestyle_text);
            type.setText(lifestyle.type);
            brf.setText(lifestyle.brf);
            text.setText(lifestyle.text);
            lifestyleLayout.addView(view);
        }

        weatherLayout.setVisibility(View.VISIBLE);
    }

}
