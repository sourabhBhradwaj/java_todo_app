package com.youngtechie.todoapp.networkClient;
import static android.content.Context.MODE_PRIVATE;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitService {
    private static String BASE_URL = "http://192.168.1.6:3000/";
    private static RetrofitService retrofitService;
    private static Retrofit retrofit;


    private RetrofitService(Context context) {
        Gson gson = new GsonBuilder().setLenient().create();
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.level(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.addInterceptor(interceptor);


        // getting token
        SharedPreferences entity = context.getSharedPreferences("user_todo",MODE_PRIVATE);
        String token = entity.getString("token", "");
        Log.d("", "RetrofitService: " + token);
        // adding interceptor  for token and print the response in debug
        OkHttpClient okHttpClient = builder.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request newRequest = chain.request().newBuilder()
                        .addHeader("Authorization",  token)
                        .build();
                return chain.proceed(newRequest);
            }
        }).build();
        retrofit = new Retrofit.Builder().addConverterFactory(GsonConverterFactory.create()).baseUrl(BASE_URL).client(okHttpClient
        ).build();
    }



    public static synchronized RetrofitService getInstance(Context context) {
        if (retrofitService == null) {
            retrofitService = new RetrofitService(context);
        }
        return retrofitService;
    }

    public ApiRepository getApi() {
        return retrofit.create(ApiRepository.class);
    }

}
