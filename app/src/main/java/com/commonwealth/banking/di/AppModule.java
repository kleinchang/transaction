package com.commonwealth.banking.di;

import android.content.Context;

import dagger.Module;
import dagger.Provides;


@Module
public final class AppModule {

    private final Context mContext;

    public AppModule(Context context) {
        mContext = context;
    }

    @Provides
    Context provideContext() {
        return mContext;
    }
}