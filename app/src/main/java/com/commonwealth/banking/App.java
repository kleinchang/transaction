package com.commonwealth.banking;

import android.app.Application;
import android.support.annotation.VisibleForTesting;

import com.commonwealth.banking.di.AppComponent;
import com.commonwealth.banking.di.AppModule;
import com.commonwealth.banking.di.DaggerRepositoryComponent;
import com.commonwealth.banking.di.DaggerAppComponent;
import com.commonwealth.banking.di.RepositoryComponent;
import com.commonwealth.banking.di.SchedulerModule;
import com.commonwealth.banking.util.scheduler.SchedulerProvider;


public class App extends Application {

    private AppComponent mAppComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        RepositoryComponent repositoryComponent = DaggerRepositoryComponent.builder()
                .appModule(new AppModule(this))
                .build();
        mAppComponent = DaggerAppComponent.builder()
                        .repositoryComponent(repositoryComponent)
                        .schedulerModule(new SchedulerModule(SchedulerProvider.getInstance()))
                        .build();
    }

    public AppComponent appComponent() {
        return mAppComponent;
    }

    @VisibleForTesting
    public void setAppComponent(AppComponent appComponent) {
        mAppComponent = appComponent;
    }

}
