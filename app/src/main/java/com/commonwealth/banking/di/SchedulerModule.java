package com.commonwealth.banking.di;

import com.commonwealth.banking.util.scheduler.BaseSchedulerProvider;

import dagger.Module;
import dagger.Provides;


@Module
public class SchedulerModule {

    private final BaseSchedulerProvider mScheduler;

    public SchedulerModule(BaseSchedulerProvider scheduler) {
        mScheduler = scheduler;
    }

    @Provides
    BaseSchedulerProvider provideScheduler() {
        return mScheduler;
    }

}