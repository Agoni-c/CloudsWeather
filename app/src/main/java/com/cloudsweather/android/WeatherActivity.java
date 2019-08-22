package com.cloudsweather.android;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.cloudsweather.android.gson.Forecast;
import com.cloudsweather.android.gson.Forecast_hourly;
import com.cloudsweather.android.gson.Weather;
import com.cloudsweather.android.util.HttpUtil;
import com.cloudsweather.android.util.IconUtil;
import com.cloudsweather.android.util.Utility;

import java.io.IOException;

@SuppressLint("NewApi")
public class WeatherActivity extends AppCompatActivity {

    public SwipeRefreshLayout swipeRefresh;
    private String mWeatherId;

    private ImageView weatherInfoImage;
    private ImageView backgroundImage;

    public DrawerLayout drawLayout;
    private ScrollView weatherLayout;

    private TextView title_county;//城市
    private TextView title_update_time;//时间
    private TextView temperatureText;//温度
    private TextView minandmaxText;//当日最低温和最高温
    private TextView weatherInfoText;//天气信息
    private TextView weatherWindText;//风向及风力

    //今日详情
    private ImageView humImage;
    private ImageView presImage;
    private ImageView visImage;
    private ImageView windImage;
    private TextView humInfo;
    private TextView visInfo;
    private TextView presInfo;
    private TextView windsc;
    private TextView winddir;

    //生活指数
    private ImageView comfImage;
    private ImageView cwImage;
    private ImageView drsgImage;
    private ImageView spiImage;
    private ImageView travImage;
    private ImageView sportImage;
    private TextView comfInfo;
    private TextView cwInfo;
    private TextView sportInfo;
    private TextView spiInfo;
    private TextView travInfo;
    private TextView drsgInfo;



    private Toolbar toolbar;

    //AQi，空气质量，PM2.5
    private TextView aqiText;
    private TextView qltyText;
    private TextView pm25Text;

    //生活建议
    private TextView comfortText;
    private TextView carWashText;
    private TextView sportText;

    //未来7日天气预报
    private LinearLayout forecastLayout;
    //24小时天气预报
    private LinearLayout forecastHourlyLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().
                detectDiskWrites().detectNetwork().penaltyLog().build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectLeakedSqlLiteObjects().
                detectLeakedClosableObjects().penaltyLog().penaltyDeath().build());
        super.onCreate(savedInstanceState);
//        if (Build.VERSION.SDK_INT >= 21 ){
//            View decorView = getWindow().getDecorView();
//            decorView.setSystemUiVisibility(
//                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
//            getWindow().setStatusBarColor(Color.TRANSPARENT);
//        }
        setContentView(R.layout.activity_weather);
        androidx.appcompat.widget.Toolbar toolbar = (androidx.appcompat.widget.Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);//让导航按钮显示出来
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);//设置导航按钮图标
        }
        //初始化各控件
        weatherInfoImage = (ImageView) findViewById(R.id.weather_info_image) ;
        backgroundImage = (ImageView) findViewById(R.id.background_img);
        weatherLayout = (ScrollView) findViewById(R.id.weather_layout);
        title_county = (TextView)findViewById(R.id.toolbar_title) ;
        title_update_time = (TextView) findViewById(R.id.title_update_time);
        temperatureText = (TextView) findViewById(R.id.temperature_text);
        weatherInfoText = (TextView) findViewById(R.id.weather_info_text);
        weatherWindText = (TextView) findViewById(R.id.weather_wind);
        minandmaxText = (TextView) findViewById(R.id.minandmax_text);

        humImage = (ImageView) findViewById(R.id.hum_image);
        presImage = (ImageView) findViewById(R.id.pres_image);
        visImage = (ImageView) findViewById(R.id.vis_image);
        windImage = (ImageView) findViewById(R.id.wind_mage);
        humInfo = (TextView) findViewById(R.id.hum_info);
        visInfo = (TextView) findViewById(R.id.vis_info);
        presInfo = (TextView) findViewById(R.id.pres_info);
        windsc = (TextView) findViewById(R.id.wind_sc);
        winddir = (TextView) findViewById(R.id.wind_dir);

        comfImage = (ImageView) findViewById(R.id.comf_image);
        cwImage = (ImageView) findViewById(R.id.cw_image);
        travImage = (ImageView) findViewById(R.id.trav_image);
        spiImage = (ImageView) findViewById(R.id.spi_image);
        drsgImage = (ImageView) findViewById(R.id.drsg_image);
        sportImage = (ImageView) findViewById(R.id.sport_image);
        comfInfo = (TextView) findViewById(R.id.comf_info);
        cwInfo = (TextView) findViewById(R.id.cw_info);
        sportInfo = (TextView) findViewById(R.id.sport_info);
        spiInfo = (TextView) findViewById(R.id.spi_info);
        travInfo = (TextView) findViewById(R.id.trav_info);
        drsgInfo = (TextView) findViewById(R.id.drsg_Info);

        forecastLayout = (LinearLayout) findViewById(R.id.forecast_layout);
        forecastHourlyLayout = (LinearLayout) findViewById(R.id.forecast_hourly_layout);

        qltyText = (TextView) findViewById(R.id.aqi_qlty);
        aqiText = (TextView) findViewById(R.id.aqi_aqi) ;
        pm25Text = (TextView) findViewById(R.id.aqi_pm25);
        comfortText = (TextView) findViewById(R.id.comfort_txt);
        sportText = (TextView) findViewById(R.id.sport_txt);
        carWashText = (TextView) findViewById(R.id.carWash_txt);

        swipeRefresh = findViewById(R.id.refreshLayout);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);


//        SharedPreferences prefs = getSharedPreferences("data",MODE_PRIVATE);
//        String weatherString = prefs.getString("weather",null);
//        if (weatherString != null){
//            //有缓存时直接解析天气数据
//            try {
//                Weather weather = Utility.handleWeatherResponse(weatherString);
//                showWeatherInfo(weather);
//            }catch (Exception e){
//                e.printStackTrace();
//            }
//        }else {
        //无缓冲时去服务器查询天气
        //String weatherId = getIntent().getStringExtra("weather_id");
        mWeatherId = getIntent().getStringExtra("weather_id");
        weatherLayout.setVisibility(View.INVISIBLE);
        requestWeather(mWeatherId);

        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestWeather(mWeatherId);
            }
        });
//        }
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
                drawLayout.openDrawer(GravityCompat.START);
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
//        String weatherUrl1 = "http://guolin.tech/api/weather?cityid="+weatherId+"&key=d2e05f5d675e4be4a1b68951c44e878c";
//        String weatherUrl2 = "https://api.heweather.net/s6/weather/lifestyle?location="+weatherId+"&key=b3f12f4deb964e7289fa38050148e5b5";
//        String weatherUrl3 = "https://api.heweather.net/s6/weather/hourly?location="+weatherId+"&key=b3f12f4deb964e7289fa38050148e5b5";
//        String[] responseTextAll = new String[3];
//        //JSONObject jsonObject = new JSONObject();
//        //Weather weather = new Weather();
//        Gson gson = new Gson();
//        try {
//            responseTextAll[0] =  HttpUtil.sendOkHttpRequest(weatherUrl1).body().string();
//            responseTextAll[1] =  HttpUtil.sendOkHttpRequest(weatherUrl2).body().string();
//            responseTextAll[2] =  HttpUtil.sendOkHttpRequest(weatherUrl3).body().string();
////            jsonObject.put("1",responseTextAll[0]);
////            jsonObject.put("2",responseTextAll[1]);
////            jsonObject.put("3",responseTextAll[2]);
////            JSONObject HeWeather = jsonObject.getJSONObject("1");
////            JSONObject HeWeather6 = jsonObject.getJSONObject("2");
////            JSONArray jsonArray = HeWeather.getJSONArray("HeWeather");
////            JSONArray jsonArray2 = HeWeather6.getJSONArray("HeWeather6");
////            String  weatherContent = jsonArray.getJSONObject(0).toString();
////            String  weatherContent2 = jsonArray2.getJSONObject(0).toString();
////            weather = gson.fromJson(weatherContent,Weather.class);
////            weather = gson.fromJson(weatherContent2,Weather.class);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
////        catch (JSONException e){
////            e.printStackTrace();
////        }
//        Log.e("log",responseTextAll[0] );
//        Log.e("log1",responseTextAll[1] );
//        Log.e("log2",responseTextAll[2] );
//
//        final String responseText =responseTextAll[0] + responseTextAll[1] + responseTextAll[2];
//        //final  String reponseText = weather.toString();
//        //final Weather weather = new Gson().fromJson(responseText,Weather.class);

        String weatherUrl = "https://api.heweather.net/s6/weather?location="+weatherId+"&key=b3f12f4deb964e7289fa38050148e5b5";
        String responseText = null;
        try {
            responseText =  HttpUtil.sendOkHttpRequest(weatherUrl).body().string();
        }catch (IOException e){
            e.printStackTrace();
        }
        final Weather weather = Utility.handleWeatherResponse(responseText);
        if (weather != null && "ok".equals(weather.status)){
            SharedPreferences.Editor editor = getSharedPreferences("data",MODE_PRIVATE).edit();
            editor.putString("weather",responseText);
            editor.apply();
            mWeatherId = weather.basic.weatherId;
            showWeatherInfo(weather);
            Log.e("123", "weather.lifestyleList");
        }else {
            Toast.makeText(WeatherActivity.this,responseText,Toast.LENGTH_SHORT).show();
            Toast.makeText(WeatherActivity.this,"获取天气信息失败",Toast.LENGTH_SHORT).show();
        }
        swipeRefresh.setRefreshing(false);
    }

    /**
     * 处理并显示Weather实体类中的数据
     */
    private void showWeatherInfo(Weather weather){
        String temperature = weather.now.temperature+"℃";
        String weatherInfo = weather.now.txt+" | ";
        String wind = weather.now.wind_direction+weather.now.wind_strength+"级 | ";
        String weatherInfoCode = weather.now.code;

        String todayMax = weather.forecastList.get(0).max;
        String todayMin = weather.forecastList.get(0).min;
        String minandmax = todayMin + "~" + todayMax +"℃";

        int backgroundImageId = IconUtil.getDayBack(weatherInfoCode);
        int weatherInfoImageId = IconUtil.getDayIcon(weatherInfoCode);

        String county = weather.basic.countyName;
        String time = weather.update.updateTime.split(" ")[1];

        humImage.setImageResource(R.drawable.hum);
        visImage.setImageResource(R.drawable.vis);
        presImage.setImageResource(R.drawable.pres);
        windImage.setImageResource(R.drawable.wind);
        humInfo.setText(weather.now.humidity+"%");
        visInfo.setText(weather.now.vis+"千米");
        presInfo.setText(weather.now.pres+"百帕");
        winddir.setText(weather.now.wind_direction);
        windsc.setText(weather.now.wind_strength+"级");

        weatherInfoImage.setImageResource(weatherInfoImageId);
        backgroundImage.setImageResource(backgroundImageId);
        title_county.setText(county);
        title_update_time.setText(time);
        temperatureText.setText(temperature);
        weatherInfoText.setText(weatherInfo);
        weatherWindText.setText(wind);
        minandmaxText.setText(minandmax);
//        if(weather.aqi != null){
//            qltyText.setText(weather.aqi.city.qlty);
//            aqiText.setText(weather.aqi.city.aqi);
//            pm25Text.setText(weather.aqi.city.pm25);
//        }

        cwImage.setImageResource(R.drawable.cw);
        comfImage.setImageResource(R.drawable.comf);
        spiImage.setImageResource(R.drawable.spi);
        sportImage.setImageResource(R.drawable.sport);
        drsgImage.setImageResource(R.drawable.drsg);
        travImage.setImageResource(R.drawable.trav);
        cwInfo.setText(weather.lifestyleList.get(6).brf);
        comfInfo.setText(weather.lifestyleList.get(0).brf);
        sportInfo.setText(weather.lifestyleList.get(3).brf);
        spiInfo.setText(weather.lifestyleList.get(15).brf);
        travInfo.setText(weather.lifestyleList.get(4).brf);
        drsgInfo.setText(weather.lifestyleList.get(1).brf);

        forecastLayout.removeAllViews();
        for(Forecast forecast:weather.forecastList){
            View view = LayoutInflater.from(this).inflate(R.layout.forecast_item,forecastLayout,false);
            TextView infoText = (TextView) view.findViewById(R.id.info_text);
            TextView dateText = (TextView) view.findViewById(R.id.date_text);
            TextView maxText = (TextView) view.findViewById(R.id.max_text);
            TextView minText = (TextView) view.findViewById(R.id.min_text);
            dateText.setText(forecast.date);
            infoText.setText(forecast.info);
            maxText.setText(forecast.max);
            minText.setText(forecast.min);
            forecastLayout.addView(view);
    }

        forecastHourlyLayout.removeAllViews();
        for(Forecast_hourly forecast_hourly:weather.forecast_hourlyList){
            String hourlyInfoCode = forecast_hourly.code;
            int hourlyInfoImageId = IconUtil.getDayIcon(hourlyInfoCode);
            View view = LayoutInflater.from(this).inflate(R.layout.forecast_hourly_item,forecastHourlyLayout,false);
            TextView hourlyInfoText = (TextView) view.findViewById(R.id.hourlyInfo_text);
            TextView hourlyTimeText = (TextView) view.findViewById(R.id.hourlyTime_text);
            TextView hourlyTmpText = (TextView) view.findViewById(R.id.hourlyTmp_text);
            ImageView hourlyInfoImage = (ImageView) view.findViewById(R.id.hourlyInfo_image);
            hourlyInfoImage.setImageResource(hourlyInfoImageId);
            hourlyInfoText.setText(forecast_hourly.hourlyInfo);
            hourlyTimeText.setText(forecast_hourly.time.split(" ")[1]);
            hourlyTmpText.setText(forecast_hourly.tmp+"℃");
            forecastHourlyLayout.addView(view);
        }
        weatherLayout.setVisibility(View.VISIBLE);
    }

}
