package com.commonwealth.banking.data;

import android.content.Context;

import com.commonwealth.banking.data.model.TransactionData;
import com.commonwealth.banking.util.TestUtil;
import com.google.gson.Gson;

import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Single;


@Singleton
public class MockTransactionData implements TransactionDataSource {

    Context mContext;
    private static final int DELAY = 3;

    @Inject
    public MockTransactionData(Context context) {
        mContext = context;
    }

    @Override
    public void refreshData() {

    }

    @Override
    public Single<TransactionData> getTransactions() {
        try {
            String jsonResponse = TestUtil.getStringFromFile(mContext, "exercise.json");
            TransactionData accountDetail = new Gson().fromJson(jsonResponse, TransactionData.class);
            return Single.just(accountDetail).delay(DELAY, TimeUnit.SECONDS);
        } catch (Exception exp) {
            exp.printStackTrace();
        }

        return Single.error(new NoSuchElementException());
    }

}
