package com.commonwealth.banking.data.remote;

import com.commonwealth.banking.data.model.TransactionData;


import io.reactivex.Single;
import retrofit2.http.GET;


public interface TransactionAPI {

    @GET("s/tewg9b71x0wrou9/data.json?dl=1")
    Single<TransactionData> getTransactionData();
}