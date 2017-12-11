package com.commonwealth.banking.di;

import com.commonwealth.banking.ui.TransactionActivity;

import dagger.Component;


@ActivityScoped
@Component(dependencies = { RepositoryComponent.class }, modules = { SchedulerModule.class })
public interface AppComponent {

    void inject(TransactionActivity activity);
}
