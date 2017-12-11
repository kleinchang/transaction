package com.commonwealth.banking;

import com.commonwealth.banking.ui.TransactionViewModel.Loading;
import com.commonwealth.banking.data.TransactionRepository;
import com.commonwealth.banking.data.model.ATM;
import com.commonwealth.banking.data.model.Coordinate;
import com.commonwealth.banking.data.model.Transaction;
import com.commonwealth.banking.data.model.TransactionData;
import com.commonwealth.banking.ui.Contract;
import com.commonwealth.banking.ui.TransactionViewModel;
import com.commonwealth.banking.util.scheduler.BaseSchedulerProvider;

import org.junit.Before;
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
import io.reactivex.schedulers.Schedulers;

import static io.reactivex.Single.error;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import static org.mockito.Matchers.anyDouble;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


public class TransactionViewModelTest {

    private static final String ATM_ID = "atm_id";
    private static final double LAT = -33.861382;
    private static final double LNG = 151.210316;

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @Rule
    public TestSchedulerRule testSchedulerRule = new TestSchedulerRule();

    @Mock
    private TransactionRepository mRepository;

    @Mock
    private BaseSchedulerProvider mSchedulerProvider;

    @InjectMocks
    private TransactionViewModel mViewModel;

    @Mock
    Contract.Navigator mNavigator;

    @Mock Transaction mTransaction;
    @Mock ATM mAtm;
    @Mock Coordinate mCoordinate;

    private List<ATM> atmList = new ArrayList<>();

    @Before
    public void setup() {
        when(mSchedulerProvider.computation()).thenReturn(Schedulers.trampoline());
        when(mSchedulerProvider.ui()).thenReturn(Schedulers.trampoline());
    }

    @Test
    public void first_launch_success() {
        when(mRepository.getTransactions()).thenReturn(Single.just(mock(TransactionData.class)).delay(3, TimeUnit.SECONDS));

        mViewModel.loadTransaction(Loading.FIRST_LAUNCH);
        testSchedulerRule.getTestScheduler().advanceTimeBy(2, TimeUnit.SECONDS);

        assertNull(mViewModel.mTransactionData.get());
        assertTrue(mViewModel.dataLoading.get());
        assertFalse(mViewModel.pullToRefresh.get());
        assertFalse(mViewModel.error.get());

        testSchedulerRule.getTestScheduler().advanceTimeBy(2, TimeUnit.SECONDS);

        assertNotNull(mViewModel.mTransactionData.get());
        assertFalse(mViewModel.dataLoading.get());
        assertFalse(mViewModel.pullToRefresh.get());
        assertFalse(mViewModel.error.get());
    }

    @Test
    public void first_launch_fail() {
        when(mRepository.getTransactions()).thenReturn(error(new NoSuchElementException()));

        mViewModel.loadTransaction(Loading.FIRST_LAUNCH);

        assertNull(mViewModel.mTransactionData.get());
        assertFalse(mViewModel.dataLoading.get());
        assertFalse(mViewModel.pullToRefresh.get());
        assertTrue(mViewModel.error.get());
    }

    @Test
    public void first_launch_fail_click_retry_then_success() {
        when(mRepository.getTransactions()).thenReturn(error(new NoSuchElementException()));

        mViewModel.loadTransaction(Loading.FIRST_LAUNCH);

        assertNull(mViewModel.mTransactionData.get());
        assertFalse(mViewModel.dataLoading.get());
        assertFalse(mViewModel.pullToRefresh.get());
        assertTrue(mViewModel.error.get());

        when(mRepository.getTransactions()).thenReturn(Single.just(mock(TransactionData.class)).delay(3, TimeUnit.SECONDS));
        mViewModel.loadTransaction(Loading.RETRY);
        testSchedulerRule.getTestScheduler().advanceTimeBy(2, TimeUnit.SECONDS);

        assertNull(mViewModel.mTransactionData.get());
        assertTrue(mViewModel.dataLoading.get());
        assertFalse(mViewModel.pullToRefresh.get());
        assertFalse(mViewModel.error.get());

        testSchedulerRule.getTestScheduler().advanceTimeBy(2, TimeUnit.SECONDS);

        assertNotNull(mViewModel.mTransactionData.get());
        assertFalse(mViewModel.dataLoading.get());
        assertFalse(mViewModel.pullToRefresh.get());
        assertFalse(mViewModel.error.get());
    }

    @Test
    public void pull_to_refresh_success() {
        when(mRepository.getTransactions()).thenReturn(Single.just(mock(TransactionData.class)).delay(3, TimeUnit.SECONDS));

        mViewModel.loadTransaction(Loading.REFRESH);
        testSchedulerRule.getTestScheduler().advanceTimeBy(2, TimeUnit.SECONDS);

        assertNull(mViewModel.mTransactionData.get());
        assertTrue(mViewModel.dataLoading.get());
        assertTrue(mViewModel.pullToRefresh.get());
        assertFalse(mViewModel.error.get());

        testSchedulerRule.getTestScheduler().advanceTimeBy(2, TimeUnit.SECONDS);

        assertNotNull(mViewModel.mTransactionData.get());
        assertFalse(mViewModel.dataLoading.get());
        assertFalse(mViewModel.dataLoading.get());
        assertFalse(mViewModel.error.get());
    }

    @Test
    public void pull_to_refresh_fail() {
        when(mRepository.getTransactions()).thenReturn(error(new NoSuchElementException()));

        mViewModel.loadTransaction(Loading.REFRESH);

        assertNull(mViewModel.mTransactionData.get());
        assertFalse(mViewModel.dataLoading.get());
        assertFalse(mViewModel.pullToRefresh.get());
        assertTrue(mViewModel.error.get());
    }

    @Test
    public void first_launch_rotate_while_loading_then_success() {
        when(mRepository.getTransactions()).thenReturn(Single.just(mock(TransactionData.class)).delay(3, TimeUnit.SECONDS));

        mViewModel.loadTransaction(Loading.FIRST_LAUNCH);

        testSchedulerRule.getTestScheduler().advanceTimeBy(2, TimeUnit.SECONDS);
        assertNull(mViewModel.mTransactionData.get());
        assertTrue(mViewModel.dataLoading.get());
        assertFalse(mViewModel.pullToRefresh.get());
        assertFalse(mViewModel.error.get());

        mViewModel.subscribe();
        testSchedulerRule.getTestScheduler().advanceTimeBy(2, TimeUnit.SECONDS);

        assertNull(mViewModel.mTransactionData.get());
        assertTrue(mViewModel.dataLoading.get());
        assertFalse(mViewModel.pullToRefresh.get());
        assertFalse(mViewModel.error.get());

        testSchedulerRule.getTestScheduler().advanceTimeBy(2, TimeUnit.SECONDS);

        assertNotNull(mViewModel.mTransactionData.get());
        assertFalse(mViewModel.dataLoading.get());
        assertFalse(mViewModel.pullToRefresh.get());
        assertFalse(mViewModel.error.get());
    }

    @Test
    public void first_launch_rotate_while_loading_then_fail() {

        when(mRepository.getTransactions()).thenReturn(Single.just(mock(TransactionData.class)).delay(3, TimeUnit.SECONDS));
        mViewModel.loadTransaction(Loading.FIRST_LAUNCH);
        testSchedulerRule.getTestScheduler().advanceTimeBy(2, TimeUnit.SECONDS);

        assertNull(mViewModel.mTransactionData.get());
        assertTrue(mViewModel.dataLoading.get());
        assertFalse(mViewModel.pullToRefresh.get());
        assertFalse(mViewModel.error.get());

        // RxJava issue, Single.error doesn't delay
        when(mRepository.getTransactions()).thenReturn(error(new TimeoutException()));
        mViewModel.subscribe();
        testSchedulerRule.getTestScheduler().advanceTimeBy(2, TimeUnit.SECONDS);

        assertNull(mViewModel.mTransactionData.get());
        assertFalse(mViewModel.dataLoading.get());
        assertFalse(mViewModel.pullToRefresh.get());
        assertTrue(mViewModel.error.get());
    }

    @Test
    public void pull_to_refresh_rotate_while_loading_then_success() {
        when(mRepository.getTransactions()).thenReturn(Single.just(mock(TransactionData.class)).delay(3, TimeUnit.SECONDS));

        mViewModel.loadTransaction(Loading.REFRESH);

        testSchedulerRule.getTestScheduler().advanceTimeBy(2, TimeUnit.SECONDS);
        assertNull(mViewModel.mTransactionData.get());
        assertTrue(mViewModel.pullToRefresh.get());
        assertTrue(mViewModel.dataLoading.get());
        assertFalse(mViewModel.error.get());

        mViewModel.subscribe();
        testSchedulerRule.getTestScheduler().advanceTimeBy(2, TimeUnit.SECONDS);

        assertNull(mViewModel.mTransactionData.get());
        assertTrue(mViewModel.pullToRefresh.get());
        assertTrue(mViewModel.dataLoading.get());
        assertFalse(mViewModel.error.get());

        testSchedulerRule.getTestScheduler().advanceTimeBy(2, TimeUnit.SECONDS);

        assertNotNull(mViewModel.mTransactionData.get());
        assertFalse(mViewModel.pullToRefresh.get());
        assertFalse(mViewModel.dataLoading.get());
        assertFalse(mViewModel.error.get());
    }


    @Test
    public void click_ATM_withdrawal_transaction() {
        when(mTransaction.getAtmId()).thenReturn(ATM_ID);

        // a list of 3 ATM items, including one with atm id ATM_ID
        when(mAtm.getId()).thenReturn(ATM_ID);
        atmList.add(mock(ATM.class));
        atmList.add(mAtm);
        atmList.add(mock(ATM.class));

        when(mAtm.getLocation()).thenReturn(mCoordinate);
        when(mCoordinate.getLat()).thenReturn(LAT);
        when(mCoordinate.getLng()).thenReturn(LNG);

        mViewModel.openMapForATMWithdrawal(mTransaction, atmList, mNavigator);
        verify(mNavigator).openMap(LAT, LNG);
    }

    @Test
    public void click_transaction_without_atm_id() {
        when(mTransaction.getAtmId()).thenReturn(null);

        mViewModel.openMapForATMWithdrawal(mTransaction, atmList, mNavigator);
        verify(mNavigator, times(0)).openMap(anyDouble(), anyDouble());
    }

    @Test
    public void click_transaction_atm_data_unavailable() {
        when(mTransaction.getAtmId()).thenReturn(ATM_ID);

        mViewModel.openMapForATMWithdrawal(mTransaction, atmList, mNavigator);
        verify(mNavigator, times(0)).openMap(anyDouble(), anyDouble());
    }

    @Test
    public void click_transaction_no_matching_atm_id() {
        when(mTransaction.getAtmId()).thenReturn(ATM_ID);
        // a list of 10 ATM items
        for (int i=0; i<10; i++)
            atmList.add(mock(ATM.class));

        mViewModel.openMapForATMWithdrawal(mTransaction, atmList, mNavigator);
        verify(mNavigator, times(0)).openMap(anyDouble(), anyDouble());
    }
}
