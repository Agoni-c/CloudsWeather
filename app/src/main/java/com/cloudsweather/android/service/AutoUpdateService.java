package com.cloudsweather.android.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;

import com.cloudsweather.android.gson.Weather;
import com.cloudsweather.android.util.HttpUtil;
import com.cloudsweather.android.util.Utility;

import java.io.IOException;

public class AutoUpdateService extends Service {
    public AutoUpdateService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
    @Override
    public int onStartCommand(Intent intent,int flags,int startId){
        updataWeather();
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        int anHour = 1 * 60 * 60 * 1000;
        long triggerAtTime = SystemClock.elapsedRealtime() + anHour;
        Intent i = new Intent(this,AutoUpdateService.class);
        PendingIntent pi = PendingIntent.getService(this,0,i,0);
        manager.cancel(pi);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,triggerAtTime,pi);
        return super.onStartCommand(intent,flags,startId);

    }

    /**
     * 更新天气数据
     */
    private void updataWeather(){
        SharedPreferences sharedPreferences = getSharedPreferences("data",MODE_PRIVATE);
        String weatherString = sharedPreferences.getString("weather",null);
        if (weatherString != null) {
            //有缓存时直接解析天气数据
            Weather weather = Utility.handleWeatherResponse(weatherString);
            String weatherId = weather.basic.weatherId;

            String weatherUrl = "https://api.heweather.net/s6/weather?location="+weatherId+"&key=b3f12f4deb964e7289fa38050148e5b5";
            try {
                String responseText = HttpUtil.sendOkHttpRequest(weatherUrl).body().string();
                Weather weather1 = Utility.handleWeatherResponse(responseText);
                if (weather != null && "ok".equals(weather.status)){
                    SharedPreferences.Editor editor = getSharedPreferences("data",MODE_PRIVATE).edit();
                    editor.putString("weather",responseText);
                    editor.apply();
                }
            }catch (IOException e){
                e.printStackTrace();
            }


        }
    }
}
