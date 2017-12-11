package com.commonwealth.banking.di;

import com.commonwealth.banking.data.TransactionDataSource;
import com.commonwealth.banking.data.MockTransactionData;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;


@Module
public abstract class RepositoryModule {

    @Singleton
    @Binds
    abstract TransactionDataSource provideTransactionDataSource(MockTransactionData dataSource);
}
