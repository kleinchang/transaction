package com.commonwealth.banking.data.model;


import com.commonwealth.banking.util.Utils;

public class Account {

    private String accountName;
    private String accountNumber;
    private String available;
    private String balance;

    public String getAccountName() {
        return accountName;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getAvailable() {
        return Utils.formatDollar(available);
    }

    public String getBalance() {
        return Utils.formatDollar(balance);
    }
}
