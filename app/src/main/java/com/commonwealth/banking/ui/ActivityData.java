package com.commonwealth.banking.ui;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;


public class ActivityData extends ViewModel {

    public MutableLiveData<TransactionViewModel> transactionViewModel = new MutableLiveData<>();
}
