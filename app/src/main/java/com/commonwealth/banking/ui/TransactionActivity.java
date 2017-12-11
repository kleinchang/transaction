package com.commonwealth.banking.ui;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.support.annotation.VisibleForTesting;
import android.support.test.espresso.IdlingResource;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.commonwealth.banking.App;
import com.commonwealth.banking.R;
import com.commonwealth.banking.ui.TransactionViewModel.Loading;
import com.commonwealth.banking.data.model.TransactionData;
import com.commonwealth.banking.data.model.Transaction;
import com.commonwealth.banking.databinding.ActivityTransactionBinding;
import com.commonwealth.banking.util.EspressoIdlingResource;
import com.commonwealth.banking.util.RecyclerItemClickListener;

import javax.inject.Inject;

public class TransactionActivity extends AppCompatActivity implements Contract.Navigator {

    @Inject
    TransactionViewModel mInjectViewModel;

    TransactionViewModel mViewModel;

    private ActivityTransactionBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_transaction);

        ActivityData retainedData = ViewModelProviders.of(this).get(ActivityData.class);
        if (retainedData.transactionViewModel.getValue() == null) {
            ((App) getApplication()).appComponent().inject(this);
            retainedData.transactionViewModel.setValue(mInjectViewModel);
        }
        mViewModel = retainedData.transactionViewModel.getValue();

        mBinding.setViewModel(mViewModel);
        mBinding.list.setLayoutManager(new LinearLayoutManager(this));
        mBinding.list.addOnItemTouchListener(
                new RecyclerItemClickListener(this, ((view, position) -> clickTransactionHistory(mBinding.list, position)))
        );
        mBinding.setRetryListener(v -> mViewModel.loadTransaction(Loading.RETRY));
        mBinding.refreshPulldown.setOnRefreshListener(() -> mViewModel.loadTransaction(Loading.REFRESH));

        mViewModel.subscribe();
    }

    @BindingAdapter({"bind:transaction_data"})
    public static void setTransactionData(RecyclerView recyclerView, TransactionData transactionData) {
        if (transactionData != null) {
            TransactionAdapter adapter = new TransactionAdapter(transactionData);
            recyclerView.setAdapter(adapter);
            mAdapter = adapter;
        }
    }

    private void clickTransactionHistory(RecyclerView recyclerView, int position) {
        TransactionAdapter adapter = (TransactionAdapter) recyclerView.getAdapter();
        TransactionData mTransactionData = adapter.getTransactionData();
        Object obj = adapter.getItem(position);
        if (obj instanceof Transaction) {
            mViewModel.openMapForATMWithdrawal((Transaction) obj, mTransactionData.getAtms(), this);
        }
    }

    @Override
    public void openMap(double lat, double lng) {
        Intent mapIntent = new Intent(getApplicationContext(), MapActivity.class);
        mapIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mapIntent.putExtra(MapActivity.LATITUDE, lat);
        mapIntent.putExtra(MapActivity.LONGITUDE, lng);
        getApplicationContext().startActivity(mapIntent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mViewModel.unsubscribe();
    }

    @VisibleForTesting
    public IdlingResource getCountingIdlingResource() {
        return EspressoIdlingResource.getIdlingResource();
    }


    private static TransactionAdapter mAdapter;

    @VisibleForTesting
    public TransactionAdapter getAdapter() {
        return mAdapter;
    }
}
