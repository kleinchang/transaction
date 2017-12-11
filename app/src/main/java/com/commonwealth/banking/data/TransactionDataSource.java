package com.commonwealth.banking.data;

import com.commonwealth.banking.data.model.TransactionData;

import io.reactivex.Single;


public interface TransactionDataSource {

    void refreshData();

    Single<TransactionData> getTransactions();

}
