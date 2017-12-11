package com.commonwealth.banking.data;

import com.commonwealth.banking.data.model.TransactionData;


import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Single;


@Singleton
public class TransactionRepository implements TransactionDataSource {

    private TransactionDataSource mSource;
    public static final int TIME_OUT = 5;
    private Map<String, TransactionData> cache = new HashMap<>();
    private static final String KEY = "key";

    @Inject
    public TransactionRepository(TransactionDataSource source) {
        mSource = source;
    }

    private boolean mCacheIsDirty = true;

    @Override
    public void refreshData() {
        mCacheIsDirty = true;
    }

    @Override
    public Single<TransactionData> getTransactions() {
        // use real query parameters as key to save and retrieve cached response
        if (cache.get(KEY) != null && !mCacheIsDirty) {
            return Single.just(cache.get(KEY));
        }
        else {
            Single<TransactionData> single = mSource.getTransactions().timeout(TIME_OUT, TimeUnit.SECONDS);
            return single.doAfterSuccess(list -> {
                cache.put(KEY, list);
                mCacheIsDirty = false;
            });
        }
    }
}
