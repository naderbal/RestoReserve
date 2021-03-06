package com.example.restoreserve.sections.restaurant;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.example.restoreserve.R;
import com.example.restoreserve.base.BaseActivity;
import com.example.restoreserve.base.BaseFragment;
import com.example.restoreserve.data.reservations.ReservationsManager;
import com.example.restoreserve.data.reservations.ReservationsProvider;
import com.example.restoreserve.data.reservations.model.Reservation;
import com.example.restoreserve.data.session.AppSessionManager;
import com.example.restoreserve.data.user.User;
import com.example.restoreserve.sections.restaurant.events.RestaurantEventsFragment;
import com.example.restoreserve.sections.restaurant.reservations.RestaurantReservationsFragment;
import com.example.restoreserve.sections.restaurant.settings.RestaurantSettingsFragment;
import com.example.restoreserve.sections.restaurant.statistics.RestaurantStatisticsFragment;
import com.example.restoreserve.sections.restaurant.tables.RestaurantTablesFragment;
import com.example.restoreserve.utils.DateHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import rx.SingleSubscriber;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 *
 */

public class RestaurantHomeActivity extends BaseActivity {
    // views
    private AHBottomNavigation bottomNavigation;
    private ViewPager viewPager;
    // fragments
    private RestaurantReservationsFragment reservationsFragment;
    private RestaurantTablesFragment tablesFragment;
    private RestaurantStatisticsFragment statisticsFragment;
    private RestaurantEventsFragment eventsFragment;
    private RestaurantSettingsFragment settingsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_home);
        initViews();
        initFragments();
        setupPager();
        subscribeToSession();
    }

    private void subscribeToSession() {
        AppSessionManager.getInstance().subscribeToTracker(new Subscriber<AppSessionManager.SessionState>() {
            @Override
            public void onCompleted() {}

            @Override
            public void onError(Throwable e) {}

            @Override
            public void onNext(AppSessionManager.SessionState sessionState) {
                if (sessionState == AppSessionManager.SessionState.LOGGED_OUT) {
                    setResult(BaseActivity.RESULT_CODE_LOG_OUT);
                    finish();
                }
            }
        });
    }

    /**
     * Remove reservations of before current time (security check)
     */
    private ArrayList<Reservation> handleReservations(ArrayList<Reservation> reservations) {
        ArrayList<Reservation> newReservations = new ArrayList<>();
        for (Reservation reservation : reservations) {
            final String strDate = reservation.getDate();
            final Date reservationDate = DateHelper.parseDate(strDate, DateHelper.PATTERN_API_DATE);

            Calendar calendar = Calendar.getInstance();
            final Date currentTime = calendar.getTime();
            final String strCurrentDate = DateHelper.formatDate(currentTime);
            final Date currentDate = DateHelper.parseDate(strCurrentDate, DateHelper.PATTERN_API_DATE);
            if (currentDate != null) {
                if (currentDate.before(reservationDate)) {
                    // date before reservation
                    newReservations.add(reservation);
                } else if (currentDate.equals(reservationDate)) {
                    // date after reservation
                    final String time = reservation.getTime();
                    Date reservationTime = DateHelper.parseTime(time);

                    final String strCurrentTime = DateHelper.formatTime(currentTime);
                    Date currentTime2 = DateHelper.parseTime(strCurrentTime);
                    if (currentTime2 != null && currentTime2.before(reservationTime)) {
                        newReservations.add(reservation);
                    }
                }
            }
        }
        return newReservations;
    }

    private void initFragments() {
        reservationsFragment = RestaurantReservationsFragment.newInstance();
        tablesFragment = RestaurantTablesFragment.newInstance();
        statisticsFragment = RestaurantStatisticsFragment.newInstance();
        eventsFragment = RestaurantEventsFragment.newInstance();
        settingsFragment = RestaurantSettingsFragment.newInstance();
    }

    private void initViews() {
        bottomNavigation =  findViewById(R.id.bottomNavigation);
        viewPager =  findViewById(R.id.viewPager);

        AHBottomNavigationItem item1 = new AHBottomNavigationItem("Reservations", R.drawable.ic_local_library_white_24dp);
        AHBottomNavigationItem item2 = new AHBottomNavigationItem("Tables", R.drawable.ic_local_dining_white_24dp);
        AHBottomNavigationItem item3 = new AHBottomNavigationItem("Statistics", R.drawable.ic_insert_chart_white_24dp);
        AHBottomNavigationItem item4 = new AHBottomNavigationItem("Events", R.drawable.ic_event_white_24dp);
        AHBottomNavigationItem item5 = new AHBottomNavigationItem("Settings", R.drawable.ic_settings_white_24dp);

        ArrayList<AHBottomNavigationItem> bottomNavigationItems = new ArrayList<>();

        bottomNavigationItems.add(item1);
        bottomNavigationItems.add(item2);
        bottomNavigationItems.add(item3);
        bottomNavigationItems.add(item4);
        bottomNavigationItems.add(item5);

        bottomNavigation.setAccentColor(ContextCompat.getColor(getBaseContext(), R.color.colorAccent));
        bottomNavigation.setTitleState(AHBottomNavigation.TitleState.ALWAYS_SHOW);
        bottomNavigation.setBehaviorTranslationEnabled(false);
        bottomNavigation.addItems(bottomNavigationItems);

        bottomNavigation.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener() {
            @Override
            public boolean onTabSelected(int position, boolean wasSelected) {
                viewPager.setCurrentItem(position, false);
                return true;
            }
        });
        bottomNavigation.setCurrentItem(0);
    }

    private void setupPager() {
        SectionsPagerAdapter pagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        viewPager.setOffscreenPageLimit(3);
        viewPager.setAdapter(pagerAdapter);
        // set view pager item as current section
        viewPager.setCurrentItem(0, false);
    }

    /**
     * Returns the fragment at the given position.
     */
    @NonNull
    private BaseFragment getFragmentAtPosition(int position) {
        if (position == 0) {
            return reservationsFragment;
        } else if (position == 1){
            return tablesFragment;
        } else if (position == 2){
            return statisticsFragment;
        } else if (position == 3){
            return eventsFragment;
        } else {
            return settingsFragment;
        }
    }

    @Override
    public void onBackPressed() {
        if (viewPager.getCurrentItem() != 0) {
            viewPager.setCurrentItem(0);
            bottomNavigation.setCurrentItem(0);
        } else {
            super.onBackPressed();
        }
    }

    /**
     * A {@link FragmentStatePagerAdapter} that returns
     * a fragment corresponding to one of the sections/tabs/pages.
     */
    private class SectionsPagerAdapter extends FragmentPagerAdapter {
        private SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return getFragmentAtPosition(position);
        }

        @Override
        public int getCount() {
            return 5;
        }
    }
}
