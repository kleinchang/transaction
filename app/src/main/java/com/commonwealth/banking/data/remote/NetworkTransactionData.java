package com.commonwealth.banking.data.remote;

import com.commonwealth.banking.data.TransactionDataSource;
import com.commonwealth.banking.data.model.TransactionData;


import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Single;


@Singleton
public class NetworkTransactionData implements TransactionDataSource {

    TransactionAPI mTransactionAPI;

    @Inject
    public NetworkTransactionData(TransactionAPI transactionAPI) {
        mTransactionAPI = transactionAPI;
    }

    @Override
    public void refreshData() {

    }

    @Override
    public Single<TransactionData> getTransactions() {
        return mTransactionAPI.getTransactionData();
    }

}
