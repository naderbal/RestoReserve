package com.example.restoreserve.sections.customer.home;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

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
import com.example.restoreserve.sections.customer.home.settings.SettingsFragment;
import com.example.restoreserve.sections.customer.reservations.ReservationsFragment;
import com.example.restoreserve.utils.DateHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import rx.SingleSubscriber;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class HomeActivity extends AppCompatActivity {
    // views
    private AHBottomNavigation bottomNavigation;
    private ViewPager viewPager;
    // fragments
    private RestaurantsListingFragment restaurantsListingFragment;
    private ReservationsFragment reservationsFragment;
    private SettingsFragment settingsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initViews();
        initFragments();
        setupPager();
        subscribeToReservations();
        subscribeToSession();
    }

    private void subscribeToSession() {
        AppSessionManager.getInstance().subscribeToTracker(new Subscriber<AppSessionManager.SessionState>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(AppSessionManager.SessionState sessionState) {
                if (sessionState == AppSessionManager.SessionState.LOGGED_OUT) {
                    setResult(BaseActivity.RESULT_CODE_LOG_OUT);
                    finish();
                }
            }
        });
    }

    private void subscribeToReservations() {
        // cache
        ReservationsManager.getInstance().subscribeToTracker(new Subscriber<Integer>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Integer integer) {
                bottomNavigation.setNotification(String.valueOf(integer), 1);
            }
        });
        // remote
        User user = AppSessionManager.getInstance().getUser();
        if (user != null) {
            ReservationsProvider.rxGetReservationOfUser(user.getId())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new SingleSubscriber<ArrayList<Reservation>>() {
                        @Override
                        public void onSuccess(ArrayList<Reservation> reservations) {
                            ReservationsManager.getInstance().addReservations(handleReservations(reservations));
                        }

                        @Override
                        public void onError(Throwable error) {

                        }
                    });
        }
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
        restaurantsListingFragment = RestaurantsListingFragment.newInstance();
        reservationsFragment = ReservationsFragment.newInstance();
        settingsFragment = SettingsFragment.newInstance();
    }

    private void initViews() {
        bottomNavigation =  findViewById(R.id.bottomNavigation);
        viewPager =  findViewById(R.id.viewPager);

        AHBottomNavigationItem item1 = new AHBottomNavigationItem("Restaurants", R.drawable.ic_home_white_24dp);
        AHBottomNavigationItem item2 = new AHBottomNavigationItem("Reservations", R.drawable.ic_local_library_white_24dp);
        AHBottomNavigationItem item3 = new AHBottomNavigationItem("Settings", R.drawable.ic_settings_white_24dp);

        ArrayList<AHBottomNavigationItem> bottomNavigationItems = new ArrayList<>();

        bottomNavigationItems.add(item1);
        bottomNavigationItems.add(item2);
        bottomNavigationItems.add(item3);

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
        viewPager.setOffscreenPageLimit(2);
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
            return restaurantsListingFragment;
        } else if (position == 1) {
            return reservationsFragment;
        } else {
            return settingsFragment;
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
            return 3;
        }
    }
}
