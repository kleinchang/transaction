package com.commonwealth.banking.di;

import android.content.Context;

import com.commonwealth.banking.data.remote.AppConfig;
import com.commonwealth.banking.data.remote.TransactionAPI;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.io.File;
import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
//import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


@Module
public class DataModule {


    @Provides @Singleton
    protected AppConfig provideAppConfig() {
        return new AppConfig(AppConfig.HOSTNAME);
    }

    @Provides @Singleton
    Cache provideHttpCache(Context app) {
        File cacheDir = new File(app.getCacheDir(), "okhttp-cache");
        if (!cacheDir.exists())
            cacheDir.mkdirs();
        return new Cache(cacheDir, AppConfig.CACHE_SIZE);
    }

    @Provides @Singleton
    OkHttpClient provideHttpClient(Cache cache) {
        //HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        //loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.HEADERS);
        return new OkHttpClient.Builder()
                .connectTimeout(20, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                //.addNetworkInterceptor(loggingInterceptor)
                .cache(cache).build();
    }

    @Provides @Singleton
    Retrofit provideRetrofit(AppConfig appConfig, OkHttpClient okHttpClient) {
        return new Retrofit.Builder()
                .baseUrl(appConfig.hostname)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }

    @Provides @Singleton
    TransactionAPI provideTransactionAPIService(Retrofit retrofit) {
        return retrofit.create(TransactionAPI.class);
    }
}
