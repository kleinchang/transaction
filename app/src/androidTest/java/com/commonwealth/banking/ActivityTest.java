package com.commonwealth.banking;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.IdlingPolicies;
import android.support.test.espresso.IdlingRegistry;
import android.support.test.espresso.IdlingResource;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.intent.Intents;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.util.Pair;

import com.commonwealth.banking.data.model.Transaction;
import com.commonwealth.banking.data.remote.AppConfig;
import com.commonwealth.banking.di.AppModule;
import com.commonwealth.banking.di.DaggerRepositoryComponent;
import com.commonwealth.banking.di.DaggerTestAppComponent;
import com.commonwealth.banking.di.DataModule;
import com.commonwealth.banking.di.RepositoryComponent;
import com.commonwealth.banking.di.SchedulerModule;
import com.commonwealth.banking.di.TestAppComponent;
import com.commonwealth.banking.ui.MapActivity;
import com.commonwealth.banking.ui.TransactionAdapter;
import com.commonwealth.banking.util.TestUtil;
import static com.commonwealth.banking.util.TestUtil.OUTCOME.SUCCESS;
import static com.commonwealth.banking.util.TestUtil.OUTCOME.FAILURE;
import com.commonwealth.banking.ui.TransactionActivity;
import com.commonwealth.banking.util.scheduler.SchedulerProvider;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.swipeDown;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.not;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ActivityTest {

    MockWebServer server;
    IdlingResource mIdlingResource;


    @Rule
    public ActivityTestRule<TransactionActivity> mRule =
            new ActivityTestRule<>(TransactionActivity.class, true, false);

    @Before
    public void setUp() throws IOException {
        Intents.init();

        server = new MockWebServer();
        server.start();

        TestAppComponent testAppComponent = prepare(server);
        getApplication().setAppComponent(testAppComponent);

        IdlingPolicies.setMasterPolicyTimeout(3, TimeUnit.MINUTES);
        IdlingPolicies.setIdlingResourceTimeout(3, TimeUnit.MINUTES);
    }

    // @Test
    // public void launch_success_then_pullToRefresh_success() throws Exception {
    //     String jsonResponse = TestUtil.getStringFromFile(getApplication().getApplicationContext(),"exercise.json");
    //     server.enqueue(new MockResponse().setResponseCode(200).setBody(jsonResponse).setBodyDelay(2, TimeUnit.SECONDS));

    //     mIdlingResource = launchActivity(mRule);
    //     checkOutcome(SUCCESS);

    //     server.enqueue(new MockResponse().setResponseCode(200).setBody(jsonResponse).setBodyDelay(2, TimeUnit.SECONDS));
    //     onView(withId(R.id.refresh_pulldown)).perform(swipeDown());
    //     checkOutcome(SUCCESS);
    // }

    @Test
    public void launch_success_then_pullToRefresh_fail() throws Exception {
        String jsonResponse = TestUtil.getStringFromFile(getApplication().getApplicationContext(),"exercise.json");
        server.enqueue(new MockResponse().setResponseCode(200).setBody(jsonResponse));

        mIdlingResource = launchActivity(mRule);
        checkOutcome(SUCCESS);

        server.enqueue(new MockResponse().setResponseCode(503).setBody(jsonResponse).setBodyDelay(2, TimeUnit.SECONDS));
        onView(withId(R.id.refresh_pulldown)).perform(swipeDown());
        checkOutcome(FAILURE);
    }

    // @Test
    // public void changeOrientation() throws Exception {

    //     String jsonResponse = TestUtil.getStringFromFile(getApplication().getApplicationContext(),"exercise.json");
    //     server.enqueue(new MockResponse().setResponseCode(200).setBody(jsonResponse));
    //     mIdlingResource = launchActivity(mRule);

    //     server.enqueue(new MockResponse().setResponseCode(200).setBody(jsonResponse));
    //     mRule.getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    //     //checkOutcome(SUCCESS);

    //     server.enqueue(new MockResponse().setResponseCode(200).setBody(jsonResponse));
    //     mRule.getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    //     checkOutcome(SUCCESS);
    // }

    @Test
    public void service_not_available_show_error() {
        server.enqueue(new MockResponse().setResponseCode(503).setBody("Service not available"));

        mIdlingResource = launchActivity(mRule);
        checkOutcome(FAILURE);
    }

    // @Test
    // public void timeout_show_error() throws Exception {
    //     String jsonResponse = TestUtil.getStringFromFile(getApplication().getApplicationContext(),"exercise.json");
    //     server.enqueue(new MockResponse().setResponseCode(200).setBody(jsonResponse).setBodyDelay(10, TimeUnit.SECONDS));

    //     mIdlingResource = launchActivity(mRule);
    //     checkOutcome(FAILURE);
    // }

    @Test
    public void click_ATM_withdrawal_open_map() throws Exception {
        String jsonResponse = TestUtil.getStringFromFile(getApplication().getApplicationContext(),"exercise.json");
        server.enqueue(new MockResponse().setResponseCode(200).setBody(jsonResponse).setBodyDelay(2, TimeUnit.SECONDS));

        mIdlingResource = launchActivity(mRule);
        checkOutcome(SUCCESS);

        List<Integer> listATMIndex = getATMIndex().first;
        if (listATMIndex.size() > 0) {
            int index = listATMIndex.get(0);
            onView(withId(R.id.list)).perform(RecyclerViewActions.scrollToPosition(index));
            onView(withId(R.id.list)).perform(RecyclerViewActions.actionOnItemAtPosition(index, click()));
            intended(hasComponent(MapActivity.class.getName()));
            onView(withId(R.id.map)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        }
    }

    // @Test
    // public void click_not_ATM_withdrawal_nothing_happen() throws Exception {
    //     String jsonResponse = TestUtil.getStringFromFile(getApplication().getApplicationContext(),"exercise.json");
    //     server.enqueue(new MockResponse().setResponseCode(200).setBody(jsonResponse));

    //     mIdlingResource = launchActivity(mRule);
    //     checkOutcome(SUCCESS);

    //     List<Integer> listNonATMIndex = getATMIndex().second;
    //     if (listNonATMIndex.size() > 0) {
    //         int index = listNonATMIndex.get(0);
    //         onView(withId(R.id.list)).perform(RecyclerViewActions.scrollToPosition(index));
    //         onView(withId(R.id.list)).perform(RecyclerViewActions.actionOnItemAtPosition(index, click()));

    //         intended(not(hasComponent(MapActivity.class.getName())));
    //     }
    // }

    @After
    public void tearDown() {
        if (mIdlingResource != null) {
            IdlingRegistry.getInstance().unregister(mIdlingResource);
            mIdlingResource = null;
        }
        Intents.release();
    }

    private Pair<List<Integer>, List<Integer>> getATMIndex() {
        List<Integer> listATMIndex = new ArrayList<>();
        List<Integer> listNonATMIndex = new ArrayList<>();
        TransactionAdapter adapter = mRule.getActivity().getAdapter();
        for (int i=0; i<adapter.getItemCount(); i++) {
            Object object = adapter.getItem(i);
            if (object instanceof Transaction) {
                // assume every atm id is valid
                if (((Transaction) object).getAtmId() != null)
                    listATMIndex.add(i);
                else
                    listNonATMIndex.add(i);
            }
        }
        return new Pair<>(listATMIndex, listNonATMIndex);
    }

    private IdlingResource launchActivity(ActivityTestRule<TransactionActivity> rule) {
        rule.launchActivity(new Intent());
        mIdlingResource = rule.getActivity().getCountingIdlingResource();
        IdlingRegistry.getInstance().register(mIdlingResource);
        return mIdlingResource;
    }

    private void checkOutcome(TestUtil.OUTCOME outcome) {
        boolean successful = outcome == SUCCESS;
        onView(withId(R.id.list)).check(matches(withEffectiveVisibility(successful ? ViewMatchers.Visibility.VISIBLE : ViewMatchers.Visibility.GONE)));
        onView(withId(R.id.progress)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
        onView(withId(R.id.error)).check(matches(withEffectiveVisibility(successful ? ViewMatchers.Visibility.GONE : ViewMatchers.Visibility.VISIBLE)));
    }

    private static App getApplication() {
        return (App) InstrumentationRegistry.getTargetContext().getApplicationContext();
    }

    private static TestAppComponent prepare(MockWebServer server) {

        RepositoryComponent repositoryComponent = DaggerRepositoryComponent.builder()
                .appModule(new AppModule(getApplication()))
                .dataModule(new DataModule() {
                    @Override
                    protected AppConfig provideAppConfig() {
                        return new AppConfig(server.url("/").toString());
                    }
                })
                .build();

        return DaggerTestAppComponent.builder()
                .repositoryComponent(repositoryComponent)
                .schedulerModule(new SchedulerModule(SchedulerProvider.getInstance()))
                .build();
    }
}
