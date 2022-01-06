package com.example.bbpjnjembatan.config;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UtilsApi {
	private static final String API = "http://192.168.1.72/project/perbaikan_jembatan/";
//	private static final String API = "http://10.172.1.11/project/perbaikan_jembatan/";
//	private static final String API = "http://192.168.43.75/project/perbaikan_jembatan/";
	private static final String BASE_URL = API+"index.php/Api/";
	public static final String BASE_IMG_MARKER = API+"assets/upload/foto/";
	public static final String BASE_IMG_API = API+"assets/upload/perbaikan/";
    private static Retrofit retrofit = null;

    public static BaseApiService getAPIService() {
        return getRetrofitInstance().create(BaseApiService.class);
    }

    private static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
            httpClient.addInterceptor(logging);
            httpClient.connectTimeout(120, TimeUnit.SECONDS);
            httpClient.readTimeout(120, TimeUnit.SECONDS);

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(httpClient.build())
                    .build();
        }
        return retrofit;
    }

    public static void CheckPermisiDiAplikasi(Context ctx, Activity act) {
        String[] PERMISSIONS = new String[]{
                Manifest.permission.INTERNET,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.ACCESS_FINE_LOCATION};

        if (!(ActivityCompat.checkSelfPermission(ctx, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) ||
                (ActivityCompat.checkSelfPermission(ctx, Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED) ||
                (ActivityCompat.checkSelfPermission(ctx, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) ||
                (ActivityCompat.checkSelfPermission(ctx, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) ||
                (ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) ||
                (ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_NETWORK_STATE) == PackageManager.PERMISSION_GRANTED) ||
                (ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_WIFI_STATE) == PackageManager.PERMISSION_GRANTED) ||
                (ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)) {

            ActivityCompat.requestPermissions(act, PERMISSIONS, 0);
        }
    }
}
