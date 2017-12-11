package com.commonwealth.banking.ui;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.commonwealth.banking.R;
import com.commonwealth.banking.data.model.Account;
import com.commonwealth.banking.data.model.TransactionData;
import com.commonwealth.banking.data.model.Transaction;
import com.commonwealth.banking.databinding.ViewAccountDataBinding;
import com.commonwealth.banking.databinding.ViewTransactionRecordBinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class TransactionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private TransactionData mTransactionData;
    private Account mAccount = null;
    private int mIndexShift = 0;
    private List<Transaction> mMixedList = new ArrayList<>();

    public TransactionAdapter(TransactionData transactionData) {
        mTransactionData = transactionData;

        mAccount = transactionData.getAccount();
        updateList(transactionData.getPending(), true);
        mMixedList.addAll(transactionData.getPending());
        mMixedList.addAll(transactionData.getTransactions());

        mIndexShift = mAccount == null ? 0 : 1;
        Collections.sort(mMixedList);
        for (int i = 1; i < mMixedList.size(); i++) {
            if (mMixedList.get(i - 1).effectiveDate.equals(mMixedList.get(i).effectiveDate))
                mMixedList.get(i).isSameDate = true;
        }
    }

    private void updateList(List<Transaction> list, boolean isPending) {
        for (Transaction t : list) {
            t.setPending(isPending);
        }
    }

    public TransactionData getTransactionData() {
        return mTransactionData;
    }

    public Object getItem(int position) {
        if (position == 0 && mIndexShift == 1)
            return mAccount;
        else
            return mMixedList.get(position - mIndexShift);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater =
                LayoutInflater.from(parent.getContext());

        if (viewType == VIEW_ACCOUNT_DATA) {
            ViewAccountDataBinding accountBinding =
                    ViewAccountDataBinding.inflate(layoutInflater, parent, false);
            return new AccountViewHolder(accountBinding);
        } else {
            ViewTransactionRecordBinding itemBinding =
                    ViewTransactionRecordBinding.inflate(layoutInflater, parent, false);
            return new TransactionViewHolder(itemBinding);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int shiftedPoistion = position - mIndexShift;
        if (shiftedPoistion < 0) {
            ((AccountViewHolder) holder).mBinding.setAccount(mAccount);
            ((AccountViewHolder) holder).mBinding.executePendingBindings();
        } else {
            Transaction transaction = mMixedList.get(shiftedPoistion);
            ((TransactionViewHolder) holder).mBinding.setTransaction(transaction);
            ((TransactionViewHolder) holder).mBinding.executePendingBindings();
        }
    }

    @Override
    public int getItemCount() {
        return mIndexShift + mMixedList.size();
    }

    public static final int VIEW_ACCOUNT_DATA = R.layout.view_account_data;
    public static final int VIEW_TRANSACTION_RECORD = R.layout.view_transaction_record;

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return VIEW_ACCOUNT_DATA;
        } else {
            return VIEW_TRANSACTION_RECORD;
        }
    }

    static class AccountViewHolder extends RecyclerView.ViewHolder {

        ViewAccountDataBinding mBinding;

        public AccountViewHolder(ViewAccountDataBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }
    }

    static class TransactionViewHolder extends RecyclerView.ViewHolder {

        ViewTransactionRecordBinding mBinding;

        public TransactionViewHolder(ViewTransactionRecordBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }
    }
}
