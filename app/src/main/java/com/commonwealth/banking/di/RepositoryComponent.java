package com.commonwealth.banking.di;

import com.commonwealth.banking.data.TransactionRepository;

import javax.inject.Singleton;

import dagger.Component;


@Singleton
@Component(modules = { RepositoryModule.class, AppModule.class, DataModule.class })
public interface RepositoryComponent {

    TransactionRepository provideRepository();
}
