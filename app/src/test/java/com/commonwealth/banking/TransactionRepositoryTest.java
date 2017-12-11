package com.commonwealth.banking;

import com.commonwealth.banking.data.TransactionDataSource;
import com.commonwealth.banking.data.TransactionRepository;
import com.commonwealth.banking.data.model.Transaction;
import com.commonwealth.banking.data.model.TransactionData;

import junit.framework.Assert;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import io.reactivex.Single;
import io.reactivex.observers.TestObserver;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


public class TransactionRepositoryTest {

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @Rule public TestSchedulerRule testSchedulerRule = new TestSchedulerRule();

    @Mock
    TransactionDataSource mSource;

    @InjectMocks
    TransactionRepository mRepository;

    @Mock
    TransactionData mTransactionData;

    public static final int TRANSACTION_LIST_SIZE = 10;
    public static final int TIME_OUT = 5;


    @Test
    public void load_data_success() throws Exception {

        List<Transaction> transactions = new ArrayList<>();
        for (int i=0; i<TRANSACTION_LIST_SIZE; i++)
            transactions.add(mock(Transaction.class));
        when(mTransactionData.getTransactions()).thenReturn(transactions);
        when(mSource.getTransactions()).thenReturn(Single.just(mTransactionData).delay(3, TimeUnit.SECONDS));

        TestObserver<TransactionData> testObserver = mRepository.getTransactions().test();

        testSchedulerRule.getTestScheduler().advanceTimeBy(2, TimeUnit.SECONDS);
        Assert.assertEquals(0, testObserver.values().size());

        testSchedulerRule.getTestScheduler().advanceTimeBy(2, TimeUnit.SECONDS);
        Assert.assertEquals(1, testObserver.values().size());
        Assert.assertEquals(TRANSACTION_LIST_SIZE, testObserver.values().get(0).getTransactions().size());
    }

    @Test
    public void load_data_success_then_rotate_load_cache() throws Exception {

        List<Transaction> transactions = new ArrayList<>();
        for (int i=0; i<TRANSACTION_LIST_SIZE; i++)
            transactions.add(mock(Transaction.class));
        when(mTransactionData.getTransactions()).thenReturn(transactions);
        when(mSource.getTransactions()).thenReturn(Single.just(mTransactionData).delay(3, TimeUnit.SECONDS));

        TestObserver<TransactionData> testObserver = mRepository.getTransactions().test();

        testSchedulerRule.getTestScheduler().advanceTimeBy(4, TimeUnit.SECONDS);
        verify(mSource, times(1)).getTransactions();
        Assert.assertEquals(1, testObserver.values().size());
        Assert.assertEquals(TRANSACTION_LIST_SIZE, testObserver.values().get(0).getTransactions().size());

        // load cache this time
        testObserver = mRepository.getTransactions().test();
        testSchedulerRule.getTestScheduler().advanceTimeBy(1, TimeUnit.SECONDS);
        verify(mSource, times(1)).getTransactions();
        Assert.assertEquals(1, testObserver.values().size());
        Assert.assertEquals(TRANSACTION_LIST_SIZE, testObserver.values().get(0).getTransactions().size());
    }

    @Test
    public void load_data_success_then_refresh_force_reload() throws Exception {

        List<Transaction> transactions = new ArrayList<>();
        for (int i=0; i<TRANSACTION_LIST_SIZE; i++)
            transactions.add(mock(Transaction.class));
        when(mTransactionData.getTransactions()).thenReturn(transactions);
        when(mSource.getTransactions()).thenReturn(Single.just(mTransactionData).delay(3, TimeUnit.SECONDS));

        TestObserver<TransactionData> testObserver = mRepository.getTransactions().test();

        testSchedulerRule.getTestScheduler().advanceTimeBy(4, TimeUnit.SECONDS);
        verify(mSource, times(1)).getTransactions();
        Assert.assertEquals(1, testObserver.values().size());
        Assert.assertEquals(TRANSACTION_LIST_SIZE, testObserver.values().get(0).getTransactions().size());

        mRepository.refreshData(); // pull to refresh
        testObserver = mRepository.getTransactions().test();
        testSchedulerRule.getTestScheduler().advanceTimeBy(4, TimeUnit.SECONDS);
        verify(mSource, times(2)).getTransactions();
        Assert.assertEquals(1, testObserver.values().size());
        Assert.assertEquals(TRANSACTION_LIST_SIZE, testObserver.values().get(0).getTransactions().size());
    }

    @Test
    public void load_data_fail() throws Exception {
        when(mSource.getTransactions()).thenReturn(Single.error(new NoSuchElementException()));

        TestObserver<TransactionData> testObserver = mRepository.getTransactions().test();

        testObserver.assertError(NoSuchElementException.class);
        Assert.assertEquals(0, testObserver.values().size());
    }

    @Test
    public void load_data_timeout() throws Exception {
        when(mSource.getTransactions())
                .thenReturn(Single.just(mock(TransactionData.class))
                .delay(10, TimeUnit.SECONDS));

        TestObserver<TransactionData> testObserver = mRepository.getTransactions().test();

        testSchedulerRule.getTestScheduler().advanceTimeBy(TIME_OUT, TimeUnit.SECONDS);
        testObserver.assertError(TimeoutException.class);
    }
}
