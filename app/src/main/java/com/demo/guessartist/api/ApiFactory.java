package com.demo.guessartist.api;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class ApiFactory {

    private static ApiFactory apiFactory;
    private static final String BASE_URL = "https://lermontovgallery.ru/spravochnik-antikvariata/top-100-samykh-izvestnykh-russkikh-khudozhnikov/";
    private Retrofit retrofit;

    private ApiFactory(){
        retrofit = new Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(ScalarsConverterFactory.create())
                .baseUrl(BASE_URL)
                .build();

    }

    public static ApiFactory getApiFactory(){
        if (apiFactory == null){
            apiFactory = new ApiFactory();
        }
        return apiFactory;
    }

    public ApiService getApiService(){
        return retrofit.create(ApiService.class);
    }

}