package com.commonwealth.banking.data.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TransactionData {

    Account account;
    List<Transaction> transactions;
    List<Transaction> pending;

    @SerializedName("atms")
    List<ATM> atmList;

    public Account getAccount() {
        return account;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public List<Transaction> getPending() {
        return pending;
    }

    public List<ATM> getAtms() {
        return atmList;
    }
}
