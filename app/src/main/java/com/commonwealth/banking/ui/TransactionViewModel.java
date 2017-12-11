package com.commonwealth.banking.ui;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;

import com.commonwealth.banking.BR;
import com.commonwealth.banking.data.TransactionRepository;
import com.commonwealth.banking.data.model.ATM;
import com.commonwealth.banking.data.model.TransactionData;
import com.commonwealth.banking.data.model.Transaction;
import com.commonwealth.banking.util.EspressoIdlingResource;
import com.commonwealth.banking.util.scheduler.BaseSchedulerProvider;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;


public class TransactionViewModel extends BaseObservable implements Contract.ViewModel {

    public final ObservableField<TransactionData> mTransactionData = new ObservableField<>();
    public final ObservableBoolean dataLoading = new ObservableBoolean(false);
    public final ObservableBoolean pullToRefresh = new ObservableBoolean(false);
    public final ObservableBoolean error = new ObservableBoolean(false);

    TransactionRepository mRepository;
    BaseSchedulerProvider mSchedulerProvider;
    CompositeDisposable mSubscriptions;

    public enum Loading { FIRST_LAUNCH, RETRY, REFRESH }
    private Loading mType = Loading.FIRST_LAUNCH;


    @Inject
    public TransactionViewModel(TransactionRepository repository, BaseSchedulerProvider schedulerProvider) {
        mRepository = repository;
        mSchedulerProvider = schedulerProvider;
        mSubscriptions = new CompositeDisposable();
    }

    public void subscribe() {
        loadTransaction(mType);
    }

    public void unsubscribe() {
        mSubscriptions.clear();
    }


    public void loadTransaction(Loading source) {
        if (source == Loading.REFRESH || source == Loading.RETRY) {
            mRepository.refreshData();
            mType = source;
        }
        else if (error.get())
            return;

        mSubscriptions.clear();
        Disposable disposable = mRepository.getTransactions()
                .doOnSubscribe(l -> {
                    setLoadingIndicator(true);
                    error.set(false);
                    pullToRefresh.set(source == Loading.REFRESH);
                    mTransactionData.set(null);
                })
                .doOnDispose(this::teardown)
                .subscribeOn(mSchedulerProvider.computation())
                .observeOn(mSchedulerProvider.ui())
                .subscribe(transactions -> { populateResult(transactions); complete(); }, this::error);
        mSubscriptions.add(disposable);
    }

    private void populateResult(TransactionData transactionData) {
        mTransactionData.set(transactionData);
    }

    private void complete() {
        setLoadingIndicator(false);
        resetFlags();
    }

    private void error(Throwable throwable) {
        setLoadingIndicator(false);
        resetFlags();
        error.set(true);
    }

    private void resetFlags() {
        mType = Loading.FIRST_LAUNCH;
        pullToRefresh.set(false);
    }

    private void teardown() {
        if (!EspressoIdlingResource.getIdlingResource().isIdleNow())
            EspressoIdlingResource.decrement();
    }

    @Bindable
    public boolean getShowContent() {
        return !dataLoading.get() && !error.get();
    }

    @Bindable
    public boolean getRefreshing() {
        return dataLoading.get() && pullToRefresh.get();
    }

    @Bindable
    public boolean getLoading() {
        return dataLoading.get() && !pullToRefresh.get();
    }

    @Override
    public void openMapForATMWithdrawal(Transaction t, List<ATM> atmList, Contract.Navigator navigator) {
        if (t.getAtmId() == null || atmList == null)
            return;

        for (ATM atm : atmList) {
            if (t.getAtmId().equals(atm.getId())) {
                navigator.openMap(atm.getLocation().getLat(), atm.getLocation().getLng());
            }
        }
    }

    @Override
    public void setLoadingIndicator(boolean active) {
        if (active)
            EspressoIdlingResource.increment();

        dataLoading.set(active);
        notifyPropertyChanged(BR.refreshing);
        notifyPropertyChanged(BR.loading);
        notifyPropertyChanged(BR.showContent);

        if (!active) {
            try {
                Thread.sleep(100);
            } catch (Exception exp) {
            } finally {
                EspressoIdlingResource.decrement();
            }
        }
    }
}
