package com.cloudsweather.android.util;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HttpUtil {
    public static Response sendOkHttpRequest(String address){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(address).build();
        okhttp3.Response response = null;
        try {
            response = client.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }
}
