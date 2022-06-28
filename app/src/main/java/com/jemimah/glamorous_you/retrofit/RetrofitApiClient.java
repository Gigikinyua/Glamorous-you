package com.jemimah.glamorous_you.retrofit;

import android.content.Context;

import com.jemimah.glamorous_you.utility.Constants;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitApiClient {
    private static final String TAG = "RetrofitApiClient";

    private static RetrofitApiClient client;

    public static final String base_url = Constants.BASE_URL;

    public static Context contextx;

    private final HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY);


    OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .connectTimeout(1, TimeUnit.MINUTES)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .addInterceptor(httpLoggingInterceptor)
            .addInterceptor(chain -> {
                Request request = chain.request();
                Request.Builder requestBuilder = request.newBuilder()
                        .addHeader("UUID", Constants.UUID);
                request = requestBuilder.build();
                return chain.proceed(request);
            })
            .build();

    Retrofit retrofit = new Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(base_url)
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    public static synchronized RetrofitApiClient getInstance(Context context) {
        if (client == null) {
            client = new RetrofitApiClient();
        }
        contextx = context;

        return client;
    }

    public ApiClient getApi() {
        return retrofit.create(ApiClient.class);
    }

}
