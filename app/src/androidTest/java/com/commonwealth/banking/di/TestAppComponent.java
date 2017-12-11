package com.commonwealth.banking.di;

import com.commonwealth.banking.ActivityTest;

import dagger.Component;


@ActivityScoped
@Component(dependencies = { RepositoryComponent.class }, modules = { SchedulerModule.class })
public interface TestAppComponent extends AppComponent {

    void inject(ActivityTest test);
}
