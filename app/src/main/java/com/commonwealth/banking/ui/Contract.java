package com.commonwealth.banking.ui;

import com.commonwealth.banking.data.model.ATM;
import com.commonwealth.banking.data.model.Transaction;
import com.commonwealth.banking.ui.TransactionViewModel.Loading;

import java.util.List;


public interface Contract {

    interface Navigator {

        void openMap(double lat, double lng);
    }

    interface ViewModel {

        void openMapForATMWithdrawal(Transaction t, List<ATM> atmList, Navigator navigator);

        void setLoadingIndicator(boolean active);

        void subscribe();

        void unsubscribe();

        void loadTransaction(Loading type);
    }

}
